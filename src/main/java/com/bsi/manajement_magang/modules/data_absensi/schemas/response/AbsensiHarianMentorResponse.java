package com.bsi.manajement_magang.modules.data_absensi.schemas.response;

import java.time.LocalDate;
import java.util.UUID;

public record AbsensiHarianMentorResponse(
    UUID mahasiswaId,
    String nim,
    String nama,
    String noHp,
    UUID periodeMagangId,
    LocalDate tanggalMulai,
    LocalDate tanggalBerakhir,
    UUID absensiId,       // null jika belum ada record → tampilkan sebagai alfa
    String absensiStatus  // "hadir" | "izin" | "sakit" | "alpha"
) {}
