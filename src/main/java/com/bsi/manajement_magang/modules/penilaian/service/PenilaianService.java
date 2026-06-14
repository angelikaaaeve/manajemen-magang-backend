package com.bsi.manajement_magang.modules.penilaian.service;

import com.bsi.manajement_magang.modules.penilaian.schema.request.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schema.response.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schema.response.PenilaianStatResponse;

import java.util.List;

public interface PenilaianService {

    List<PenilaianResponse> listPenilaian(String status, String namaMahasiswa);

    PenilaianResponse editPenilaian(PenilaianRequest req);

    PenilaianStatResponse getPenilaianStatistics(String namaMahasiswa);
}
