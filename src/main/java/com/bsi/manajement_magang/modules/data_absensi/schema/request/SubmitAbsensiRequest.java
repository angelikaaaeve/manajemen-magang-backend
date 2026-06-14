package com.bsi.manajement_magang.modules.data_absensi.schema.request;

/**
 * Request body for submitting daily attendance.
 * status: "hadir" | "izin" | "sakit"
 * keterangan: optional reason/description for izin & sakit
 */
public record SubmitAbsensiRequest(
    String status,
    String keterangan
) {}
