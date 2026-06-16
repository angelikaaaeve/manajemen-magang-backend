package com.bsi.manajement_magang.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusPeriode {
    AKTIF("aktif"),
    SELESAI("selesai"),
    BATAL("batal");

    private final String value;

    StatusPeriode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusPeriode fromString(String text) {
        for (StatusPeriode b : StatusPeriode.values()) {
            if (b.value.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
