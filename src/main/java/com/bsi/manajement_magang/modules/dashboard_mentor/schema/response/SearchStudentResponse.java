package com.bsi.manajement_magang.modules.dashboard_mentor.schema.response;

import java.time.LocalDate;
import java.util.UUID;

public record SearchStudentResponse(
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
    String statusPeriode
) {}
