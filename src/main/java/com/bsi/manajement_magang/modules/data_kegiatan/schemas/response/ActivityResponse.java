package com.bsi.manajement_magang.modules.data_kegiatan.schemas.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActivityResponse(
    UUID id,
    UUID mahasiswaId,
    String namaMahasiswa,
    String judul,
    String deskripsi,
    OffsetDateTime waktu,
    String fileUrl,
    String status
) {}
