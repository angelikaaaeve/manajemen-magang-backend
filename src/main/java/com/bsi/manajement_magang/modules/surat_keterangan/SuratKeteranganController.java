package com.bsi.manajement_magang.modules.surat_keterangan;

import com.bsi.manajement_magang.modules.surat_keterangan.schemas.request.SuratKeteranganRequest;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganStatResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.SuratKeteranganService;
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
@RequestMapping("/api/surat-keterangan")
public class SuratKeteranganController {
    private final SuratKeteranganService suratKeteranganService;

    public SuratKeteranganController(SuratKeteranganService suratKeteranganService) {
        this.suratKeteranganService = suratKeteranganService;
    }

    @GetMapping
    public ResponseEntity<com.bsi.manajement_magang.shared.PaginatedResponse<SuratKeteranganResponse>> listSuratKeterangan(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa,
            @RequestParam(defaultValue = "1") int index,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(suratKeteranganService.listSuratKeterangan(status, namaMahasiswa, index, size));
    }

    @PostMapping
    public ResponseEntity<APIResponse<SuratKeteranganResponse>> uploadSuratKeterangan(
            @RequestBody @Valid SuratKeteranganRequest req) {
        return ResponseEntity.ok(APIResponse.success(
                suratKeteranganService.uploadSuratKeterangan(req), "Reference letter saved successfully"));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<SuratKeteranganStatResponse>> getSuratKeteranganStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(suratKeteranganService.getSuratKeteranganStatistics(namaMahasiswa)));
    }

    @GetMapping("/mahasiswa")
    public ResponseEntity<APIResponse<SuratKeteranganResponse>> getMahasiswaSuratKeterangan() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = SecurityUtil.requireUserId(auth);
        SuratKeteranganResponse result = suratKeteranganService.getMahasiswaSuratKeterangan(userId)
                .orElseThrow(() -> DomainException.notFound("Tidak ada data surat keterangan ditemukan untuk mahasiswa ini"));
        return ResponseEntity.ok(APIResponse.success(result));
    }
}
