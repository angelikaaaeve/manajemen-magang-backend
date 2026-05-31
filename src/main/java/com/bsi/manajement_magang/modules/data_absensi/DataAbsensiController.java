package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.modules.data_absensi.schema.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.AbsensiStatResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // 1. Baca / List Absensi Mahasiswa (with filters)
    @GetMapping
    public ResponseEntity<List<AbsensiResponse>> listAbsensi(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        List<AbsensiResponse> response = dataAbsensiService.listAbsensi(status, namaMahasiswa);
        return ResponseEntity.ok(response);
    }

    // 2. Verifikasi Absensi: setujui (action=setujui) / tolak (action=tolak)
    @PostMapping("/{id}/verifikasi")
    public ResponseEntity<AbsensiResponse> verifyAbsensi(
            @PathVariable UUID id,
            @RequestParam String action) {
        AbsensiResponse response = dataAbsensiService.verifyAbsensi(id, action);
        return ResponseEntity.ok(response);
    }

    // 2b. Verifikasi Absensi: hapus (DELETE /api/absensi/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsensi(@PathVariable UUID id) {
        dataAbsensiService.deleteAbsensi(id);
        return ResponseEntity.noContent().build();
    }

    // 3. Statistik Absensi (following filters)
    @GetMapping("/statistik")
    public ResponseEntity<AbsensiStatResponse> getAbsensiStatistics(
            @RequestParam(required = false) String namaMahasiswa) {
        AbsensiStatResponse response = dataAbsensiService.getAbsensiStatistics(namaMahasiswa);
        return ResponseEntity.ok(response);
    }

    // 4. Lihat Surat Keterangan (Attachment URL)
    @GetMapping("/{id}/surat-keterangan")
    public ResponseEntity<Map<String, String>> getSuratKeterangan(@PathVariable UUID id) {
        String url = dataAbsensiService.getAttachmentUrl(id);
        return ResponseEntity.ok(Map.of("url", url));
    }

    // 5. Ekspor Rekap Absensi to Excel CSV
    @GetMapping("/ekspor")
    public ResponseEntity<byte[]> exportAbsensi(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String namaMahasiswa) {
        String csv = dataAbsensiService.exportRekapAbsensi(status, namaMahasiswa);
        byte[] body = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDispositionFormData("attachment", "rekap-absensi.csv");

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
