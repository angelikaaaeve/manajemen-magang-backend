package com.bsi.manajement_magang.modules.data_mahasiswa.schemas.request;

import com.bsi.manajement_magang.enums.Gender;
import com.bsi.manajement_magang.enums.StatusPeriode;

import java.time.LocalDate;

public record UpdateStudentRequest(
    String email,
    String nim,
    String nama,
    String noHp,
    Gender gender,
    Long idUniversity,
    UpdatePeriodRequest periode
) {
    public record UpdatePeriodRequest(
        LocalDate tanggalMulai,
        LocalDate tanggalBerakhir,
        StatusPeriode status //aktif, selesai, batal
    ) {}
}
