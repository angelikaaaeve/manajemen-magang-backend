package com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response;

public record AttendanceStatResponse(
    long jumlahHadir,
    long jumlahIzin,
    long jumlahSakit
) {}
