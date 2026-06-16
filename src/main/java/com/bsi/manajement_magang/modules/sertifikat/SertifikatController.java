package com.bsi.manajement_magang.modules.sertifikat;

import com.bsi.manajement_magang.modules.sertifikat.schemas.request.SertifikatRequest;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatResponse;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatStatResponse;
import com.bsi.manajement_magang.modules.sertifikat.SertifikatService;
import com.bsi.manajement_magang.shared.APIResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sertifikat")
public class SertifikatController {
    private final SertifikatService sertifikatService;

    public SertifikatController(SertifikatService sertifikatService) {
        this.sertifikatService = sertifikatService;
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<SertifikatResponse>>> listSertifikat(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(sertifikatService.listSertifikat(status, namaMahasiswa)));
    }

    @PostMapping
    public ResponseEntity<APIResponse<SertifikatResponse>> uploadSertifikat(@RequestBody @Valid SertifikatRequest req) {
        return ResponseEntity.ok(APIResponse.success(sertifikatService.uploadSertifikat(req), "Certificate saved successfully"));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<SertifikatStatResponse>> getSertifikatStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(sertifikatService.getSertifikatStatistics(namaMahasiswa)));
    }
}
