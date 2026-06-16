package com.bsi.manajement_magang.modules.penilaian;

import com.bsi.manajement_magang.modules.penilaian.schemas.request.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianStatResponse;
import com.bsi.manajement_magang.modules.penilaian.PenilaianService;
import com.bsi.manajement_magang.shared.APIResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/penilaian")
public class PenilaianController {
    private final PenilaianService penilaianService;

    public PenilaianController(PenilaianService penilaianService) {
        this.penilaianService = penilaianService;
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<PenilaianResponse>>> listPenilaian(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(penilaianService.listPenilaian(status, namaMahasiswa)));
    }

    @PostMapping
    public ResponseEntity<APIResponse<PenilaianResponse>> editPenilaian(@RequestBody @Valid PenilaianRequest req) {
        return ResponseEntity.ok(APIResponse.success(penilaianService.editPenilaian(req), "Assessment saved successfully"));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<PenilaianStatResponse>> getPenilaianStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(penilaianService.getPenilaianStatistics(namaMahasiswa)));
    }
}
