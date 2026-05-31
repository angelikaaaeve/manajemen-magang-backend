package com.bsi.manajement_magang.modules.penilaian.schema;

public record PenilaianStatResponse(
    long totalPenilaian,
    long totalSudahDinilai,
    long totalBelumDinilai
) {}
