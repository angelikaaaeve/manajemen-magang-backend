package com.bsi.manajement_magang.modules.universitas.schema;

import java.time.LocalDateTime;

public record UniversitasResponse(
    Long id,
    String nameUniversity,
    LocalDateTime createdAt
) {}
