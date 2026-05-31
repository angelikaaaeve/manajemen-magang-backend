package com.bsi.manajement_magang.modules.iam.domain;

public enum Role {
    admin,
    mahasiswa,
    mentor;

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}
