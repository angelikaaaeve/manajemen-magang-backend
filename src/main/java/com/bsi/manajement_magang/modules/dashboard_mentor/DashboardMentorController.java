package com.bsi.manajement_magang.modules.dashboard_mentor;

import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.request.RegisterStudentRequest;
import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response.DashboardStatResponse;
import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response.SearchStudentResponse;
import com.bsi.manajement_magang.modules.dashboard_mentor.DashboardMentorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard-mentor")
public class DashboardMentorController {
    private final DashboardMentorService service;

    public DashboardMentorController(DashboardMentorService service) {
        this.service = service;
    }

    // 1. Query / Search students by name
    @GetMapping("/mahasiswa")
    public ResponseEntity<List<SearchStudentResponse>> searchStudents(
            @RequestParam(value = "nama", required = false) String name) {
        List<SearchStudentResponse> response = service.searchStudentsByName(name);
        return ResponseEntity.ok(response);
    }

    // 2. Register new student (daftarkan mahasiswa baru) - Tanpa Auth
    @PostMapping("/mahasiswa")
    public ResponseEntity<SearchStudentResponse> registerStudent(
            @RequestBody @Valid RegisterStudentRequest req) {
        SearchStudentResponse response = service.addStudent(req);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 3. Dashboard Statistics (Statistik Dashboard) - Tanpa Auth
    @GetMapping("/statistik")
    public ResponseEntity<DashboardStatResponse> getDashboardStatistics(
            @RequestParam(value = "mentorId", required = false) UUID mentorId) {
        DashboardStatResponse response = service.getDashboardStats(mentorId);
        return ResponseEntity.ok(response);
    }
}
