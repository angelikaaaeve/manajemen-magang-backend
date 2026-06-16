package com.bsi.manajement_magang.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusAbsensi {
    HADIR("hadir"),
    IZIN("izin"),
    SAKIT("sakit"),
    ALPHA("alpha");

    private final String value;

    StatusAbsensi(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusAbsensi fromString(String text) {
        for (StatusAbsensi b : StatusAbsensi.values()) {
            if (b.value.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
