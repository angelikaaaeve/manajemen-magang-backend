package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.modules.data_absensi.schema.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.AbsensiStatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class DataAbsensiService {
    private final DataAbsensiRepository repository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public DataAbsensiService(DataAbsensiRepository repository) {
        this.repository = repository;
    }

    // List all attendance records with filters
    public List<AbsensiResponse> listAbsensi(String status, String namaMahasiswa) {
        return repository.listAbsensi(status, namaMahasiswa);
    }

    // Verify / Approve attendance record
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
    @Transactional
    public void deleteAbsensi(UUID id) {
        AbsensiResponse record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record with ID '" + id + "' was not found"));
        
        repository.deleteAbsensi(id);
    }

    // Get attendance statistics
    public AbsensiStatResponse getAbsensiStatistics(String namaMahasiswa) {
        return repository.getAbsensiStatistics(namaMahasiswa);
    }

    // Get attachment URL (lihat Surat Keterangan)
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
    public String exportRekapAbsensi(String status, String namaMahasiswa) {
        List<AbsensiResponse> records = repository.listAbsensi(status, namaMahasiswa);

        StringBuilder csv = new StringBuilder();
        // CSV BOM (Byte Order Mark) so Excel recognizes UTF-8 characters and semicolons correctly
        csv.append('\ufeff');

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

    // Helper to escape CSV fields
    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(";") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
