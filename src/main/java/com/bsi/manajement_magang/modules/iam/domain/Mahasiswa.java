package com.bsi.manajement_magang.modules.iam.domain;

import java.util.UUID;

public class Mahasiswa {
    private UUID id;
    private UUID userId;
    private String nim;
    private String nama;
    private String noHp;
    private String gender;
    private Long idUniversity;
    // Transient: populated by JOIN with university table, not stored in mahasiswa
    private String universitas;

    public Mahasiswa() {}

    public Mahasiswa(UUID id, UUID userId, String nim, String nama, String noHp) {
        this.id = id;
        this.userId = userId;
        this.nim = nim;
        this.nama = nama;
        this.noHp = noHp;
    }

    public Mahasiswa(UUID id, UUID userId, String nim, String nama, String noHp, String gender, String universitas) {
        this.id = id;
        this.userId = userId;
        this.nim = nim;
        this.nama = nama;
        this.noHp = noHp;
        this.gender = gender;
        // Store as transient text (from JOIN result)
        this.universitas = universitas;
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

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getIdUniversity() {
        return idUniversity;
    }

    public void setIdUniversity(Long idUniversity) {
        this.idUniversity = idUniversity;
    }

    public String getUniversitas() {
        return universitas;
    }

    public void setUniversitas(String universitas) {
        this.universitas = universitas;
    }
}
