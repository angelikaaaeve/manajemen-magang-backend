package com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response;

import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;

import java.util.List;

public record StudentDetailResponse(
    StudentResponse mahasiswa,
    AttendanceRecap rekapitulasiKehadiran,
    List<ActivityResponse> dataKegiatan,
    Integer totalNilai
) {}
