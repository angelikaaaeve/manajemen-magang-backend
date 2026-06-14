package com.bsi.manajement_magang.modules.data_absensi.service.impl;

import com.bsi.manajement_magang.modules.data_absensi.repository.DataAbsensiRepository;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.service.DataAbsensiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Verify / Approve attendance record
    @Override
    @Transactional
    public AbsensiResponse verifyAbsensi(UUID id, String action) {
        AbsensiResponse record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record with ID '" + id + "' was not found"));

        String statusVerifikasi;
        if ("setujui".equalsIgnoreCase(action)) {
            statusVerifikasi = "DISETUJUI";
        } else if ("tolak".equalsIgnoreCase(action)) {
            statusVerifikasi = "DITOLAK";
        } else {
            throw new IllegalArgumentException("Invalid verification action: '" + action + "'. Supported: 'setujui', 'tolak'");
        }

        repository.verifyAbsensi(id, statusVerifikasi);

        return repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated attendance record"));
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
        csv.append("No;Tanggal;NIM;Nama Mahasiswa;Jam Masuk;Jam Keluar;Status Presensi;Status Verifikasi;URL Lampiran\n");

        int index = 1;
        for (AbsensiResponse record : records) {
            String jamMasuk = record.waktuMasuk() != null ? record.waktuMasuk().format(TIME_FORMATTER) : "-";
            String jamKeluar = record.waktuKeluar() != null ? record.waktuKeluar().format(TIME_FORMATTER) : "-";
            String attachment = record.attachmentUrl() != null ? record.attachmentUrl() : "-";

            csv.append(index++).append(";")
               .append(record.tanggal()).append(";")
               .append(record.nim()).append(";")
               .append(escapeCsvField(record.namaMahasiswa())).append(";")
               .append(jamMasuk).append(";")
               .append(jamKeluar).append(";")
               .append(record.status().toUpperCase()).append(";")
               .append(record.statusVerifikasi()).append(";")
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
     * @param file         dokumen pendukung (nullable, maks 10MB)
     */
    @Override
    @Transactional
    public AbsensiResponse submitAbsensi(UUID userId, String status,
                                         String keterangan, MultipartFile file) {
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

        // 4. Validasi & simpan file (jika ada)
        String attachmentUrl = null;
        if (file != null && !file.isEmpty()) {
            // Validasi tipe file
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                throw new IllegalArgumentException(
                    "Tipe file tidak didukung. Hanya PDF dan gambar (JPEG/PNG) yang diperbolehkan.");
            }
            // Validasi ukuran file (maks 10MB)
            long maxSizeBytes = 10L * 1024 * 1024;
            if (file.getSize() > maxSizeBytes) {
                throw new IllegalArgumentException(
                    "Ukuran file melebihi batas maksimum 10MB.");
            }

            try {
                // Simpan ke local storage (di production ganti dengan cloud storage)
                String uploadDir = "uploads/absensi/";
                String ext = contentType.equals("application/pdf") ? ".pdf" :
                             contentType.equals("image/png") ? ".png" : ".jpg";
                String fileName = userId + "_" + today + "_" + UUID.randomUUID() + ext;
                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                file.transferTo(filePath.toFile());
                attachmentUrl = "/uploads/absensi/" + fileName;
            } catch (IOException e) {
                throw new RuntimeException("Gagal menyimpan file attachment: " + e.getMessage(), e);
            }
        }

        // 5. Insert ke DB
        UUID newId = repository.insertAbsensi(periodeMagangId, today, statusLower, keterangan, attachmentUrl);

        // 6. Kembalikan record yang baru dibuat
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
