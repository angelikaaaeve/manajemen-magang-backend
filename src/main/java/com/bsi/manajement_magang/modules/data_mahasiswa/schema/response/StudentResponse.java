package com.bsi.manajement_magang.modules.data_mahasiswa.schema.response;

import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
    UUID id,
    UUID userId,
    String email,
    String nim,
    String nama,
    String noHp,
    String gender,
    String universitas,
    UUID periodeId,
    LocalDate tanggalMulai,
    LocalDate tanggalBerakhir,
    String statusPeriode,
    UUID mentorId,
    String namaMentor
) {}
