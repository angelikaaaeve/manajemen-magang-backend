package com.bsi.manajement_magang.modules.dashboard_mahasiswa.controller;

import com.bsi.manajement_magang.modules.dashboard_mahasiswa.schema.response.DashboardMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.dashboard_mahasiswa.service.DashboardMahasiswaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard-mahasiswa")
public class DashboardMahasiswaController {
    private final DashboardMahasiswaService service;

    public DashboardMahasiswaController(DashboardMahasiswaService service) {
        this.service = service;
    }

    // Get statistics for the student dashboard - Tanpa Auth
    @GetMapping("/statistik")
    public ResponseEntity<DashboardMahasiswaStatResponse> getDashboardStatistics(
            @RequestParam("mahasiswaId") UUID mahasiswaId) {
        DashboardMahasiswaStatResponse response = service.getDashboardStats(mahasiswaId);
        return ResponseEntity.ok(response);
    }
}
