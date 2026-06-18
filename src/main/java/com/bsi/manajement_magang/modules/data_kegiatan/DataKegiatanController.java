package com.bsi.manajement_magang.modules.data_kegiatan;

import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityStatResponse;
import com.bsi.manajement_magang.shared.APIResponse;
import com.bsi.manajement_magang.shared.DomainException;
import com.bsi.manajement_magang.shared.PaginatedResponse;
import com.bsi.manajement_magang.shared.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<PaginatedResponse<ActivityResponse>> listActivities(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa,
            @RequestParam(defaultValue = "1") int index,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dataKegiatanService.listActivities(status, namaMahasiswa, index, size));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<APIResponse<ActivityResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UUID mentorUserId = null;
        if (auth != null && auth.getPrincipal() instanceof UUID &&
                auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
            mentorUserId = SecurityUtil.requireUserId(auth);
        }
        return ResponseEntity.ok(APIResponse.success(dataKegiatanService.updateStatus(id, status, mentorUserId), "Status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteActivity(@PathVariable UUID id) {
        dataKegiatanService.deleteActivity(id);
        return ResponseEntity.ok(APIResponse.success(null, "Activity deleted successfully"));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<APIResponse<Map<String, List<String>>>> getActivityFiles(@PathVariable UUID id) {
        List<String> urls = dataKegiatanService.getActivityFiles(id);
        return ResponseEntity.ok(APIResponse.success(Map.of("urls", urls)));
    }

    @PostMapping("/{id}/file")
    public ResponseEntity<APIResponse<Void>> addFilesToActivity(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        @SuppressWarnings("unchecked")
        List<String> fileUrls = body.containsKey("fileUrls") ? (List<String>) body.get("fileUrls") : List.of();
        dataKegiatanService.addFilesToKegiatan(id, fileUrls);
        return ResponseEntity.ok(APIResponse.success(null, "File berhasil ditambahkan"));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<ActivityStatResponse>> getActivityStatistics(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(dataKegiatanService.getActivityStatistics(status, namaMahasiswa)));
    }

    @GetMapping("/mahasiswa")
    public ResponseEntity<APIResponse<List<ActivityResponse>>> getMahasiswaActivities() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = SecurityUtil.requireUserId(auth);
        return ResponseEntity.ok(APIResponse.success(dataKegiatanService.listMahasiswaActivities(userId)));
    }

    @PostMapping("/mahasiswa")
    public ResponseEntity<APIResponse<ActivityResponse>> createMahasiswaActivity(
            @RequestBody Map<String, Object> body) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = SecurityUtil.requireUserId(auth);
        String judul = (String) body.get("judul");
        String deskripsi = body.containsKey("deskripsi") ? (String) body.get("deskripsi") : "";
        @SuppressWarnings("unchecked")
        List<String> fileUrls = body.containsKey("fileUrls") ? (List<String>) body.get("fileUrls") : List.of();
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(
                dataKegiatanService.createMahasiswaActivity(userId, judul, deskripsi, fileUrls),
                "Kegiatan berhasil ditambahkan"));
    }
}
