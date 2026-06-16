package com.bsi.manajement_magang.modules.penilaian.schemas.response;

public record PenilaianStatResponse(
    long totalPenilaian,
    long totalSudahDinilai,
    long totalBelumDinilai
) {}
