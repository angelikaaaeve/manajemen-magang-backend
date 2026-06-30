package com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response;

public record StudentStatResponse(
    long totalMahasiswa,
    long totalAktif,
    long totalSelesai,
    long totalAktifTanpaPenilaian
) {}
