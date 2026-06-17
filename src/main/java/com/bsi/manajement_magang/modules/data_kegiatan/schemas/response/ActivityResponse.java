package com.bsi.manajement_magang.modules.data_kegiatan.schemas.response;

import com.bsi.manajement_magang.enums.StatusKegiatan;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ActivityResponse(
    UUID id,
    UUID mahasiswaId,
    String namaMahasiswa,
    String judul,
    String deskripsi,
    OffsetDateTime waktu,
    List<String> fileUrls,
    StatusKegiatan status,
    String namaMentor
) {}
