package com.bsi.manajement_magang.modules.data_absensi.schema.request;

import com.bsi.manajement_magang.enums.StatusAbsensi;

/**
 * Request body for submitting daily attendance.
 * status: "hadir" | "izin" | "sakit"
 * keterangan: optional reason/description for izin & sakit
 */
public record SubmitAbsensiRequest(
    StatusAbsensi status,
    String keterangan
) {}
