package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.enums.StatusAbsensi;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiStatResponse;
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
    public List<AbsensiResponse> listAbsensi(String status, String namaMahasiswa, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
            "SELECT a.id, a.periode_magang_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       a.tanggal, a.status, a.attachment_url " +
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

        sql.append("ORDER BY a.tanggal DESC, m.nama ASC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbc.query(sql.toString(), params, this::mapAbsensiResponse);
    }

    public long countAbsensi(String status, String namaMahasiswa) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) FROM absensi a " +
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

        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    // Find attendance record by ID
    public Optional<AbsensiResponse> findById(UUID id) {
        String sql =
            "SELECT a.id, a.periode_magang_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       a.tanggal, a.status, a.attachment_url " +
            "FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE a.id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapAbsensiResponse).stream().findFirst();
    }

    // Verify/Approve attendance record removed

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

    // ========================================================
    // MAHASISWA-SIDE METHODS
    // ========================================================

    /**
     * Mencari periode magang aktif milik mahasiswa berdasarkan user_id.
     * Mengembalikan periode_magang.id jika ada, kosong jika tidak.
     */
    public Optional<UUID> findActivePeriodeByUserId(UUID userId) {
        String sql =
            "SELECT pm.id " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE m.user_id = :userId AND pm.status = 'aktif' " +
            "ORDER BY pm.created_at DESC " +
            "LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.queryForList(sql, params, UUID.class).stream().findFirst();
    }

    /**
     * Cek apakah absensi pada tanggal tertentu sudah ada di periode tersebut.
     */
    public boolean existsByPeriodeAndTanggal(UUID periodeMagangId, LocalDate tanggal) {
        String sql =
            "SELECT COUNT(1) FROM absensi " +
            "WHERE periode_magang_id = :periodeMagangId AND tanggal = :tanggal";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("periodeMagangId", periodeMagangId)
                .addValue("tanggal", tanggal);
        Long count = jdbc.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    /**
     * Insert record absensi baru. attachment_url nullable (untuk izin/sakit).
     * Mengembalikan ID record yang baru dibuat.
     */
    public UUID insertAbsensi(UUID periodeMagangId, LocalDate tanggal,
                              String status, String keterangan,
                              String attachmentUrl) {
        UUID id = UUID.randomUUID();
        String sql =
            "INSERT INTO absensi " +
            "(id, periode_magang_id, tanggal, status, attachment_url) " +
            "VALUES (:id, :periodeMagangId, :tanggal, :status, :attachmentUrl)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("periodeMagangId", periodeMagangId)
                .addValue("tanggal", tanggal)
                .addValue("status", status)
                .addValue("attachmentUrl", attachmentUrl);
        jdbc.update(sql, params);
        return id;
    }

    /**
     * List riwayat absensi milik mahasiswa (berdasarkan user_id), urut terbaru.
     */
    public List<AbsensiResponse> listAbsensiByUserId(UUID userId, int limit, int offset) {
        String sql =
            "SELECT a.id, a.periode_magang_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       a.tanggal, a.status, a.attachment_url " +
            "FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE m.user_id = :userId " +
            "ORDER BY a.tanggal DESC " +
            "LIMIT :limit OFFSET :offset";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId)
            .addValue("limit", limit)
            .addValue("offset", offset);
        return jdbc.query(sql, params, this::mapAbsensiResponse);
    }

    public long countAbsensiByUserId(UUID userId) {
        String sql =
            "SELECT COUNT(1) FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE m.user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        Long count = jdbc.queryForObject(sql, params, Long.class);
        return count != null ? count : 0L;
    }

    /**
     * Statistik absensi mahasiswa: hadir, izin, sakit, alfa.
     * alfa = total hari kerja periode aktif dikurangi semua record absensi.
     */
    public AbsensiMahasiswaStatResponse getMahasiswaStat(UUID userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);

        String sqlHadir =
            "SELECT COUNT(1) FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE m.user_id = :userId AND a.status = 'hadir'";
        Long hadir = jdbc.queryForObject(sqlHadir, params, Long.class);

        String sqlIzin =
            "SELECT COUNT(1) FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE m.user_id = :userId AND a.status = 'izin'";
        Long izin = jdbc.queryForObject(sqlIzin, params, Long.class);

        String sqlSakit =
            "SELECT COUNT(1) FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE m.user_id = :userId AND a.status = 'sakit'";
        Long sakit = jdbc.queryForObject(sqlSakit, params, Long.class);

        // Alfa = total hari kerja sejak tanggal_mulai hingga hari ini (maks tanggal_berakhir)
        // dikurangi semua record absensi yang sudah masuk
        String sqlAlfa =
            "SELECT " +
            "  GREATEST(0, " +
            "    (SELECT GREATEST(0, (LEAST(CURRENT_DATE, pm2.tanggal_berakhir) - pm2.tanggal_mulai + 1)) " +
            "     FROM periode_magang pm2 JOIN mahasiswa m2 ON pm2.mahasiswa_id = m2.id " +
            "     WHERE m2.user_id = :userId ORDER BY pm2.created_at DESC LIMIT 1) " +
            "    - (SELECT COUNT(1) FROM absensi a " +
            "       JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "       JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "       WHERE m.user_id = :userId) " +
            "  )";
        Long alfa = jdbc.queryForObject(sqlAlfa, params, Long.class);

        return new AbsensiMahasiswaStatResponse(
            hadir  != null ? hadir  : 0L,
            izin   != null ? izin   : 0L,
            sakit  != null ? sakit  : 0L,
            alfa   != null ? alfa   : 0L
        );
    }

    public Long getTotalKehadiran(UUID userId) {
        String sql = "SELECT COUNT(1) FROM absensi a " +
                     "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
                     "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
                     "WHERE m.user_id = :userId AND a.status = 'hadir'";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        Long count = jdbc.queryForObject(sql, params, Long.class);
        return count != null ? count : 0L;
    }

    // ========================================================
    // Row mapper helper
    // ========================================================
    private AbsensiResponse mapAbsensiResponse(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID periodeMagangId = UUID.fromString(rs.getString("periode_magang_id"));
        UUID mahasiswaId = UUID.fromString(rs.getString("mahasiswa_id"));
        String nim = rs.getString("nim");
        String namaMahasiswa = rs.getString("nama_mahasiswa");
        LocalDate tanggal = rs.getDate("tanggal").toLocalDate();

        return new AbsensiResponse(
            id,
            periodeMagangId,
            mahasiswaId,
            nim,
            namaMahasiswa,
            tanggal,
            rs.getString("status") != null ? StatusAbsensi.fromString(rs.getString("status")) : null,
            rs.getString("attachment_url")
        );
    }
}
