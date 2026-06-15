package com.bsi.manajement_magang.modules.data_kegiatan.schemas.response;

public record ActivityStatResponse(
    long totalKegiatan,
    long disetujui,
    long ditolak
) {}
