package com.bsi.manajement_magang.modules.surat_keterangan.schemas.response;

public record SuratKeteranganStatResponse(
    long totalSuratDiunggah,
    long totalSuratBelumDiunggah,
    long totalJumlahSurat
) {}
