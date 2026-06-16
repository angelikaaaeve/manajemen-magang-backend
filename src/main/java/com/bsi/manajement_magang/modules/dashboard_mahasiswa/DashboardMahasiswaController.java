package com.bsi.manajement_magang.modules.dashboard_mahasiswa;

import com.bsi.manajement_magang.modules.dashboard_mahasiswa.schemas.response.DashboardMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.dashboard_mahasiswa.DashboardMahasiswaService;
import com.bsi.manajement_magang.shared.APIResponse;
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

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<DashboardMahasiswaStatResponse>> getDashboardStatistics(
            @RequestParam("mahasiswaId") UUID mahasiswaId) {
        return ResponseEntity.ok(APIResponse.success(service.getDashboardStats(mahasiswaId)));
    }
}
