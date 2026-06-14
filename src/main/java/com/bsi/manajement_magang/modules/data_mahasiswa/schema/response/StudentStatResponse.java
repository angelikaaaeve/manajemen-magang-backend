package com.bsi.manajement_magang.modules.data_mahasiswa.schema.response;

public record StudentStatResponse(
    long totalAktif,
    long totalSelesai,
    long totalAktifTanpaPenilaian
) {}
