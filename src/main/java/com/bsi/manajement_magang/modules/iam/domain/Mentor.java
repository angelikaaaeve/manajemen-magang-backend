package com.bsi.manajement_magang.modules.iam.domain;

import java.util.UUID;

public class Mentor {
    private UUID id;
    private UUID userId;
    private String nama;

    public Mentor() {}

    public Mentor(UUID id, UUID userId, String nama) {
        this.id = id;
        this.userId = userId;
        this.nama = nama;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
