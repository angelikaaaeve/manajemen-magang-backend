package com.bsi.manajement_magang.modules.data_kegiatan.service;

import com.bsi.manajement_magang.modules.data_kegiatan.schema.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schema.response.ActivityStatResponse;

import java.util.List;
import java.util.UUID;

public interface DataKegiatanService {

    List<ActivityResponse> listActivities(String status, String namaMahasiswa);

    ActivityResponse updateStatus(UUID id, String status);

    void deleteActivity(UUID id);

    ActivityStatResponse getActivityStatistics(String status, String namaMahasiswa);

    String getActivityFileUrl(UUID id);
}
