package com.bsi.manajement_magang.modules.data_kegiatan;

import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityStatResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.DataKegiatanService;
import com.bsi.manajement_magang.shared.APIResponse;
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

    @GetMapping
    public ResponseEntity<APIResponse<List<ActivityResponse>>> listActivities(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(dataKegiatanService.listActivities(status, namaMahasiswa)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<APIResponse<ActivityResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ResponseEntity.ok(APIResponse.success(dataKegiatanService.updateStatus(id, status), "Status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteActivity(@PathVariable UUID id) {
        dataKegiatanService.deleteActivity(id);
        return ResponseEntity.ok(APIResponse.success(null, "Activity deleted successfully"));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<APIResponse<Map<String, String>>> getActivityFile(@PathVariable UUID id) {
        String url = dataKegiatanService.getActivityFileUrl(id);
        return ResponseEntity.ok(APIResponse.success(Map.of("url", url)));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<ActivityStatResponse>> getActivityStatistics(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(dataKegiatanService.getActivityStatistics(status, namaMahasiswa)));
    }
}
