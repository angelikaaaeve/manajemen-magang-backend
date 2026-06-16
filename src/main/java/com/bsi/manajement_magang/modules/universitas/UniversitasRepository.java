package com.bsi.manajement_magang.modules.universitas;

import com.bsi.manajement_magang.modules.universitas.schemas.response.UniversitasResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UniversitasRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public UniversitasRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<UniversitasResponse> findAll() {
        String sql = "SELECT id, name_university, created_at FROM university ORDER BY name_university ASC";
        return jdbc.query(sql, this::mapRow);
    }

    public Optional<UniversitasResponse> findById(Long id) {
        String sql = "SELECT id, name_university, created_at FROM university WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapRow).stream().findFirst();
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(1) FROM university WHERE name_university = :name";
        MapSqlParameterSource params = new MapSqlParameterSource("name", name);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        String sql = "SELECT COUNT(1) FROM university WHERE name_university = :name AND id <> :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("id", id);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public Long save(String nameUniversity) {
        String sql = "INSERT INTO university (name_university, created_at) VALUES (:name, NOW()) RETURNING id";
        MapSqlParameterSource params = new MapSqlParameterSource("name", nameUniversity);
        return jdbc.queryForObject(sql, params, Long.class);
    }

    public void update(Long id, String nameUniversity) {
        String sql = "UPDATE university SET name_university = :name WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", nameUniversity)
                .addValue("id", id);
        jdbc.update(sql, params);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM university WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbc.update(sql, params);
    }

    private UniversitasResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UniversitasResponse(
            rs.getLong("id"),
            rs.getString("name_university"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
    }
}
