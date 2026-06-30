package com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response;

public record AttendanceRecap(
    long hadir,
    long izin,
    long sakit,
    long tidakHadir
) {}
