package com.bsi.manajement_magang.modules.data_mahasiswa;

import com.bsi.manajement_magang.enums.Gender;
import com.bsi.manajement_magang.modules.data_mahasiswa.DataMahasiswaRepository;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.request.StudentRequest;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.request.UpdateStudentRequest;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response.StudentResponse;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response.StudentStatResponse;
import com.bsi.manajement_magang.shared.Argon2Hasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DataMahasiswaService {
    private final DataMahasiswaRepository repository;
    private final Argon2Hasher argon2Hasher;

    public DataMahasiswaService(DataMahasiswaRepository repository, Argon2Hasher argon2Hasher) {
        this.repository = repository;
        this.argon2Hasher = argon2Hasher;
    }

    // Add new student

    @Transactional
    public StudentResponse addStudent(StudentRequest req) {
        // Validate uniqueness
        if (repository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email '" + req.email() + "' is already registered");
        }
        if (repository.existsByNim(req.nim())) {
            throw new IllegalArgumentException("NIM '" + req.nim() + "' is already registered");
        }

        UUID userId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        // Hash password
        String hashedPassword = argon2Hasher.hash(req.password());

        // Resolve university name to id (find or create)
        Long idUniversity = req.idUniversity();

        // Save User
        repository.saveUser(userId, req.email(), hashedPassword);

        // Save Student Profile
        repository.saveMahasiswa(
                studentId,
                userId,
                req.nim(),
                req.nama(),
                req.noHp() != null ? req.noHp() : "-",
                req.gender() != null ? req.gender().getValue() : null,
                idUniversity
        );

        // Save Period if dates are provided
        if (req.tanggalMulai() != null && req.tanggalBerakhir() != null) {
            UUID periodId = UUID.randomUUID();
            repository.savePeriod(
                    periodId,
                    studentId,
                    req.tanggalMulai(),
                    req.tanggalBerakhir(),
                    req.periodeStatus() != null ? req.periodeStatus() : "aktif"
            );
        }

        return repository.findStudentDetailById(studentId)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve registered student details"));
    }

    // Edit existing student details & period

    @Transactional
    public StudentResponse editStudent(UUID id, UpdateStudentRequest req) {
        // Find existing student
        StudentResponse student = repository.findStudentDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID '" + id + "' was not found"));

        // Validate email uniqueness if changed
        if (req.email() != null && !req.email().equalsIgnoreCase(student.email())) {
            if (repository.existsByEmailAndIdNot(req.email(), student.userId())) {
                throw new IllegalArgumentException("Email '" + req.email() + "' is already registered to another user");
            }
            repository.updateUserEmail(student.userId(), req.email());
        }

        // Validate NIM uniqueness if changed
        if (req.nim() != null && !req.nim().equalsIgnoreCase(student.nim())) {
            if (repository.existsByNimAndIdNot(req.nim(), student.id())) {
                throw new IllegalArgumentException("NIM '" + req.nim() + "' is already registered to another student");
            }
        }

        Long resolvedIdUniversity = req.idUniversity();
        if (resolvedIdUniversity == null) {
            String currentUniversitas = student.universitas();
            resolvedIdUniversity = (currentUniversitas != null && !currentUniversitas.trim().isEmpty())
                ? repository.findUniversityIdByName(currentUniversitas).orElse(null)
                : null;
        }

        // Update Student Profile
        String resolvedNama = req.nama() != null ? req.nama() : student.nama();
        String resolvedNim = req.nim() != null ? req.nim() : student.nim();
        String resolvedNoHp = req.noHp() != null ? req.noHp() : student.noHp();
        Gender resolvedGenderEnum = req.gender() != null ? req.gender() : student.gender();
        String resolvedGender = resolvedGenderEnum != null ? resolvedGenderEnum.getValue() : null;

        repository.updateMahasiswa(
                student.id(),
                resolvedNim,
                resolvedNama,
                resolvedNoHp,
                resolvedGender,
                resolvedIdUniversity
        );

        // Handle nested Period update
        if (req.periode() != null) {
            Optional<Map<String, Object>> latestPeriodOpt = repository.findLatestPeriodByStudentId(student.id());

            LocalDate tanggalMulai = req.periode().tanggalMulai();
            LocalDate tanggalBerakhir = req.periode().tanggalBerakhir();
            String status = req.periode().status() != null ? req.periode().status().getValue() : null;

            if (latestPeriodOpt.isPresent()) {
                Map<String, Object> latestPeriod = latestPeriodOpt.get();
                UUID periodId = (UUID) latestPeriod.get("id");

                // Fallbacks to existing values if not specified in request
                LocalDate resolvedStart = tanggalMulai != null ? tanggalMulai : ((java.sql.Date) latestPeriod.get("tanggal_mulai")).toLocalDate();
                LocalDate resolvedEnd = tanggalBerakhir != null ? tanggalBerakhir : ((java.sql.Date) latestPeriod.get("tanggal_berakhir")).toLocalDate();
                String resolvedStatus = status != null ? status : (String) latestPeriod.get("status");

                repository.updatePeriod(periodId, resolvedStart, resolvedEnd, resolvedStatus);
            } else {
                // Save new period if none exists but dates are provided in request
                if (tanggalMulai != null && tanggalBerakhir != null) {
                    repository.savePeriod(
                            UUID.randomUUID(),
                            student.id(),
                            tanggalMulai,
                            tanggalBerakhir,
                            status != null ? status : "aktif"
                    );
                }
            }
        }

        return repository.findStudentDetailById(student.id())
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated student details"));
    }

    // List all students
    public List<StudentResponse> listStudents(String gender, String universitas, String status) {
        return repository.listStudents(gender, universitas, status);
    }

    // Detail student by ID
    public StudentResponse getStudentDetail(UUID id) {
        return repository.findStudentDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID '" + id + "' was not found"));
    }

    // Get statistics
    public StudentStatResponse getStudentStatistics(String gender, String universitas) {
        return repository.getStudentStatistics(gender, universitas);
    }

    // Get Sisa Waktu Magang
    public Long getSisaWaktuMagang(UUID userId) {
        return repository.getSisaWaktuMagangByUserId(userId);
    }
}
