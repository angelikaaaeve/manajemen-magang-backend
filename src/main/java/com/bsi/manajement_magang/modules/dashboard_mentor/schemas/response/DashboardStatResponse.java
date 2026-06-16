package com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response;

import java.util.Map;

public record DashboardStatResponse(
    long jumlahMahasiswaAktif,
    long jumlahMahasiswaSelesai,
    Map<String, Long> rekapAbsensi
) {}
