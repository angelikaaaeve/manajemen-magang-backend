package com.bsi.manajement_magang.modules.data_mahasiswa.schema.request;

import java.time.LocalDate;

public record UpdateStudentRequest(
    String email,
    String nim,
    String nama,
    String noHp,
    String gender,
    String universitas,
    UpdatePeriodRequest periode
) {
    public record UpdatePeriodRequest(
        LocalDate tanggalMulai,
        LocalDate tanggalBerakhir,
        String status
    ) {}
}
