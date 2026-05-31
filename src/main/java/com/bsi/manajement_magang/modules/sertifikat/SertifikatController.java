package com.bsi.manajement_magang.modules.sertifikat;

import com.bsi.manajement_magang.modules.sertifikat.schema.SertifikatRequest;
import com.bsi.manajement_magang.modules.sertifikat.schema.SertifikatResponse;
import com.bsi.manajement_magang.modules.sertifikat.schema.SertifikatStatResponse;
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

    // 1. Baca / List Sertifikat Mahasiswa (with filters)
    @GetMapping
    public ResponseEntity<List<SertifikatResponse>> listSertifikat(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        List<SertifikatResponse> response = sertifikatService.listSertifikat(status, namaMahasiswa);
        return ResponseEntity.ok(response);
    }

    // 2. Mengunggah file sertifikat (save & update)
    @PostMapping
    public ResponseEntity<SertifikatResponse> uploadSertifikat(@RequestBody @Valid SertifikatRequest req) {
        SertifikatResponse response = sertifikatService.uploadSertifikat(req);
        return ResponseEntity.ok(response);
    }

    // 3. Statistik Sertifikat (support filters)
    @GetMapping("/statistik")
    public ResponseEntity<SertifikatStatResponse> getSertifikatStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        SertifikatStatResponse response = sertifikatService.getSertifikatStatistics(namaMahasiswa);
        return ResponseEntity.ok(response);
    }
}
