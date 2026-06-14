package com.bsi.manajement_magang.modules.surat_keterangan.service;

import com.bsi.manajement_magang.modules.surat_keterangan.schema.request.SuratKeteranganRequest;
import com.bsi.manajement_magang.modules.surat_keterangan.schema.response.SuratKeteranganResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.schema.response.SuratKeteranganStatResponse;

import java.util.List;

public interface SuratKeteranganService {

    List<SuratKeteranganResponse> listSuratKeterangan(String status, String namaMahasiswa);

    SuratKeteranganResponse uploadSuratKeterangan(SuratKeteranganRequest req);

    SuratKeteranganStatResponse getSuratKeteranganStatistics(String namaMahasiswa);
}
