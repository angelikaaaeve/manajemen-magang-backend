package com.bsi.manajement_magang.modules.iam.schema.entity;

import java.util.UUID;

public class MentorEntity {
    private UUID id;
    private UUID userId;
    private String nama;

    public MentorEntity() {}

    public MentorEntity(UUID id, UUID userId, String nama) {
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
