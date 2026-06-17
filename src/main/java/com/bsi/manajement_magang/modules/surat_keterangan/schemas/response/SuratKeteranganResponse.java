package com.bsi.manajement_magang.modules.surat_keterangan.schemas.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SuratKeteranganResponse(
    UUID id,
    UUID periodeMagangId,
    UUID mahasiswaId,
    String nim,
    String namaMahasiswa,
    LocalDate tanggalMulai,
    LocalDate tanggalBerakhir,
    String namaMentor,
    String url,
    String statusSurat,
    LocalDateTime createdAt
) {}
