package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.modules.data_absensi.schema.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.AbsensiStatResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Repository
public class DataAbsensiRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public DataAbsensiRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // List all attendance records with filters
    public List<AbsensiResponse> listAbsensi(String status, String namaMahasiswa) {
        StringBuilder sql = new StringBuilder(
            "SELECT a.id, a.periode_magang_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       a.tanggal, a.waktu_masuk, a.waktu_keluar, a.status, a.attachment_url, a.status_verifikasi " +
            "FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE 1=1 "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("semua")) {
            sql.append("AND a.status = :status ");
            params.addValue("status", status.toLowerCase().trim());
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            sql.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        sql.append("ORDER BY a.tanggal DESC, m.nama ASC");

        return jdbc.query(sql.toString(), params, this::mapAbsensiResponse);
    }

    // Find attendance record by ID
    public Optional<AbsensiResponse> findById(UUID id) {
        String sql = 
            "SELECT a.id, a.periode_magang_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       a.tanggal, a.waktu_masuk, a.waktu_keluar, a.status, a.attachment_url, a.status_verifikasi " +
            "FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE a.id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapAbsensiResponse).stream().findFirst();
    }

    // Verify/Approve attendance record
    public void verifyAbsensi(UUID id, String statusVerifikasi) {
        String sql = "UPDATE absensi SET status_verifikasi = :statusVerifikasi WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("statusVerifikasi", statusVerifikasi);
        jdbc.update(sql, params);
    }

    // Delete attendance record
    public void deleteAbsensi(UUID id) {
        String sql = "DELETE FROM absensi WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbc.update(sql, params);
    }

    // Get attendance statistics following filters
    public AbsensiStatResponse getAbsensiStatistics(String namaMahasiswa) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder filterClause = new StringBuilder();

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            filterClause.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        // Count Hadir
        String sqlHadir = 
            "SELECT COUNT(1) FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE a.status = 'hadir' " + filterClause;
        Long totalHadir = jdbc.queryForObject(sqlHadir, params, Long.class);

        // Count Izin/Sakit
        String sqlIzinSakit = 
            "SELECT COUNT(1) FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE a.status IN ('izin', 'sakit') " + filterClause;
        Long totalIzinSakit = jdbc.queryForObject(sqlIzinSakit, params, Long.class);

        return new AbsensiStatResponse(
            totalHadir != null ? totalHadir : 0L,
            totalIzinSakit != null ? totalIzinSakit : 0L
        );
    }

    // Row mapper helper
    private AbsensiResponse mapAbsensiResponse(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID periodeMagangId = UUID.fromString(rs.getString("periode_magang_id"));
        UUID mahasiswaId = UUID.fromString(rs.getString("mahasiswa_id"));
        String nim = rs.getString("nim");
        String namaMahasiswa = rs.getString("nama_mahasiswa");
        LocalDate tanggal = rs.getDate("tanggal").toLocalDate();

        java.sql.Timestamp tMasuk = rs.getTimestamp("waktu_masuk");
        java.sql.Timestamp tKeluar = rs.getTimestamp("waktu_keluar");

        OffsetDateTime waktuMasuk = tMasuk != null ? OffsetDateTime.ofInstant(tMasuk.toInstant(), ZoneId.systemDefault()) : null;
        OffsetDateTime waktuKeluar = tKeluar != null ? OffsetDateTime.ofInstant(tKeluar.toInstant(), ZoneId.systemDefault()) : null;

        return new AbsensiResponse(
            id,
            periodeMagangId,
            mahasiswaId,
            nim,
            namaMahasiswa,
            tanggal,
            waktuMasuk,
            waktuKeluar,
            rs.getString("status"),
            rs.getString("attachment_url"),
            rs.getString("status_verifikasi")
        );
    }
}
