package com.bsi.manajement_magang.modules.dashboard_mahasiswa.schemas.response;

public record DashboardMahasiswaStatResponse(
    long totalKehadiran,
    long sisaWaktuMagangDays,
    String sisaWaktuMagangFormatted
) {}
