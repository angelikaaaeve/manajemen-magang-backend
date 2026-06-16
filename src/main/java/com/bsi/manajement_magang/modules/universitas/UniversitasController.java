package com.bsi.manajement_magang.modules.universitas;

import com.bsi.manajement_magang.modules.universitas.schemas.request.UniversitasRequest;
import com.bsi.manajement_magang.modules.universitas.schemas.response.UniversitasResponse;
import com.bsi.manajement_magang.modules.universitas.UniversitasService;
import com.bsi.manajement_magang.shared.APIResponse;
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

    @PostMapping
    public ResponseEntity<APIResponse<UniversitasResponse>> addUniversitas(@RequestBody @Valid UniversitasRequest req) {
        UniversitasResponse data = service.addUniversitas(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(data, "University registered successfully"));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<UniversitasResponse>>> listUniversitas() {
        return ResponseEntity.ok(APIResponse.success(service.listUniversitas()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<UniversitasResponse>> editUniversitas(
            @PathVariable Long id,
            @RequestBody @Valid UniversitasRequest req) {
        return ResponseEntity.ok(APIResponse.success(service.editUniversitas(id, req), "University updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUniversitas(@PathVariable Long id) {
        service.deleteUniversitas(id);
        return ResponseEntity.ok(APIResponse.success(null, "University deleted successfully"));
    }
}
