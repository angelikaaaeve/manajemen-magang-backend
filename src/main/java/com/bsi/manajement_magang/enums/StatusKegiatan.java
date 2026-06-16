package com.bsi.manajement_magang.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusKegiatan {
    DISETUJUI("disetujui"),
    BELUM_DISETUJUI("belum disetujui"),
    DITOLAK("ditolak");

    private final String value;

    StatusKegiatan(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusKegiatan fromString(String text) {
        for (StatusKegiatan b : StatusKegiatan.values()) {
            if (b.value.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
