package com.bsi.manajement_magang.modules.dashboard_mahasiswa.service;

import com.bsi.manajement_magang.modules.dashboard_mahasiswa.schema.response.DashboardMahasiswaStatResponse;

import java.util.UUID;

public interface DashboardMahasiswaService {

    DashboardMahasiswaStatResponse getDashboardStats(UUID mahasiswaId);
}
