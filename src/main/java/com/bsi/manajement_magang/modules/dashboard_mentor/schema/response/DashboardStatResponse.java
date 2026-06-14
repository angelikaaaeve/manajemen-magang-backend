package com.bsi.manajement_magang.modules.dashboard_mentor.schema.response;

import java.util.Map;

public record DashboardStatResponse(
    long jumlahMahasiswaAktif,
    long jumlahMahasiswaSelesai,
    Map<String, Long> rekapAbsensi
) {}
