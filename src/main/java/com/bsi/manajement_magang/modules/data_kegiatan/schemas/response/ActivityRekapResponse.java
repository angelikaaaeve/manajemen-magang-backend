package com.bsi.manajement_magang.modules.data_kegiatan.schemas.response;

import java.time.OffsetDateTime;

public record ActivityRekapResponse(
        String namaMahasiswa,
        String namaKegiatan,
        OffsetDateTime waktu
) {
}
