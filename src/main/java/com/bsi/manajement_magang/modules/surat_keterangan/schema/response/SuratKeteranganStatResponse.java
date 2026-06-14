package com.bsi.manajement_magang.modules.surat_keterangan.schema.response;

public record SuratKeteranganStatResponse(
    long totalSuratDiunggah,
    long totalSuratBelumDiunggah,
    long totalJumlahSurat
) {}
