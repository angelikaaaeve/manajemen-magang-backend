package com.bsi.manajement_magang.modules.data_kegiatan.schema;

public record ActivityStatResponse(
    long totalKegiatan,
    long disetujui,
    long ditolak
) {}
