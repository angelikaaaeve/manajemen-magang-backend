package com.bsi.manajement_magang.modules.data_absensi.schema.request;

public record SubmitAbsensiRequest(
        String status,
        String keterangan,
        String attachmentUrl
) {}
