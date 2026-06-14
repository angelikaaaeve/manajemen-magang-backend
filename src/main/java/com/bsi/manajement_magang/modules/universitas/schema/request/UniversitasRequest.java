package com.bsi.manajement_magang.modules.universitas.schema.request;

import jakarta.validation.constraints.NotBlank;

public record UniversitasRequest(
    @NotBlank(message = "Name of university is required")
    String nameUniversity
) {}
