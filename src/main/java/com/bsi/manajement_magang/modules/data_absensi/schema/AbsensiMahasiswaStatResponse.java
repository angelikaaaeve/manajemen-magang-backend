package com.bsi.manajement_magang.modules.data_absensi.schema;

public record AbsensiMahasiswaStatResponse(
    long totalHadir,
    long totalIzin,
    long totalSakit,
    long totalAlfa
) {}
