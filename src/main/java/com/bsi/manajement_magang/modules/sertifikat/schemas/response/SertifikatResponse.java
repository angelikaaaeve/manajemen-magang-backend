package com.bsi.manajement_magang.modules.sertifikat.schemas.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SertifikatResponse(
    UUID id,
    UUID periodeMagangId,
    UUID mahasiswaId,
    String nim,
    String namaMahasiswa,
    String url,
    String statusSertifikat,
    LocalDateTime createdAt
) {}
