package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.modules.data_absensi.DataAbsensiRepository;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiStatResponse;
import com.bsi.manajement_magang.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public List<AbsensiResponse> listAbsensi(String status, String namaMahasiswa) {
        return repository.listAbsensi(status, namaMahasiswa);
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
        List<AbsensiResponse> records = repository.listAbsensi(status, namaMahasiswa);

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

    @Transactional
    public AbsensiResponse submitAbsensi(UUID userId, String status,
                                         String keterangan, String attachmentUrl) {
        String statusLower = status != null ? status.toLowerCase().trim() : "";
        if (!List.of("hadir", "izin", "sakit").contains(statusLower)) {
            throw DomainException.invalidValue("status",
                    "'" + status + "' tidak valid. Pilihan: hadir, izin, sakit");
        }

        UUID periodeMagangId = repository.findActivePeriodeByUserId(userId)
                .orElseThrow(() -> DomainException.conflict(
                        "Mahasiswa tidak memiliki periode magang aktif. Absensi tidak dapat dilakukan."));

        LocalDate today = LocalDate.now();
        if (repository.existsByPeriodeAndTanggal(periodeMagangId, today)) {
            throw DomainException.conflict(
                    "Absensi untuk hari ini (" + today + ") sudah tercatat.");
        }

        UUID newId = repository.insertAbsensi(periodeMagangId, today, statusLower, keterangan, attachmentUrl);

        return repository.findById(newId)
                .orElseThrow(() -> DomainException.internalError("Gagal mengambil record absensi setelah insert."));
    }

    public List<AbsensiResponse> getRiwayatAbsensi(UUID userId) {
        return repository.listAbsensiByUserId(userId);
    }

    public AbsensiMahasiswaStatResponse getMahasiswaStat(UUID userId) {
        return repository.getMahasiswaStat(userId);
    }

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
