package com.bsi.manajement_magang.modules.penilaian;

import com.bsi.manajement_magang.modules.penilaian.schema.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schema.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schema.PenilaianStatResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/penilaian")
public class PenilaianController {
    private final PenilaianSerivice penilaianSerivice;

    public PenilaianController(PenilaianSerivice penilaianSerivice) {
        this.penilaianSerivice = penilaianSerivice;
    }

    // 1. Baca / List Penilaian Mahasiswa (with filters)
    @GetMapping
    public ResponseEntity<List<PenilaianResponse>> listPenilaian(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        List<PenilaianResponse> response = penilaianSerivice.listPenilaian(status, namaMahasiswa);
        return ResponseEntity.ok(response);
    }

    // 2. Edit Penilaian (digunakan untuk menilai mahasiswa / save & update)
    @PostMapping
    public ResponseEntity<PenilaianResponse> editPenilaian(@RequestBody @Valid PenilaianRequest req) {
        PenilaianResponse response = penilaianSerivice.editPenilaian(req);
        return ResponseEntity.ok(response);
    }

    // 3. Statistik Penilaian (support filters)
    @GetMapping("/statistik")
    public ResponseEntity<PenilaianStatResponse> getPenilaianStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        PenilaianStatResponse response = penilaianSerivice.getPenilaianStatistics(namaMahasiswa);
        return ResponseEntity.ok(response);
    }
}
