package com.bsi.manajement_magang.modules.penilaian;

import com.bsi.manajement_magang.modules.penilaian.schemas.request.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianStatResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class PenilaianRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public PenilaianRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // List all students and their assessments with filters
    public List<PenilaianResponse> listPenilaian(String status, String namaMahasiswa, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       p.id as penilaian_id, p.mentor_id, men.nama as nama_mentor, " +
            "       p.kinerja, p.kedisiplinan, p.tanggung_jawab, p.komunikasi, " +
            "       p.sikap, p.kerapihan, p.absensi, p.kerjasama, p.nilai_total, p.catatan " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN penilaian p ON pm.id = p.periode_magang_id " +
            "LEFT JOIN mentor men ON p.mentor_id = men.id " +
            "WHERE pm.status = 'aktif' " // We grade students with active periods
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("semua status")) {
            if (status.equalsIgnoreCase("sudah dinilai")) {
                sql.append("AND p.id IS NOT NULL ");
            } else if (status.equalsIgnoreCase("belum dinilai")) {
                sql.append("AND p.id IS NULL ");
            }
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            sql.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        sql.append("ORDER BY m.nama ASC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbc.query(sql.toString(), params, this::mapPenilaianResponse);
    }

    public long countPenilaian(String status, String namaMahasiswa) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN penilaian p ON pm.id = p.periode_magang_id " +
            "WHERE pm.status = 'aktif' "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("semua status")) {
            if (status.equalsIgnoreCase("sudah dinilai")) {
                sql.append("AND p.id IS NOT NULL ");
            } else if (status.equalsIgnoreCase("belum dinilai")) {
                sql.append("AND p.id IS NULL ");
            }
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            sql.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    // Find assessment detail by ID
    public Optional<PenilaianResponse> findById(UUID id) {
        String sql =
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       p.id as penilaian_id, p.mentor_id, men.nama as nama_mentor, " +
            "       p.kinerja, p.kedisiplinan, p.tanggung_jawab, p.komunikasi, " +
            "       p.sikap, p.kerapihan, p.absensi, p.kerjasama, p.nilai_total, p.catatan " +
            "FROM penilaian p " +
            "JOIN periode_magang pm ON p.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "JOIN mentor men ON p.mentor_id = men.id " +
            "WHERE p.id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapPenilaianResponse).stream().findFirst();
    }

    // Find assessment detail by PeriodeMagang ID
    public Optional<PenilaianResponse> findByPeriodeMagangId(UUID periodId) {
        String sql =
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       p.id as penilaian_id, p.mentor_id, men.nama as nama_mentor, " +
            "       p.kinerja, p.kedisiplinan, p.tanggung_jawab, p.komunikasi, " +
            "       p.sikap, p.kerapihan, p.absensi, p.kerjasama, p.nilai_total, p.catatan " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN penilaian p ON pm.id = p.periode_magang_id " +
            "LEFT JOIN mentor men ON p.mentor_id = men.id " +
            "WHERE pm.id = :periodId";

        MapSqlParameterSource params = new MapSqlParameterSource("periodId", periodId);
        return jdbc.query(sql, params, this::mapPenilaianResponse).stream().findFirst();
    }

    // Save/Insert new assessment (nilai_total is generated automatically by PostgreSQL!)
    public void save(UUID id, UUID actualMentorId, PenilaianRequest req) {
        String sql = "INSERT INTO penilaian (id, periode_magang_id, mentor_id, kinerja, kedisiplinan, " +
                     "                      tanggung_jawab, komunikasi, sikap, kerapihan, absensi, kerjasama, catatan, created_at) " +
                     "VALUES (:id, :periodeMagangId, :mentorId, :kinerja, :kedisiplinan, " +
                     "        :tanggungJawab, :komunikasi, :sikap, :kerapihan, :absensi, :kerjasama, :catatan, NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("periodeMagangId", req.periodeMagangId())
                .addValue("mentorId", actualMentorId)
                .addValue("kinerja", req.kinerja())
                .addValue("kedisiplinan", req.kedisiplinan())
                .addValue("tanggungJawab", req.tanggungJawab())
                .addValue("komunikasi", req.komunikasi())
                .addValue("sikap", req.sikap())
                .addValue("kerapihan", req.kerapihan())
                .addValue("absensi", req.absensi())
                .addValue("kerjasama", req.kerjasama())
                .addValue("catatan", req.catatan());

        jdbc.update(sql, params);
    }

    // Update existing assessment
    public void update(UUID id, UUID actualMentorId, PenilaianRequest req) {
        String sql = "UPDATE penilaian " +
                     "SET mentor_id = :mentorId, kinerja = :kinerja, kedisiplinan = :kedisiplinan, " +
                     "    tanggung_jawab = :tanggungJawab, komunikasi = :komunikasi, sikap = :sikap, " +
                     "    kerapihan = :kerapihan, absensi = :absensi, kerjasama = :kerjasama, catatan = :catatan " +
                     "WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("mentorId", actualMentorId)
                .addValue("kinerja", req.kinerja())
                .addValue("kedisiplinan", req.kedisiplinan())
                .addValue("tanggungJawab", req.tanggungJawab())
                .addValue("komunikasi", req.komunikasi())
                .addValue("sikap", req.sikap())
                .addValue("kerapihan", req.kerapihan())
                .addValue("absensi", req.absensi())
                .addValue("kerjasama", req.kerjasama())
                .addValue("catatan", req.catatan());

        jdbc.update(sql, params);
    }

    // Get statistics
    public PenilaianStatResponse getPenilaianStatistics(String namaMahasiswa) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder filterClause = new StringBuilder();

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            filterClause.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        // 1. Count Total
        String sqlTotal =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE pm.status = 'aktif' " + filterClause;
        Long total = jdbc.queryForObject(sqlTotal, params, Long.class);

        // 2. Count sudah dinilai
        String sqlSudah =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "JOIN penilaian p ON pm.id = p.periode_magang_id " +
            "WHERE pm.status = 'aktif' " + filterClause;
        Long sudah = jdbc.queryForObject(sqlSudah, params, Long.class);

        // 3. Count belum dinilai
        String sqlBelum =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN penilaian p ON pm.id = p.periode_magang_id " +
            "WHERE pm.status = 'aktif' AND p.id IS NULL " + filterClause;
        Long belum = jdbc.queryForObject(sqlBelum, params, Long.class);

        return new PenilaianStatResponse(
            total != null ? total : 0L,
            sudah != null ? sudah : 0L,
            belum != null ? belum : 0L
        );
    }

    // Find assessment for a specific mahasiswa by their user ID
    public Optional<PenilaianResponse> findByUserId(UUID userId) {
        String sql =
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       p.id as penilaian_id, p.mentor_id, men.nama as nama_mentor, " +
            "       p.kinerja, p.kedisiplinan, p.tanggung_jawab, p.komunikasi, " +
            "       p.sikap, p.kerapihan, p.absensi, p.kerjasama, p.nilai_total, p.catatan " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "JOIN \"user\" u ON m.user_id = u.id " +
            "LEFT JOIN penilaian p ON pm.id = p.periode_magang_id " +
            "LEFT JOIN mentor men ON p.mentor_id = men.id " +
            "WHERE u.id = :userId AND pm.status = 'aktif' " +
            "LIMIT 1";

        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(sql, params, this::mapPenilaianResponse).stream().findFirst();
    }

    // Check if period exists
    public boolean existsPeriod(UUID periodId) {
        String sql = "SELECT COUNT(1) FROM periode_magang WHERE id = :periodId";
        MapSqlParameterSource params = new MapSqlParameterSource("periodId", periodId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // Check if mentor exists
    public boolean existsMentor(UUID mentorId) {
        String sql = "SELECT COUNT(1) FROM mentor WHERE id = :mentorId";
        MapSqlParameterSource params = new MapSqlParameterSource("mentorId", mentorId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // Get mentor id by user id
    public Optional<UUID> findMentorIdByUserId(UUID userId) {
        String sql = "SELECT id FROM mentor WHERE user_id = :userId LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        List<UUID> results = jdbc.queryForList(sql, params, UUID.class);
        if (!results.isEmpty()) {
            return Optional.of(results.get(0));
        }
        return Optional.empty();
    }

    // Row mapper helper
    private PenilaianResponse mapPenilaianResponse(ResultSet rs, int rowNum) throws SQLException {
        String pId = rs.getString("penilaian_id");
        String mId = rs.getString("mentor_id");

        UUID penilaianId = pId != null ? UUID.fromString(pId) : null;
        UUID mentorId = mId != null ? UUID.fromString(mId) : null;

        BigDecimal kinerja = rs.getBigDecimal("kinerja");
        BigDecimal kedisiplinan = rs.getBigDecimal("kedisiplinan");
        BigDecimal tanggungJawab = rs.getBigDecimal("tanggung_jawab");
        BigDecimal komunikasi = rs.getBigDecimal("komunikasi");
        BigDecimal sikap = rs.getBigDecimal("sikap");
        BigDecimal kerapihan = rs.getBigDecimal("kerapihan");
        BigDecimal absensi = rs.getBigDecimal("absensi");
        BigDecimal kerjasama = rs.getBigDecimal("kerjasama");
        BigDecimal nilaiTotal = rs.getBigDecimal("nilai_total");
        String catatan = rs.getString("catatan");

        String statusPenilaian = pId != null ? "SUDAH_DINILAI" : "BELUM_DINILAI";

        return new PenilaianResponse(
            penilaianId,
            UUID.fromString(rs.getString("periode_id")),
            UUID.fromString(rs.getString("mahasiswa_id")),
            rs.getString("nim"),
            rs.getString("nama_mahasiswa"),
            mentorId,
            rs.getString("nama_mentor"),
            kinerja,
            kedisiplinan,
            tanggungJawab,
            komunikasi,
            sikap,
            kerapihan,
            absensi,
            kerjasama,
            nilaiTotal,
            catatan != null ? catatan : "-",
            statusPenilaian
        );
    }
}
