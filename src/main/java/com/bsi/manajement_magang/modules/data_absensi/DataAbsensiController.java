package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.modules.data_absensi.schemas.request.SubmitAbsensiRequest;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.DataAbsensiService;
import com.bsi.manajement_magang.shared.APIResponse;
import com.bsi.manajement_magang.shared.DomainException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/absensi")
public class DataAbsensiController {
    private final DataAbsensiService dataAbsensiService;

    public DataAbsensiController(DataAbsensiService dataAbsensiService) {
        this.dataAbsensiService = dataAbsensiService;
    }

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

    @GetMapping
    public ResponseEntity<APIResponse<List<AbsensiResponse>>> listAbsensi(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        return ResponseEntity.ok(APIResponse.success(dataAbsensiService.listAbsensi(status, namaMahasiswa)));
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

    @PostMapping("/mahasiswa/submit")
    public ResponseEntity<APIResponse<AbsensiResponse>> submitAbsensi(
            @RequestParam UUID userId,
            @RequestBody SubmitAbsensiRequest req) {
        AbsensiResponse data = dataAbsensiService.submitAbsensi(userId, req.status(), req.keterangan(), req.attachmentUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(data, "Absensi submitted successfully"));
    }

    @GetMapping("/mahasiswa/riwayat")
    public ResponseEntity<APIResponse<List<AbsensiResponse>>> getRiwayatAbsensi(@RequestParam UUID userId) {
        return ResponseEntity.ok(APIResponse.success(dataAbsensiService.getRiwayatAbsensi(userId)));
    }

    @GetMapping("/mahasiswa/statistik")
    public ResponseEntity<APIResponse<AbsensiMahasiswaStatResponse>> getMahasiswaStat(@RequestParam UUID userId) {
        return ResponseEntity.ok(APIResponse.success(dataAbsensiService.getMahasiswaStat(userId)));
    }
}
