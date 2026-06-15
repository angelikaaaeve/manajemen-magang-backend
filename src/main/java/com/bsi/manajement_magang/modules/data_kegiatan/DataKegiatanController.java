package com.bsi.manajement_magang.modules.data_kegiatan;

import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityStatResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.DataKegiatanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/kegiatan")
public class DataKegiatanController {
    private final DataKegiatanService dataKegiatanService;

    public DataKegiatanController(DataKegiatanService dataKegiatanService) {
        this.dataKegiatanService = dataKegiatanService;
    }

    // 1. Baca / List Kegiatan Mahasiswa (with filters)
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> listActivities(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        List<ActivityResponse> response = dataKegiatanService.listActivities(status, namaMahasiswa);
        return ResponseEntity.ok(response);
    }

    // 2. Mengubah "status" kegiatan
    @PutMapping("/{id}/status")
    public ResponseEntity<ActivityResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        ActivityResponse response = dataKegiatanService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // 3. Menghapus kegiatan mahasiswa tertentu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        dataKegiatanService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    // 4. Lihat File Kegiatan (yang diupload mahasiswa)
    @GetMapping("/{id}/file")
    public ResponseEntity<Map<String, String>> getActivityFile(@PathVariable UUID id) {
        String url = dataKegiatanService.getActivityFileUrl(id);
        return ResponseEntity.ok(Map.of("url", url));
    }

    // 5. Statistik Kegiatan (support filters)
    @GetMapping("/statistik")
    public ResponseEntity<ActivityStatResponse> getActivityStatistics(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        ActivityStatResponse response = dataKegiatanService.getActivityStatistics(status, namaMahasiswa);
        return ResponseEntity.ok(response);
    }
}
