package com.bsi.manajement_magang.modules.sertifikat;

import com.bsi.manajement_magang.modules.sertifikat.schemas.request.SertifikatRequest;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatResponse;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatStatResponse;
import com.bsi.manajement_magang.modules.sertifikat.SertifikatService;
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
@RequestMapping("/api/sertifikat")
public class SertifikatController {
    private final SertifikatService sertifikatService;

    public SertifikatController(SertifikatService sertifikatService) {
        this.sertifikatService = sertifikatService;
    }

    @GetMapping
    public ResponseEntity<com.bsi.manajement_magang.shared.PaginatedResponse<SertifikatResponse>> listSertifikat(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa,
            @RequestParam(defaultValue = "1") int index,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sertifikatService.listSertifikat(status, namaMahasiswa, index, size));
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

    @GetMapping("/mahasiswa")
    public ResponseEntity<APIResponse<SertifikatResponse>> getMahasiswaSertifikat() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = SecurityUtil.requireUserId(auth);
        SertifikatResponse result = sertifikatService.getMahasiswaSertifikat(userId)
                .orElseThrow(() -> DomainException.notFound("Tidak ada data sertifikat ditemukan untuk mahasiswa ini"));
        return ResponseEntity.ok(APIResponse.success(result));
    }
}
