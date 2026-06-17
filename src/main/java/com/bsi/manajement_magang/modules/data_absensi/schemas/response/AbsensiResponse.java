package com.bsi.manajement_magang.modules.data_absensi.schemas.response;

import com.bsi.manajement_magang.enums.StatusAbsensi;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AbsensiResponse(
    UUID id,
    UUID periodeMagangId,
    UUID mahasiswaId,
    UUID mentorId,
    String nim,
    String namaMahasiswa,
    LocalDate tanggal,
    StatusAbsensi status,
    String attachmentUrl
) {}
