package com.bsi.manajement_magang.modules.penilaian.schema.response;

public record PenilaianStatResponse(
    long totalPenilaian,
    long totalSudahDinilai,
    long totalBelumDinilai
) {}
