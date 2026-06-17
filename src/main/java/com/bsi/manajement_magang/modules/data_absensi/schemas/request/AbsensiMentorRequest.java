package com.bsi.manajement_magang.modules.data_absensi.schemas.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record AbsensiMentorRequest(
    UUID mahasiswaId,
    String status,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate tanggal,
    String attachmentUrl
) {}
