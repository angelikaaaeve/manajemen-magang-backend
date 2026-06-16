package com.bsi.manajement_magang.modules.universitas;

import com.bsi.manajement_magang.modules.universitas.schemas.request.UniversitasRequest;
import com.bsi.manajement_magang.modules.universitas.schemas.response.UniversitasResponse;
import com.bsi.manajement_magang.modules.universitas.UniversitasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universitas")
public class UniversitasController {
    private final UniversitasService service;

    public UniversitasController(UniversitasService service) {
        this.service = service;
    }

    // 1. Tambah Universitas
    @PostMapping
    public ResponseEntity<UniversitasResponse> addUniversitas(@RequestBody @Valid UniversitasRequest req) {
        UniversitasResponse response = service.addUniversitas(req);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Baca/List Universitas
    @GetMapping
    public ResponseEntity<List<UniversitasResponse>> listUniversitas() {
        List<UniversitasResponse> response = service.listUniversitas();
        return ResponseEntity.ok(response);
    }

    // 3. Edit Universitas
    @PutMapping("/{id}")
    public ResponseEntity<UniversitasResponse> editUniversitas(
            @PathVariable Long id,
            @RequestBody @Valid UniversitasRequest req) {
        UniversitasResponse response = service.editUniversitas(id, req);
        return ResponseEntity.ok(response);
    }

    // 4. Hapus Universitas
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUniversitas(@PathVariable Long id) {
        service.deleteUniversitas(id);
        return ResponseEntity.noContent().build();
    }
}
