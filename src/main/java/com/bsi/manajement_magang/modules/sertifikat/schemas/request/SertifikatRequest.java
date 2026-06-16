package com.bsi.manajement_magang.modules.sertifikat.schemas.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SertifikatRequest(
    @NotNull(message = "Periode Magang ID is required")
    UUID periodeMagangId,

    @NotBlank(message = "URL is required")
    String url
) {}
