package com.bsi.manajement_magang.modules.data_mahasiswa;

import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.request.StudentRequest;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.request.UpdateStudentRequest;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response.StudentResponse;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response.StudentStatResponse;
import com.bsi.manajement_magang.modules.data_mahasiswa.DataMahasiswaService;
import com.bsi.manajement_magang.shared.APIResponse;
import com.bsi.manajement_magang.shared.DomainException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mahasiswa")
public class DataMahasiswaController {
    private final DataMahasiswaService dataMahasiswaService;

    public DataMahasiswaController(DataMahasiswaService dataMahasiswaService) {
        this.dataMahasiswaService = dataMahasiswaService;
    }

    @PostMapping
    public ResponseEntity<APIResponse<StudentResponse>> addStudent(@RequestBody @Valid StudentRequest req) {
        StudentResponse data = dataMahasiswaService.addStudent(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(data, "Student registered successfully"));
    }

    @PutMapping("/edit-by-mentor/{id}")
    public ResponseEntity<APIResponse<StudentResponse>> editStudent(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateStudentRequest req) {
        return ResponseEntity.ok(APIResponse.success(dataMahasiswaService.editStudent(id, req), "Student updated successfully"));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<StudentResponse>>> listStudents(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String universitas,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(APIResponse.success(dataMahasiswaService.listStudents(gender, universitas, status)));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<StudentStatResponse>> getStudentStatistics(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String universitas) {
        return ResponseEntity.ok(APIResponse.success(dataMahasiswaService.getStudentStatistics(gender, universitas)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<StudentResponse>> getStudentDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(APIResponse.success(dataMahasiswaService.getStudentDetail(id)));
    }

    @GetMapping("/sisa-waktu-magang")
    public ResponseEntity<APIResponse<Long>> getSisaWaktuMagang() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null ||
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MAHASISWA"))) {
            throw DomainException.unauthorized("Access restricted to mahasiswa");
        }
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(APIResponse.success(dataMahasiswaService.getSisaWaktuMagang(userId)));
    }
}
