package com.bsi.manajement_magang.modules.data_mahasiswa.service;

import com.bsi.manajement_magang.modules.data_mahasiswa.schema.request.StudentRequest;
import com.bsi.manajement_magang.modules.data_mahasiswa.schema.request.UpdateStudentRequest;
import com.bsi.manajement_magang.modules.data_mahasiswa.schema.response.StudentResponse;
import com.bsi.manajement_magang.modules.data_mahasiswa.schema.response.StudentStatResponse;

import java.util.List;
import java.util.UUID;

public interface DataMahasiswaService {

    StudentResponse addStudent(StudentRequest req);

    StudentResponse editStudent(UUID id, UpdateStudentRequest req);

    List<StudentResponse> listStudents(String gender, String universitas, String status);

    StudentResponse getStudentDetail(UUID id);

    StudentStatResponse getStudentStatistics(String gender, String universitas);

    Long getSisaWaktuMagang(UUID userId);
}
