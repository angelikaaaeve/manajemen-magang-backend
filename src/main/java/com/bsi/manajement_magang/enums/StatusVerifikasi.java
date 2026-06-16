package com.bsi.manajement_magang.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusVerifikasi {
    PENDING("PENDING"),
    DISETUJUI("DISETUJUI"),
    DITOLAK("DITOLAK");

    private final String value;

    StatusVerifikasi(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusVerifikasi fromString(String text) {
        for (StatusVerifikasi b : StatusVerifikasi.values()) {
            if (b.value.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
