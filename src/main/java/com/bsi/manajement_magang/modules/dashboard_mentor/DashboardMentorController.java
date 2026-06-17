package com.bsi.manajement_magang.modules.dashboard_mentor;

import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.request.RegisterStudentRequest;
import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response.DashboardStatResponse;
import com.bsi.manajement_magang.modules.dashboard_mentor.schemas.response.SearchStudentResponse;
import com.bsi.manajement_magang.modules.dashboard_mentor.DashboardMentorService;
import com.bsi.manajement_magang.shared.APIResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard-mentor")
public class DashboardMentorController {
    private final DashboardMentorService service;

    public DashboardMentorController(DashboardMentorService service) {
        this.service = service;
    }

    @GetMapping("/mahasiswa")
    public ResponseEntity<APIResponse<List<SearchStudentResponse>>> searchStudents(
            @RequestParam(value = "nama", required = false) String name) {
        return ResponseEntity.ok(APIResponse.success(service.searchStudentsByName(name)));
    }

    @PostMapping("/mahasiswa")
    public ResponseEntity<APIResponse<SearchStudentResponse>> registerStudent(
            @RequestBody @Valid RegisterStudentRequest req) {
        SearchStudentResponse data = service.addStudent(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(data, "Student registered successfully"));
    }

    @GetMapping("/statistik")
    public ResponseEntity<APIResponse<DashboardStatResponse>> getDashboardStatistics() {
        return ResponseEntity.ok(APIResponse.success(service.getDashboardStats()));
    }
}
