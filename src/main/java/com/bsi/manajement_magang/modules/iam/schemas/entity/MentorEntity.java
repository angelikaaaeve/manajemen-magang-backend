package com.bsi.manajement_magang.modules.iam.schemas.entity;

import java.util.UUID;

public class MentorEntity {
    private UUID id;
    private UUID userId;
    private String nama;
    private String noHp;

    public MentorEntity() {}

    public MentorEntity(UUID id, UUID userId, String nama, String noHp) {
        this.id = id;
        this.userId = userId;
        this.nama = nama;
        this.noHp = noHp;
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

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
}
