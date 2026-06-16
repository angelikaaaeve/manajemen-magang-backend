package com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response;

import com.bsi.manajement_magang.enums.Gender;
import com.bsi.manajement_magang.enums.StatusPeriode;

import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
    UUID id,
    UUID userId,
    String email,
    String nim,
    String nama,
    String noHp,
    Gender gender,
    Long idUniversity,
    String universitas,
    UUID periodeId,
    LocalDate tanggalMulai,
    LocalDate tanggalBerakhir,
    StatusPeriode statusPeriode,
    UUID mentorId,
    String namaMentor
) {}
