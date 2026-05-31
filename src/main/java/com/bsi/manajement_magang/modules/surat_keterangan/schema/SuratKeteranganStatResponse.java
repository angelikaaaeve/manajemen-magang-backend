package com.bsi.manajement_magang.modules.surat_keterangan.schema;

public record SuratKeteranganStatResponse(
    long totalSuratDiunggah,
    long totalSuratBelumDiunggah,
    long totalJumlahSurat
) {}
