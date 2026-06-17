package com.bsi.manajement_magang.modules.dashboard_mentor;

import com.bsi.manajement_magang.enums.Gender;
import com.bsi.manajement_magang.enums.StatusPeriode;
import com.bsi.manajement_magang.modules.dashboard_mentor.DashboardMentorRepository;
import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.request.RegisterStudentRequest;
import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response.DashboardStatResponse;
import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response.SearchStudentResponse;
import com.bsi.manajement_magang.shared.Argon2Hasher;
import com.bsi.manajement_magang.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DashboardMentorService {
    private final DashboardMentorRepository repository;
    private final Argon2Hasher argon2Hasher;

    public DashboardMentorService(DashboardMentorRepository repository, Argon2Hasher argon2Hasher) {
        this.repository = repository;
        this.argon2Hasher = argon2Hasher;
    }

    // 1. Register new student (daftarkan mahasiswa baru)

    @Transactional
    public SearchStudentResponse addStudent(RegisterStudentRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw DomainException.conflict("Email '" + req.email() + "' is already registered");
        }
        if (repository.existsByNim(req.nim())) {
            throw DomainException.conflict("NIM '" + req.nim() + "' is already registered");
        }

        UUID userId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        // Hash raw password
        String hashedPassword = argon2Hasher.hash(req.password());

        // Save records to database
        repository.saveUser(userId, req.email(), hashedPassword);
        repository.saveMahasiswa(
                studentId,
                userId,
                req.nim(),
                req.nama(),
                req.noHp(),
                req.gender() != null ? req.gender().getValue() : null,
                req.universitas()
        );

        // Save period record if both dates are specified
        if (req.tanggalMulai() != null && req.tanggalBerakhir() != null) {
            repository.savePeriod(UUID.randomUUID(), studentId, req.tanggalMulai(), req.tanggalBerakhir());
        }

        // Return the created student details
        return repository.findStudentById(studentId)
                .orElseThrow(() -> DomainException.internalError("Failed to retrieve newly registered student details"));
    }

    // 2. Query / Search students by name
    public List<SearchStudentResponse> searchStudentsByName(String name) {
        List<Map<String, Object>> rows = repository.searchStudentsByName(name);
        List<SearchStudentResponse> results = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            results.add(new SearchStudentResponse(
                    toUUID(row.get("id")),
                    toUUID(row.get("user_id")),
                    (String) row.get("email"),
                    (String) row.get("nim"),
                    (String) row.get("nama"),
                    (String) row.get("no_hp"),
                    row.get("gender") != null ? Gender.fromString((String) row.get("gender")) : null,
                    (String) row.get("universitas"),
                    toUUID(row.get("periode_id")),
                    toLocalDate(row.get("tanggal_mulai")),
                    toLocalDate(row.get("tanggal_berakhir")),
                    row.get("status_periode") != null ? StatusPeriode.fromString((String) row.get("status_periode")) : null
            ));
        }

        return results;
    }

    // 3. Get Dashboard Statistics (Active Count, Completed Count, and Attendance Breakdown)
    public DashboardStatResponse getDashboardStats() {
        long activeCount = repository.countActiveStudents();
        long completedCount = repository.countCompletedStudents();
        Map<String, Long> attendanceMap = repository.getAttendanceAccumulation();

        return new DashboardStatResponse(activeCount, completedCount, attendanceMap);
    }

    // Robust type converters for PostgreSQL result maps
    private UUID toUUID(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof UUID) {
            return (UUID) obj;
        }
        return UUID.fromString(obj.toString());
    }

    private LocalDate toLocalDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toLocalDate();
        }
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        }
        return LocalDate.parse(obj.toString());
    }
}
