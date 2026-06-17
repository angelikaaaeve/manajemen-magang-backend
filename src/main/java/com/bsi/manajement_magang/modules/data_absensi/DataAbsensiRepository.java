package com.bsi.manajement_magang.modules.data_absensi;

import com.bsi.manajement_magang.enums.StatusAbsensi;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schemas.response.AbsensiHarianMentorResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class DataAbsensiRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public DataAbsensiRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ========================================================
    // MENTOR-SIDE QUERIES
    // ========================================================

    /**
     * Ambil SEMUA mahasiswa yang periode magangnya mencakup tanggal tertentu.
     * Tidak filter berdasarkan pm.status — periode yang sudah selesai pun tetap
     * ditampilkan jika tanggal yang dicari masuk dalam range-nya, sehingga
     * absensi tanggal lampau tetap bisa dilihat/dicatat.
     */
    public List<AbsensiHarianMentorResponse> listAbsensiHarianMentor(
            LocalDate tanggal, int limit, int offset) {
        String sql =
            "SELECT m.id AS mahasiswa_id, m.nim, m.nama, m.no_hp, " +
            "       pm.id AS periode_magang_id, pm.tanggal_mulai, pm.tanggal_berakhir, " +
            "       a.id AS absensi_id, " +
            "       COALESCE(a.status, 'alpha') AS absensi_status " +
            "FROM mahasiswa m " +
            "JOIN periode_magang pm " +
            "  ON pm.mahasiswa_id = m.id " +
            "  AND :tanggal BETWEEN pm.tanggal_mulai AND pm.tanggal_berakhir " +
            "LEFT JOIN absensi a ON a.periode_magang_id = pm.id AND a.tanggal = :tanggal " +
            "ORDER BY m.nama ASC " +
            "LIMIT :limit OFFSET :offset";
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("tanggal", tanggal)
            .addValue("limit", limit)
            .addValue("offset", offset);
        return jdbc.query(sql, params, this::mapAbsensiHarianMentor);
    }

    public long countAbsensiHarianMentor(LocalDate tanggal) {
        String sql =
            "SELECT COUNT(1) " +
            "FROM mahasiswa m " +
            "JOIN periode_magang pm " +
            "  ON pm.mahasiswa_id = m.id " +
            "  AND :tanggal BETWEEN pm.tanggal_mulai AND pm.tanggal_berakhir";
        Long count = jdbc.queryForObject(sql, new MapSqlParameterSource("tanggal", tanggal), Long.class);
        return count != null ? count : 0L;
    }

    /** Resolve mentor.id dari user_id — dipakai untuk mencatat siapa yang input absensi. */
    public Optional<UUID> findMentorIdByUserId(UUID userId) {
        String sql = "SELECT id FROM mentor WHERE user_id = :userId";
        return jdbc.queryForList(sql, new MapSqlParameterSource("userId", userId), UUID.class)
                   .stream().findFirst();
    }

    /** Cari periode_magang aktif milik mahasiswa berdasarkan mahasiswa.id. */
    public Optional<UUID> findActivePeriodeByMahasiswaId(UUID mahasiswaId) {
        String sql =
            "SELECT id FROM periode_magang " +
            "WHERE mahasiswa_id = :mahasiswaId AND status = 'aktif' " +
            "ORDER BY created_at DESC LIMIT 1";
        return jdbc.queryForList(sql, new MapSqlParameterSource("mahasiswaId", mahasiswaId), UUID.class)
                   .stream().findFirst();
    }

    // ========================================================
    // SHARED LIST + FIND QUERIES
    // ========================================================

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

    public void deleteAbsensi(UUID id) {
        String sql = "DELETE FROM absensi WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }

    public AbsensiStatResponse getAbsensiStatistics(String namaMahasiswa) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder filterClause = new StringBuilder();

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            filterClause.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        String sqlHadir =
            "SELECT COUNT(1) FROM absensi a " +
            "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE a.status = 'hadir' " + filterClause;
        Long totalHadir = jdbc.queryForObject(sqlHadir, params, Long.class);

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
    // MAHASISWA-SIDE QUERIES
    // ========================================================

    public Optional<UUID> findActivePeriodeByUserId(UUID userId) {
        String sql =
            "SELECT pm.id " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE m.user_id = :userId AND pm.status = 'aktif' " +
            "ORDER BY pm.created_at DESC " +
            "LIMIT 1";
        return jdbc.queryForList(sql, new MapSqlParameterSource("userId", userId), UUID.class)
                   .stream().findFirst();
    }

    /** Resolve mahasiswa.id dari user_id (JWT principal). */
    public Optional<UUID> findMahasiswaIdByUserId(UUID userId) {
        String sql = "SELECT id FROM mahasiswa WHERE user_id = :userId";
        return jdbc.queryForList(sql, new MapSqlParameterSource("userId", userId), UUID.class)
                   .stream().findFirst();
    }

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
     * Insert record absensi baru.
     * mahasiswaId wajib diisi; mentorId boleh null (self-submit oleh mahasiswa).
     */
    public UUID insertAbsensi(UUID periodeMagangId, UUID mahasiswaId, UUID mentorId,
                              LocalDate tanggal, String status, String attachmentUrl) {
        UUID id = UUID.randomUUID();
        String sql =
            "INSERT INTO absensi " +
            "(id, periode_magang_id, mahasiswa_id, mentor_id, tanggal, status, attachment_url) " +
            "VALUES (:id, :periodeMagangId, :mahasiswaId, :mentorId, :tanggal, :status, :attachmentUrl)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("periodeMagangId", periodeMagangId)
                .addValue("mahasiswaId", mahasiswaId)
                .addValue("mentorId", mentorId)
                .addValue("tanggal", tanggal)
                .addValue("status", status)
                .addValue("attachmentUrl", attachmentUrl);
        jdbc.update(sql, params);
        return id;
    }

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
        Long count = jdbc.queryForObject(sql, new MapSqlParameterSource("userId", userId), Long.class);
        return count != null ? count : 0L;
    }

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
        Long count = jdbc.queryForObject(sql, new MapSqlParameterSource("userId", userId), Long.class);
        return count != null ? count : 0L;
    }

    // ========================================================
    // Row mappers
    // ========================================================

    private AbsensiResponse mapAbsensiResponse(ResultSet rs, int rowNum) throws SQLException {
        return new AbsensiResponse(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("periode_magang_id")),
            UUID.fromString(rs.getString("mahasiswa_id")),
            rs.getString("nim"),
            rs.getString("nama_mahasiswa"),
            rs.getDate("tanggal").toLocalDate(),
            rs.getString("status") != null ? StatusAbsensi.fromString(rs.getString("status")) : null,
            rs.getString("attachment_url")
        );
    }

    private AbsensiHarianMentorResponse mapAbsensiHarianMentor(ResultSet rs, int rowNum) throws SQLException {
        String absensiIdStr = rs.getString("absensi_id");
        return new AbsensiHarianMentorResponse(
            UUID.fromString(rs.getString("mahasiswa_id")),
            rs.getString("nim"),
            rs.getString("nama"),
            rs.getString("no_hp"),
            UUID.fromString(rs.getString("periode_magang_id")),
            rs.getDate("tanggal_mulai").toLocalDate(),
            rs.getDate("tanggal_berakhir").toLocalDate(),
            absensiIdStr != null ? UUID.fromString(absensiIdStr) : null,
            rs.getString("absensi_status")
        );
    }
}
