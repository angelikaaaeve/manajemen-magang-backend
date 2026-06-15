package com.bsi.manajement_magang.modules.universitas.schemas.response;

import java.time.LocalDateTime;

public record UniversitasResponse(
    Long id,
    String nameUniversity,
    LocalDateTime createdAt
) {}
