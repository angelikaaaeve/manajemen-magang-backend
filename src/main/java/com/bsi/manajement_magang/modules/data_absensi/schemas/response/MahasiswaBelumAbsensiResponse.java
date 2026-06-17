package com.bsi.manajement_magang.modules.data_absensi.schemas.response;

import java.time.LocalDate;
import java.util.UUID;

public record MahasiswaBelumAbsensiResponse(
    UUID mahasiswaId,
    String nim,
    String nama,
    String noHp,
    UUID periodeMagangId,
    LocalDate tanggalMulai,
    LocalDate tanggalBerakhir
) {}
