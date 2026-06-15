package com.bsi.manajement_magang.modules.sertifikat.schemas.response;

public record SertifikatStatResponse(
    long totalSertifikatDiunggah,
    long totalSertifikatBelumDiunggah,
    long totalJumlahSertifikat
) {}
