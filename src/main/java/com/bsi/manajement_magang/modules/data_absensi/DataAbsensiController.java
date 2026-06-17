package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.modules.data_absensi.schemas.request.AbsensiMentorRequest;
import com.bsi.manajement_magang.modules.data_absensi.schemas.request.SubmitAbsensiRequest;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiHarianMentorResponse;
import com.bsi.manajement_magang.shared.APIResponse;
import com.bsi.manajement_magang.shared.DomainException;
import com.bsi.manajement_magang.shared.PaginatedResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/absensi")
public class DataAbsensiController {
    private final DataAbsensiService dataAbsensiService;

    public DataAbsensiController(DataAbsensiService dataAbsensiService) {
        this.dataAbsensiService = dataAbsensiService;
    }

    // ========================================================
    // MENTOR ENDPOINTS
    // ========================================================

    /**
     * GET /api/absensi/mentor/harian
     * Paginated list SEMUA mahasiswa bimbingan yang periode magangnya mencakup tanggal tertentu.
     * Status absensi: hadir/izin/sakit jika sudah dicatat, "alpha" jika belum.
     * Query param `tanggal` (yyyy-MM-dd) opsional; default hari ini.
     */
    @GetMapping("/mentor/harian")
    public ResponseEntity<PaginatedResponse<AbsensiHarianMentorResponse>> getAbsensiHarianMentor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
            @RequestParam(defaultValue = "1") int index,
            @RequestParam(defaultValue = "10") int size) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
            throw DomainException.unauthorized("Access restricted to mentor");
        }
        LocalDate targetDate = tanggal != null ? tanggal : LocalDate.now();
        return ResponseEntity.ok(
            dataAbsensiService.listAbsensiHarianMentor(targetDate, index, size)
        );
    }

    /**
     * POST /api/absensi/mentor/submit
     * Mentor mencatat absensi untuk mahasiswa bimbingannya.
     * Body: { mahasiswaId, status, tanggal (opsional, default hari ini), attachmentUrl (opsional) }
     */
    @PostMapping("/mentor/submit")
    public ResponseEntity<APIResponse<AbsensiResponse>> submitAbsensiByMentor(
            @RequestBody AbsensiMentorRequest req) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
            throw DomainException.unauthorized("Access restricted to mentor");
        }
        UUID mentorUserId = (UUID) auth.getPrincipal();
        LocalDate targetDate = req.tanggal() != null ? req.tanggal() : LocalDate.now();
        AbsensiResponse result = dataAbsensiService.submitAbsensiByMentor(
            mentorUserId, req.mahasiswaId(), req.status(), targetDate, req.attachmentUrl()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(APIResponse.success(result, "Absensi berhasil dicatat"));
    }

    // ========================================================
    // MAHASISWA ENDPOINTS
    // ========================================================

    @GetMapping("/total-kehadiran")
    public ResponseEntity<APIResponse<Long>> getTotalKehadiran() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null ||
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = (UUID) auth.getPrincipal();
        Long total = dataAbsensiService.getTotalKehadiran(userId);
        return ResponseEntity.ok(APIResponse.success(total != null ? total : 0L));
    }

    @GetMapping("/statistik-kehadiran")
    public ResponseEntity<APIResponse<Map<String, Long>>> getStatistikKehadiran() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null ||
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = (UUID) auth.getPrincipal();
        AbsensiMahasiswaStatResponse stat = dataAbsensiService.getMahasiswaStat(userId);
        Map<String, Long> data = Map.of(
                "totalHadir", stat.totalHadir(),
                "totalIzin", stat.totalIzin(),
                "totalSakit", stat.totalSakit()
        );
        return ResponseEntity.ok(APIResponse.success(data));
    }

    @PostMapping("/mahasiswa/submit")
    public ResponseEntity<APIResponse<AbsensiResponse>> submitAbsensi(
            @RequestParam UUID userId,
            @RequestBody SubmitAbsensiRequest req) {
        AbsensiResponse data = dataAbsensiService.submitAbsensi(userId, req.status(), req.attachmentUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(data, "Absensi submitted successfully"));
    }

    @GetMapping("/mahasiswa/riwayat")
    public ResponseEntity<PaginatedResponse<AbsensiResponse>> getRiwayatAbsensi(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "1") int index,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dataAbsensiService.getRiwayatAbsensi(userId, index, size));
    }

    @GetMapping("/mahasiswa/statistik")
    public ResponseEntity<APIResponse<AbsensiMahasiswaStatResponse>> getMahasiswaStat(@RequestParam UUID userId) {
        return ResponseEntity.ok(APIResponse.success(dataAbsensiService.getMahasiswaStat(userId)));
    }

    // ========================================================
    // SHARED / ADMIN ENDPOINTS
    // ========================================================

    @GetMapping
    public ResponseEntity<PaginatedResponse<AbsensiResponse>> listAbsensi(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa,
            @RequestParam(defaultValue = "1") int index,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dataAbsensiService.listAbsensi(status, namaMahasiswa, index, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteAbsensi(@PathVariable UUID id) {
        dataAbsensiService.deleteAbsensi(id);
        return ResponseEntity.ok(APIResponse.success(null, "Attendance record deleted successfully"));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<AbsensiStatResponse>> getAbsensiStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(dataAbsensiService.getAbsensiStatistics(namaMahasiswa)));
    }

    @PostMapping("/{id}/verifikasi")
    public ResponseEntity<APIResponse<AbsensiResponse>> verifikasiAbsensi(
            @PathVariable UUID id,
            @RequestParam String action) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
            throw DomainException.unauthorized("Access restricted to mentor");
        }
        UUID mentorUserId = (UUID) auth.getPrincipal();
        AbsensiResponse result = dataAbsensiService.verifikasiAbsensi(id, action, mentorUserId);
        String msg = "setujui".equalsIgnoreCase(action) ? "Absensi berhasil disetujui" : "Absensi berhasil ditolak";
        return ResponseEntity.ok(APIResponse.success(result, msg));
    }

    @GetMapping("/{id}/surat-keterangan")
    public ResponseEntity<APIResponse<Map<String, String>>> getSuratKeterangan(@PathVariable UUID id) {
        String url = dataAbsensiService.getAttachmentUrl(id);
        return ResponseEntity.ok(APIResponse.success(Map.of("url", url)));
    }

    @GetMapping("/ekspor")
    public ResponseEntity<byte[]> exportAbsensi(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        String csv = dataAbsensiService.exportRekapAbsensi(status, namaMahasiswa);
        byte[] body = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDispositionFormData("attachment", "rekap-absensi.csv");

        return ResponseEntity.ok().headers(headers).body(body);
    }
}
