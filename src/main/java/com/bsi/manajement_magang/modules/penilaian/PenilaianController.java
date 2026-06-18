package com.bsi.manajement_magang.modules.penilaian;

import com.bsi.manajement_magang.modules.penilaian.schemas.request.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianStatResponse;
import com.bsi.manajement_magang.modules.penilaian.PenilaianService;
import com.bsi.manajement_magang.shared.APIResponse;
import com.bsi.manajement_magang.shared.DomainException;
import com.bsi.manajement_magang.shared.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/penilaian")
public class PenilaianController {
    private final PenilaianService penilaianService;

    public PenilaianController(PenilaianService penilaianService) {
        this.penilaianService = penilaianService;
    }

    @GetMapping
    public ResponseEntity<com.bsi.manajement_magang.shared.PaginatedResponse<PenilaianResponse>> listPenilaian(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa,
            @RequestParam(defaultValue = "1") int index,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(penilaianService.listPenilaian(status, namaMahasiswa, index, size));
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

    @GetMapping("/mahasiswa/nilai")
    public ResponseEntity<APIResponse<PenilaianResponse>> getMahasiswaNilai() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = SecurityUtil.requireUserId(auth);
        PenilaianResponse result = penilaianService.getMahasiswaNilai(userId)
                .orElseThrow(() -> DomainException.notFound("Tidak ada data penilaian ditemukan untuk mahasiswa ini"));
        return ResponseEntity.ok(APIResponse.success(result));
    }
}
