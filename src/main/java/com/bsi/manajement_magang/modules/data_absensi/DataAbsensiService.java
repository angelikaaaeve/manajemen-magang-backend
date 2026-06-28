package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiHarianMentorResponse;
import com.bsi.manajement_magang.shared.DomainException;
import com.bsi.manajement_magang.shared.PaginatedResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DataAbsensiService {
    private final DataAbsensiRepository repository;

    public DataAbsensiService(DataAbsensiRepository repository) {
        this.repository = repository;
    }

    // ========================================================
    // MENTOR-SIDE FEATURES
    // ========================================================

    /**
     * Paginated list SEMUA mahasiswa bimbingan yang periode magangnya mencakup tanggal tertentu.
     * Status: hadir/izin/sakit jika sudah ada record, "alpha" jika belum.
     */
    public PaginatedResponse<AbsensiHarianMentorResponse> listAbsensiHarianMentor(
            LocalDate tanggal, int index, int size) {
        int limit = size;
        int offset = (index - 1) * size;
        List<AbsensiHarianMentorResponse> data =
            repository.listAbsensiHarianMentor(tanggal, limit, offset);
        long total = repository.countAbsensiHarianMentor(tanggal);
        return PaginatedResponse.success(data, total, index, size);
    }

    /**
     * Mentor mencatat absensi untuk mahasiswa. Semua mentor bisa catat absensi semua mahasiswa.
     */
    @Transactional
    public AbsensiResponse submitAbsensiByMentor(UUID mentorUserId, UUID mahasiswaId,
                                                 String status, LocalDate tanggal,
                                                 String attachmentUrl) {
        String statusLower = status != null ? status.toLowerCase().trim() : "";
        if (!List.of("hadir", "izin", "sakit").contains(statusLower)) {
            throw DomainException.invalidValue("status",
                "'" + status + "' tidak valid. Pilihan: hadir, izin, sakit");
        }

        UUID mentorId = repository.findMentorIdByUserId(mentorUserId)
            .orElseThrow(() -> DomainException.notFound("Data mentor tidak ditemukan"));

        UUID periodeMagangId = repository.findActivePeriodeByMahasiswaId(mahasiswaId)
            .orElseThrow(() -> DomainException.conflict(
                "Mahasiswa tidak memiliki periode magang aktif. Absensi tidak dapat dilakukan."));

        if (repository.existsByPeriodeAndTanggal(periodeMagangId, tanggal)) {
            throw DomainException.conflict(
                "Absensi untuk tanggal " + tanggal + " sudah tercatat.");
        }

        UUID newId = repository.insertAbsensi(periodeMagangId, mahasiswaId, mentorId,
                                              tanggal, statusLower, attachmentUrl);

        return repository.findById(newId)
            .orElseThrow(() -> DomainException.internalError("Gagal mengambil record absensi setelah insert."));
    }

    // ========================================================
    // SHARED / ADMIN FEATURES
    // ========================================================

    public PaginatedResponse<AbsensiResponse> listAbsensi(String status, String namaMahasiswa,
                                                          int index, int size) {
        int limit = size;
        int offset = (index - 1) * size;
        List<AbsensiResponse> data = repository.listAbsensi(status, namaMahasiswa, limit, offset);
        long total = repository.countAbsensi(status, namaMahasiswa);
        return PaginatedResponse.success(data, total, index, size);
    }

    @Transactional
    public AbsensiResponse verifikasiAbsensi(UUID id, String action, UUID mentorUserId) {
        repository.findById(id)
            .orElseThrow(() -> DomainException.notFound("Absensi dengan ID '" + id + "' tidak ditemukan"));
        if ("setujui".equalsIgnoreCase(action)) {
            UUID mentorId = repository.findMentorIdByUserId(mentorUserId)
                .orElseThrow(() -> DomainException.notFound("Data mentor tidak ditemukan"));
            repository.updateMentorId(id, mentorId);
        } else if ("tolak".equalsIgnoreCase(action)) {
            repository.deleteAbsensi(id);
            return null;
        }
        return repository.findById(id)
            .orElseThrow(() -> DomainException.internalError("Gagal mengambil record setelah verifikasi"));
    }

    @Transactional
    public void deleteAbsensi(UUID id) {
        repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Attendance record with ID '" + id + "' was not found"));
        repository.deleteAbsensi(id);
    }

    public AbsensiStatResponse getAbsensiStatistics(String namaMahasiswa) {
        return repository.getAbsensiStatistics(namaMahasiswa);
    }

    public String getAttachmentUrl(UUID id) {
        AbsensiResponse record = repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Attendance record with ID '" + id + "' was not found"));

        String url = record.attachmentUrl();
        if (url == null || url.trim().isEmpty() || url.equalsIgnoreCase("-")) {
            throw DomainException.notFound("No attachment file is uploaded for this attendance record");
        }
        return url;
    }

    public String exportRekapAbsensi(String status, String namaMahasiswa) {
        List<AbsensiResponse> records = repository.listAbsensi(status, namaMahasiswa, Integer.MAX_VALUE, 0);

        StringBuilder csv = new StringBuilder();
        csv.append('﻿');
        csv.append("No;Tanggal;NIM;Nama Mahasiswa;Status Presensi;URL Lampiran\n");

        int index = 1;
        for (AbsensiResponse record : records) {
            String attachment = record.attachmentUrl() != null ? record.attachmentUrl() : "-";
            csv.append(index++).append(";")
               .append(record.tanggal()).append(";")
               .append(record.nim()).append(";")
               .append(escapeCsvField(record.namaMahasiswa())).append(";")
               .append(record.status().getValue().toUpperCase()).append(";")
               .append(attachment).append("\n");
        }

        return csv.toString();
    }

    public List<Object[]> getRekapAbsensi(LocalDate startDate, LocalDate endDate, UUID mahasiswaId) {
        List<com.bsi.manajement_magang.modules.data_absensi.schemas.response.RekapAbsensiResponse> records = 
            repository.getRekapAbsensi(startDate, endDate, mahasiswaId);
        
        return records.stream()
            .map(r -> new Object[]{r.namaMahasiswa(), r.tanggal(), r.status()})
            .collect(java.util.stream.Collectors.toList());
    }

    public List<com.bsi.manajement_magang.modules.data_absensi.schemas.response.RekapDetailAbsensiResponse> getRekapDetailAbsensi(LocalDate startDate, LocalDate endDate, UUID mahasiswaId) {
        return repository.getRekapDetailAbsensi(startDate, endDate, mahasiswaId);
    }

    // ========================================================
    // MAHASISWA-SIDE FEATURES
    // ========================================================

    @Transactional
    public AbsensiResponse submitAbsensi(UUID userId, String status, String attachmentUrl) {
        String statusLower = status != null ? status.toLowerCase().trim() : "";
        if (!List.of("hadir", "izin", "sakit").contains(statusLower)) {
            throw DomainException.invalidValue("status",
                    "'" + status + "' tidak valid. Pilihan: hadir, izin, sakit");
        }

        UUID mahasiswaId = repository.findMahasiswaIdByUserId(userId)
            .orElseThrow(() -> DomainException.notFound("Data mahasiswa tidak ditemukan"));

        UUID periodeMagangId = repository.findActivePeriodeByUserId(userId)
                .orElseThrow(() -> DomainException.conflict(
                        "Mahasiswa tidak memiliki periode magang aktif. Absensi tidak dapat dilakukan."));

        LocalDate today = LocalDate.now();
        if (repository.existsByPeriodeAndTanggal(periodeMagangId, today)) {
            throw DomainException.conflict(
                    "Absensi untuk hari ini (" + today + ") sudah tercatat.");
        }

        UUID newId = repository.insertAbsensi(periodeMagangId, mahasiswaId, null,
                                              today, statusLower, attachmentUrl);

        return repository.findById(newId)
                .orElseThrow(() -> DomainException.internalError("Gagal mengambil record absensi setelah insert."));
    }

    public PaginatedResponse<AbsensiResponse> getRiwayatAbsensi(UUID userId, int index, int size) {
        int limit = size;
        int offset = (index - 1) * size;
        List<AbsensiResponse> data = repository.listAbsensiByUserId(userId, limit, offset);
        long total = repository.countAbsensiByUserId(userId);
        return PaginatedResponse.success(data, total, index, size);
    }

    public AbsensiMahasiswaStatResponse getMahasiswaStat(UUID userId) {
        return repository.getMahasiswaStat(userId);
    }

    public Long getTotalKehadiran(UUID userId) {
        return repository.getTotalKehadiran(userId);
    }

    // ========================================================
    // Helpers
    // ========================================================

    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(";") || field.contains(",") || field.contains("\"")
                || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
