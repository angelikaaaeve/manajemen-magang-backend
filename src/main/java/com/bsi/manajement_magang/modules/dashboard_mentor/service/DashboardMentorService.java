package com.bsi.manajement_magang.modules.dashboard_mentor.service;

import com.bsi.manajement_magang.modules.dashboard_mentor.schema.request.RegisterStudentRequest;
import com.bsi.manajement_magang.modules.dashboard_mentor.schema.response.DashboardStatResponse;
import com.bsi.manajement_magang.modules.dashboard_mentor.schema.response.SearchStudentResponse;

import java.util.List;
import java.util.UUID;

public interface DashboardMentorService {

    SearchStudentResponse addStudent(RegisterStudentRequest req);

    List<SearchStudentResponse> searchStudentsByName(String name);

    DashboardStatResponse getDashboardStats(UUID mentorId);
}
