package com.bsi.manajement_magang.modules.dashboard_mahasiswa;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DashboardMahasiswaRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public DashboardMahasiswaRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 1. Count attendance records with 'hadir' status for a student
    public long countAttendanceByMahasiswaId(UUID mahasiswaId) {
        String sql = "SELECT COUNT(*) " +
                     "FROM absensi a " +
                     "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
                     "WHERE pm.mahasiswa_id = :mahasiswaId AND a.status = 'hadir'";
        MapSqlParameterSource params = new MapSqlParameterSource("mahasiswaId", mahasiswaId);
        Long count = jdbc.queryForObject(sql, params, Long.class);
        return count != null ? count : 0L;
    }

    // 2. Find the latest period details for a student to compute remaining days
    public Optional<Map<String, Object>> findLatestPeriodByMahasiswaId(UUID mahasiswaId) {
        String sql = "SELECT tanggal_mulai, tanggal_berakhir, status " +
                     "FROM periode_magang " +
                     "WHERE mahasiswa_id = :mahasiswaId " +
                     "ORDER BY created_at DESC " +
                     "LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("mahasiswaId", mahasiswaId);
        return jdbc.queryForList(sql, params).stream().findFirst();
    }
}
