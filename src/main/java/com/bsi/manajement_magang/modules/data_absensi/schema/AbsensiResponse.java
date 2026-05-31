package com.bsi.manajement_magang.modules.data_absensi.schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AbsensiResponse(
    UUID id,
    UUID periodeMagangId,
    UUID mahasiswaId,
    String nim,
    String namaMahasiswa,
    LocalDate tanggal,
    OffsetDateTime waktuMasuk,
    OffsetDateTime waktuKeluar,
    String status,
    String attachmentUrl,
    String statusVerifikasi
) {}
