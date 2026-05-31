package com.bsi.manajement_magang.modules.surat_keterangan.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record SuratKeteranganResponse(
    UUID id,
    UUID periodeMagangId,
    UUID mahasiswaId,
    String nim,
    String namaMahasiswa,
    String url,
    String statusSurat,
    LocalDateTime createdAt
) {}
