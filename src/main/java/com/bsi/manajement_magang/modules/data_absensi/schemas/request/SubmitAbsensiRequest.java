package com.bsi.manajement_magang.modules.data_absensi.schemas.request;

public record SubmitAbsensiRequest(
        String status,
        String attachmentUrl
) {}
