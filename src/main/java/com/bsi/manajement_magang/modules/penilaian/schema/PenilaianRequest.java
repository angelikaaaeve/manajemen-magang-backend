package com.bsi.manajement_magang.modules.penilaian.schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PenilaianRequest(
    @NotNull(message = "Periode Magang ID is required")
    UUID periodeMagangId,

    @NotNull(message = "Mentor ID is required")
    UUID mentorId,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal kinerja,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal kedisiplinan,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal tanggungJawab,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal komunikasi,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal sikap,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal kerapihan,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal absensi,

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    BigDecimal kerjasama,

    String catatan
) {}
