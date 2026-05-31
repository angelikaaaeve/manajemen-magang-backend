package com.bsi.manajement_magang.modules.iam.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void saveUser(User user);
    void saveMahasiswa(Mahasiswa mahasiswa);
    void saveMentor(Mentor mentor);
    
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    Optional<Mahasiswa> findMahasiswaByUserId(UUID userId);
    Optional<Mentor> findMentorByUserId(UUID userId);
    
    void updateUser(User user);
    void updateMahasiswa(Mahasiswa mahasiswa);
    void updateMentor(Mentor mentor);
}
