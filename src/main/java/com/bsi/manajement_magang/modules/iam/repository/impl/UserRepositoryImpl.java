package com.bsi.manajement_magang.modules.iam.repository.impl;

import com.bsi.manajement_magang.modules.iam.repository.UserRepository;
import com.bsi.manajement_magang.modules.iam.repository.mapper.MahasiswaRowMapper;
import com.bsi.manajement_magang.modules.iam.repository.mapper.MentorRowMapper;
import com.bsi.manajement_magang.modules.iam.repository.mapper.UserRowMapper;
import com.bsi.manajement_magang.modules.iam.schema.entity.MahasiswaEntity;
import com.bsi.manajement_magang.modules.iam.schema.entity.MentorEntity;
import com.bsi.manajement_magang.modules.iam.schema.entity.UserEntity;
import com.bsi.manajement_magang.util.SqlLoader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private final UserRowMapper userRowMapper = new UserRowMapper();
    private final MahasiswaRowMapper mahasiswaRowMapper = new MahasiswaRowMapper();
    private final MentorRowMapper mentorRowMapper = new MentorRowMapper();

    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void saveUser(UserEntity user) {
        String sql = SqlLoader.load("iam/insert_user.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole().name())
                .addValue("is_active", user.isActive());
        jdbc.update(sql, params);
    }

    @Override
    public void saveMahasiswa(MahasiswaEntity mahasiswa) {
        String sql = SqlLoader.load("iam/insert_mahasiswa.sql");
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

    @Override
    public void saveMentor(MentorEntity mentor) {
        String sql = SqlLoader.load("iam/insert_mentor.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", mentor.getId())
                .addValue("user_id", mentor.getUserId())
                .addValue("nama", mentor.getNama());
        jdbc.update(sql, params);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        String sql = SqlLoader.load("iam/find_user_by_email.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        return jdbc.query(sql, params, userRowMapper).stream().findFirst();
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        String sql = SqlLoader.load("iam/find_user_by_id.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, userRowMapper).stream().findFirst();
    }

    @Override
    public Optional<MahasiswaEntity> findMahasiswaByUserId(UUID userId) {
        String sql = SqlLoader.load("iam/find_mahasiswa_by_user_id.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(sql, params, mahasiswaRowMapper).stream().findFirst();
    }

    @Override
    public Optional<MentorEntity> findMentorByUserId(UUID userId) {
        String sql = SqlLoader.load("iam/find_mentor_by_user_id.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(sql, params, mentorRowMapper).stream().findFirst();
    }

    @Override
    public void updateUser(UserEntity user) {
        String sql = SqlLoader.load("iam/update_user.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail());
        jdbc.update(sql, params);
    }

    @Override
    public void updateMahasiswa(MahasiswaEntity mahasiswa) {
        String sql = SqlLoader.load("iam/update_mahasiswa.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", mahasiswa.getUserId())
                .addValue("nim", mahasiswa.getNim())
                .addValue("nama", mahasiswa.getNama())
                .addValue("noHp", mahasiswa.getNoHp())
                .addValue("gender", mahasiswa.getGender())
                .addValue("id_university", mahasiswa.getIdUniversity());
        jdbc.update(sql, params);
    }

    @Override
    public void updateMentor(MentorEntity mentor) {
        String sql = SqlLoader.load("iam/update_mentor.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", mentor.getUserId())
                .addValue("nama", mentor.getNama());
        jdbc.update(sql, params);
    }

    @Override
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
            throw new IllegalStateException("Failed to create university: " + name);
        }
        return id;
    }
}
