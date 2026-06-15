package com.bsi.manajement_magang.modules.data_absensi.service.impl;

import com.bsi.manajement_magang.modules.data_absensi.repository.DataAbsensiRepository;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.service.DataAbsensiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class DataAbsensiServiceImpl implements DataAbsensiService {
    private final DataAbsensiRepository repository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public DataAbsensiServiceImpl(DataAbsensiRepository repository) {
        this.repository = repository;
    }

    // List all attendance records with filters
    @Override
    public List<AbsensiResponse> listAbsensi(String status, String namaMahasiswa) {
        return repository.listAbsensi(status, namaMahasiswa);
    }

    // Delete attendance record
    @Override
    @Transactional
    public void deleteAbsensi(UUID id) {
        AbsensiResponse record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record with ID '" + id + "' was not found"));

        repository.deleteAbsensi(id);
    }

    // Get attendance statistics
    @Override
    public AbsensiStatResponse getAbsensiStatistics(String namaMahasiswa) {
        return repository.getAbsensiStatistics(namaMahasiswa);
    }

    // Get attachment URL (lihat Surat Keterangan)
    @Override
    public String getAttachmentUrl(UUID id) {
        AbsensiResponse record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record with ID '" + id + "' was not found"));

        String url = record.attachmentUrl();
        if (url == null || url.trim().isEmpty() || url.equalsIgnoreCase("-")) {
            throw new IllegalArgumentException("No attachment file is uploaded for this attendance record");
        }
        return url;
    }

    // Export Rekap Absensi to Excel-compatible CSV
    @Override
    public String exportRekapAbsensi(String status, String namaMahasiswa) {
        List<AbsensiResponse> records = repository.listAbsensi(status, namaMahasiswa);

        StringBuilder csv = new StringBuilder();
        // CSV BOM (Byte Order Mark) so Excel recognizes UTF-8 characters and semicolons correctly
        csv.append('﻿');

        // CSV Header
        csv.append("No;Tanggal;NIM;Nama Mahasiswa;Status Presensi;URL Lampiran\n");

        int index = 1;
        for (AbsensiResponse record : records) {
            String attachment = record.attachmentUrl() != null ? record.attachmentUrl() : "-";

            csv.append(index++).append(";")
               .append(record.tanggal()).append(";")
               .append(record.nim()).append(";")
               .append(escapeCsvField(record.namaMahasiswa())).append(";")
               .append(record.status().toUpperCase()).append(";")
               .append(attachment).append("\n");
        }

        return csv.toString();
    }

    // ========================================================
    // MAHASISWA-SIDE METHODS
    // ========================================================

    /**
     * Submit absensi harian mahasiswa.
     * - hadir   : tidak butuh file
     * - izin    : wajib ada keterangan, file PDF/image optional (maks 10MB)
     * - sakit   : wajib ada keterangan, file PDF/image optional (maks 10MB)
     * Satu mahasiswa hanya boleh submit 1x per hari (UNIQUE constraint di DB).
     *
     * @param userId       user_id mahasiswa yang login
     * @param status       "hadir" | "izin" | "sakit"
     * @param keterangan   alasan/keterangan (untuk izin & sakit)
     * @param attachmentUrl key media dokumen pendukung (nullable, hasil upload via modul media)
     */
    @Override
    @Transactional
    public AbsensiResponse submitAbsensi(UUID userId, String status,
                                         String keterangan, String attachmentUrl) {
        // 1. Validasi status
        String statusLower = status != null ? status.toLowerCase().trim() : "";
        if (!List.of("hadir", "izin", "sakit").contains(statusLower)) {
            throw new IllegalArgumentException(
                "Status tidak valid: '" + status + "'. Pilihan: hadir, izin, sakit");
        }

        // 2. Cari periode magang aktif
        UUID periodeMagangId = repository.findActivePeriodeByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                    "Mahasiswa tidak memiliki periode magang aktif. Absensi tidak dapat dilakukan."));

        // 3. Cek duplikat – 1 absensi per hari
        LocalDate today = LocalDate.now();
        if (repository.existsByPeriodeAndTanggal(periodeMagangId, today)) {
            throw new IllegalStateException(
                "Absensi untuk hari ini (" + today + ") sudah tercatat.");
        }

        // 4. Insert ke DB
        UUID newId = repository.insertAbsensi(periodeMagangId, today, statusLower, keterangan, attachmentUrl);

        // 5. Kembalikan record yang baru dibuat
        return repository.findById(newId)
                .orElseThrow(() -> new IllegalStateException("Gagal mengambil record absensi setelah insert."));
    }

    /**
     * Riwayat absensi 30 hari terakhir milik mahasiswa (berdasarkan user_id).
     */
    @Override
    public List<AbsensiResponse> getRiwayatAbsensi(UUID userId) {
        return repository.listAbsensiByUserId(userId);
    }

    /**
     * Statistik absensi mahasiswa: hadir, izin, sakit, alfa.
     */
    @Override
    public AbsensiMahasiswaStatResponse getMahasiswaStat(UUID userId) {
        return repository.getMahasiswaStat(userId);
    }

    @Override
    public Long getTotalKehadiran(UUID userId) {
        return repository.getTotalKehadiran(userId);
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(";") || field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
