package com.bsi.manajement_magang.modules.penilaian.schemas.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PenilaianResponse(
    UUID id,
    UUID periodeMagangId,
    UUID mahasiswaId,
    String nim,
    String namaMahasiswa,
    UUID mentorId,
    String namaMentor,
    BigDecimal kinerja,
    BigDecimal kedisiplinan,
    BigDecimal tanggungJawab,
    BigDecimal komunikasi,
    BigDecimal sikap,
    BigDecimal kerapihan,
    BigDecimal absensi,
    BigDecimal kerjasama,
    BigDecimal nilaiTotal,
    String catatan,
    String statusPenilaian
) {}
