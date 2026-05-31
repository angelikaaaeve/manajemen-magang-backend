package com.bsi.manajement_magang.modules.data_mahasiswa.schema;

public record StudentStatResponse(
    long totalAktif,
    long totalSelesai,
    long totalAktifTanpaPenilaian
) {}
