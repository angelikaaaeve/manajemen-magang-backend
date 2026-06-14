package com.bsi.manajement_magang.modules.sertifikat.service;

import com.bsi.manajement_magang.modules.sertifikat.schema.request.SertifikatRequest;
import com.bsi.manajement_magang.modules.sertifikat.schema.response.SertifikatResponse;
import com.bsi.manajement_magang.modules.sertifikat.schema.response.SertifikatStatResponse;

import java.util.List;

public interface SertifikatService {

    List<SertifikatResponse> listSertifikat(String status, String namaMahasiswa);

    SertifikatResponse uploadSertifikat(SertifikatRequest req);

    SertifikatStatResponse getSertifikatStatistics(String namaMahasiswa);
}
