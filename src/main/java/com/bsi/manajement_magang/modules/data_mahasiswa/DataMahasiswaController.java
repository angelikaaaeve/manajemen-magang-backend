package com.bsi.manajement_magang.modules.data_mahasiswa;

import com.bsi.manajement_magang.modules.data_mahasiswa.schema.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mahasiswa")
public class DataMahasiswaController {
    private final DataMahasiwaService dataMahasiwaService;

    public DataMahasiswaController(DataMahasiwaService dataMahasiwaService) {
        this.dataMahasiwaService = dataMahasiwaService;
    }

    // 1. Tambah Mahasiswa
    @PostMapping
    public ResponseEntity<StudentResponse> addStudent(@RequestBody @Valid StudentRequest req) {
        StudentResponse response = dataMahasiwaService.addStudent(req);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Edit Mahasiswa (Data Mahasiswa & Periode)
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> editStudent(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateStudentRequest req) {
        StudentResponse response = dataMahasiwaService.editStudent(id, req);
        return ResponseEntity.ok(response);
    }

    // 3. Baca/List Mahasiswa (with filters)
    @GetMapping
    public ResponseEntity<List<StudentResponse>> listStudents(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String universitas,
            @RequestParam(required = false) String status) {
        List<StudentResponse> response = dataMahasiwaService.listStudents(gender, universitas, status);
        return ResponseEntity.ok(response);
    }

    // 4. Statistik Mahasiswa (following filters)
    @GetMapping("/statistik")
    public ResponseEntity<StudentStatResponse> getStudentStatistics(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String universitas) {
        StudentStatResponse response = dataMahasiwaService.getStudentStatistics(gender, universitas);
        return ResponseEntity.ok(response);
    }

    // 5. Detail Mahasiswa (mahasiswa.id)
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentDetail(@PathVariable UUID id) {
        StudentResponse response = dataMahasiwaService.getStudentDetail(id);
        return ResponseEntity.ok(response);
    }
}
