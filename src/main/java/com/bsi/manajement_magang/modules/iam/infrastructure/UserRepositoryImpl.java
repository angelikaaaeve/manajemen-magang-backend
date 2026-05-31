package com.bsi.manajement_magang.modules.iam.infrastructure;

import com.bsi.manajement_magang.modules.iam.domain.Mahasiswa;
import com.bsi.manajement_magang.modules.iam.domain.Mentor;
import com.bsi.manajement_magang.modules.iam.domain.User;
import com.bsi.manajement_magang.modules.iam.domain.UserRepository;
import com.bsi.manajement_magang.modules.iam.infrastructure.mapper.MahasiswaRowMapper;
import com.bsi.manajement_magang.modules.iam.infrastructure.mapper.MentorRowMapper;
import com.bsi.manajement_magang.modules.iam.infrastructure.mapper.UserRowMapper;
import com.bsi.manajement_magang.util.SqlLoader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public void saveUser(User user) {
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
    public void saveMahasiswa(Mahasiswa mahasiswa) {
        String sql = SqlLoader.load("iam/insert_mahasiswa.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", mahasiswa.getId())
                .addValue("user_id", mahasiswa.getUserId())
                .addValue("nim", mahasiswa.getNim())
                .addValue("nama", mahasiswa.getNama())
                .addValue("no_hp", mahasiswa.getNoHp())
                .addValue("gender", mahasiswa.getGender())
                .addValue("universitas", mahasiswa.getUniversitas());
        jdbc.update(sql, params);
    }

    @Override
    public void saveMentor(Mentor mentor) {
        String sql = SqlLoader.load("iam/insert_mentor.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", mentor.getId())
                .addValue("user_id", mentor.getUserId())
                .addValue("nama", mentor.getNama());
        jdbc.update(sql, params);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = SqlLoader.load("iam/find_user_by_email.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        return jdbc.query(sql, params, userRowMapper).stream().findFirst();
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = SqlLoader.load("iam/find_user_by_id.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, userRowMapper).stream().findFirst();
    }

    @Override
    public Optional<Mahasiswa> findMahasiswaByUserId(UUID userId) {
        String sql = SqlLoader.load("iam/find_mahasiswa_by_user_id.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(sql, params, mahasiswaRowMapper).stream().findFirst();
    }

    @Override
    public Optional<Mentor> findMentorByUserId(UUID userId) {
        String sql = SqlLoader.load("iam/find_mentor_by_user_id.sql");
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(sql, params, mentorRowMapper).stream().findFirst();
    }

    @Override
    public void updateUser(User user) {
        String sql = SqlLoader.load("iam/update_user.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail());
        jdbc.update(sql, params);
    }

    @Override
    public void updateMahasiswa(Mahasiswa mahasiswa) {
        String sql = SqlLoader.load("iam/update_mahasiswa.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", mahasiswa.getUserId())
                .addValue("nim", mahasiswa.getNim())
                .addValue("nama", mahasiswa.getNama())
                .addValue("noHp", mahasiswa.getNoHp())
                .addValue("gender", mahasiswa.getGender())
                .addValue("universitas", mahasiswa.getUniversitas());
        jdbc.update(sql, params);
    }

    @Override
    public void updateMentor(Mentor mentor) {
        String sql = SqlLoader.load("iam/update_mentor.sql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", mentor.getUserId())
                .addValue("nama", mentor.getNama());
        jdbc.update(sql, params);
    }
}
