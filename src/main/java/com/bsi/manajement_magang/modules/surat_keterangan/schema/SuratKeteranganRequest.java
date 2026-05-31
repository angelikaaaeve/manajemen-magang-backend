package com.bsi.manajement_magang.modules.surat_keterangan.schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SuratKeteranganRequest(
    @NotNull(message = "Periode Magang ID is required")
    UUID periodeMagangId,

    @NotBlank(message = "URL is required")
    String url
) {}
