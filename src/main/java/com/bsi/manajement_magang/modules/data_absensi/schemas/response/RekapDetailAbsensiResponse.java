package com.bsi.manajement_magang.modules.data_absensi.schemas.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RekapDetailAbsensiResponse(
    UUID id,
    String namaMahasiswa,
    LocalDate tanggal,
    String status,
    LocalDateTime createdAt,
    String attachmentUrl
) {}
