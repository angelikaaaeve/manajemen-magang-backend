package com.bsi.manajement_magang.modules.surat_keterangan.controller;

import com.bsi.manajement_magang.modules.surat_keterangan.schema.request.SuratKeteranganRequest;
import com.bsi.manajement_magang.modules.surat_keterangan.schema.response.SuratKeteranganResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.schema.response.SuratKeteranganStatResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.service.SuratKeteranganService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surat-keterangan")
public class SuratKeteranganController {
    private final SuratKeteranganService suratKeteranganService;

    public SuratKeteranganController(SuratKeteranganService suratKeteranganService) {
        this.suratKeteranganService = suratKeteranganService;
    }

    // 1. Baca / List Surat Keterangan Mahasiswa (with filters)
    @GetMapping
    public ResponseEntity<List<SuratKeteranganResponse>> listSuratKeterangan(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        List<SuratKeteranganResponse> response = suratKeteranganService.listSuratKeterangan(status, namaMahasiswa);
        return ResponseEntity.ok(response);
    }

    // 2. Mengunggah file surat keterangan (save & update)
    @PostMapping
    public ResponseEntity<SuratKeteranganResponse> uploadSuratKeterangan(@RequestBody @Valid SuratKeteranganRequest req) {
        SuratKeteranganResponse response = suratKeteranganService.uploadSuratKeterangan(req);
        return ResponseEntity.ok(response);
    }

    // 3. Statistik Surat Keterangan (support filters)
    @GetMapping("/statistik")
    public ResponseEntity<SuratKeteranganStatResponse> getSuratKeteranganStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        SuratKeteranganStatResponse response = suratKeteranganService.getSuratKeteranganStatistics(namaMahasiswa);
        return ResponseEntity.ok(response);
    }
}
