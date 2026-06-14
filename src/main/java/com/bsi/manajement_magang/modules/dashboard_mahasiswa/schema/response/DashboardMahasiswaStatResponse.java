package com.bsi.manajement_magang.modules.dashboard_mahasiswa.schema.response;

public record DashboardMahasiswaStatResponse(
    long totalKehadiran,
    long sisaWaktuMagangDays,
    String sisaWaktuMagangFormatted
) {}
