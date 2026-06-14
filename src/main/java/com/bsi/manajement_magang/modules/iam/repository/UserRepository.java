package com.bsi.manajement_magang.modules.iam.repository;

import com.bsi.manajement_magang.modules.iam.schema.entity.MahasiswaEntity;
import com.bsi.manajement_magang.modules.iam.schema.entity.MentorEntity;
import com.bsi.manajement_magang.modules.iam.schema.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void saveUser(UserEntity user);
    void saveMahasiswa(MahasiswaEntity mahasiswa);
    void saveMentor(MentorEntity mentor);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(UUID id);
    Optional<MahasiswaEntity> findMahasiswaByUserId(UUID userId);
    Optional<MentorEntity> findMentorByUserId(UUID userId);

    void updateUser(UserEntity user);
    void updateMahasiswa(MahasiswaEntity mahasiswa);
    void updateMentor(MentorEntity mentor);

    /** Finds an existing university by name (case-insensitive) or creates a new one. Returns its ID. */
    Long findOrCreateUniversityByName(String name);
}
