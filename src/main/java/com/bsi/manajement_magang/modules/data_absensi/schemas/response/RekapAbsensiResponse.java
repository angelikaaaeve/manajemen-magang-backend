package com.bsi.manajement_magang.modules.data_absensi.schemas.response;

import java.time.LocalDate;

public record RekapAbsensiResponse(
    String namaMahasiswa,
    LocalDate tanggal,
    String status
) {}
