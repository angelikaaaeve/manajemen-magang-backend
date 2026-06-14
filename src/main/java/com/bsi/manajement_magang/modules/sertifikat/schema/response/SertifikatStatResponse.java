package com.bsi.manajement_magang.modules.sertifikat.schema.response;

public record SertifikatStatResponse(
    long totalSertifikatDiunggah,
    long totalSertifikatBelumDiunggah,
    long totalJumlahSertifikat
) {}
