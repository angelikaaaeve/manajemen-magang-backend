package com.bsi.manajement_magang.modules.iam;

import com.bsi.manajement_magang.modules.iam.schemas.entity.MahasiswaEntity;
import com.bsi.manajement_magang.modules.iam.schemas.entity.MentorEntity;
import com.bsi.manajement_magang.modules.iam.schemas.entity.UserEntity;
import com.bsi.manajement_magang.shared.DomainException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private final UserRowMapper userRowMapper = new UserRowMapper();
    private final MahasiswaRowMapper mahasiswaRowMapper = new MahasiswaRowMapper();
    private final MentorRowMapper mentorRowMapper = new MentorRowMapper();

    public UserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void saveUser(UserEntity user) {
        String sql = "INSERT INTO \"user\" (id, email, password, role, is_active, created_at, updated_at) " +
                     "VALUES (:id, :email, :password, :role, :is_active, NOW(), NOW())";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole().name())
                .addValue("is_active", user.isActive());
        jdbc.update(sql, params);
    }

    public void saveMahasiswa(MahasiswaEntity mahasiswa) {
        String sql = "INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp, gender, id_university) " +
                     "VALUES (:id, :user_id, :nim, :nama, :no_hp, :gender, :id_university)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", mahasiswa.getId())
                .addValue("user_id", mahasiswa.getUserId())
                .addValue("nim", mahasiswa.getNim())
                .addValue("nama", mahasiswa.getNama())
                .addValue("no_hp", mahasiswa.getNoHp())
                .addValue("gender", mahasiswa.getGender())
                .addValue("id_university", mahasiswa.getIdUniversity());
        jdbc.update(sql, params);
    }

    public void saveMentor(MentorEntity mentor) {
        String sql = "INSERT INTO mentor (id, user_id, nama) VALUES (:id, :user_id, :nama)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", mentor.getId())
                .addValue("user_id", mentor.getUserId())
                .addValue("nama", mentor.getNama());
        jdbc.update(sql, params);
    }

    public Optional<UserEntity> findByEmail(String email) {
        String sql = "SELECT id, email, password, role, is_active, created_at, updated_at " +
                     "FROM \"user\" WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        return jdbc.query(sql, params, userRowMapper).stream().findFirst();
    }

    public Optional<UserEntity> findById(UUID id) {
        String sql = "SELECT id, email, password, role, is_active, created_at, updated_at " +
                     "FROM \"user\" WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, userRowMapper).stream().findFirst();
    }

    public Optional<MahasiswaEntity> findMahasiswaByUserId(UUID userId) {
        String sql = "SELECT m.id, m.user_id, m.nim, m.nama, m.no_hp, m.gender, m.id_university, univ.name_university AS universitas " +
                     "FROM mahasiswa m LEFT JOIN university univ ON m.id_university = univ.id " +
                     "WHERE m.user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(sql, params, mahasiswaRowMapper).stream().findFirst();
    }

    public Optional<MentorEntity> findMentorByUserId(UUID userId) {
        String sql = "SELECT id, user_id, nama FROM mentor WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(sql, params, mentorRowMapper).stream().findFirst();
    }

    public void updateUser(UserEntity user) {
        String sql = "UPDATE \"user\" SET email = :email, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail());
        jdbc.update(sql, params);
    }

    public void updateMahasiswa(MahasiswaEntity mahasiswa) {
        String sql = "UPDATE mahasiswa SET nim = :nim, nama = :nama, no_hp = :noHp, gender = :gender, id_university = :id_university " +
                     "WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", mahasiswa.getUserId())
                .addValue("nim", mahasiswa.getNim())
                .addValue("nama", mahasiswa.getNama())
                .addValue("noHp", mahasiswa.getNoHp())
                .addValue("gender", mahasiswa.getGender())
                .addValue("id_university", mahasiswa.getIdUniversity());
        jdbc.update(sql, params);
    }

    public void updateMentor(MentorEntity mentor) {
        String sql = "UPDATE mentor SET nama = :nama WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", mentor.getUserId())
                .addValue("nama", mentor.getNama());
        jdbc.update(sql, params);
    }

    public Long findOrCreateUniversityByName(String name) {
        // Try to find existing by name (case-insensitive)
        String findSql = "SELECT id FROM university WHERE LOWER(name_university) = LOWER(:name) LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("name", name);
        List<Long> result = jdbc.queryForList(findSql, params, Long.class);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Create new
        String insertSql = "INSERT INTO university (name_university, created_at) VALUES (:name, NOW()) RETURNING id";
        Long id = jdbc.queryForObject(insertSql, params, Long.class);
        if (id == null) {
            throw DomainException.databaseError("Failed to create university: " + name);
        }
        return id;
    }
}
