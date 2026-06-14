package com.bsi.manajement_magang.modules.universitas.service;

import com.bsi.manajement_magang.modules.universitas.schema.request.UniversitasRequest;
import com.bsi.manajement_magang.modules.universitas.schema.response.UniversitasResponse;

import java.util.List;

public interface UniversitasService {

    UniversitasResponse addUniversitas(UniversitasRequest req);

    UniversitasResponse editUniversitas(Long id, UniversitasRequest req);

    List<UniversitasResponse> listUniversitas();

    void deleteUniversitas(Long id);
}
