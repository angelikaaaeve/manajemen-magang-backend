package com.bsi.manajement_magang.modules.data_kegiatan.schema.response;

public record ActivityStatResponse(
    long totalKegiatan,
    long disetujui,
    long ditolak
) {}
