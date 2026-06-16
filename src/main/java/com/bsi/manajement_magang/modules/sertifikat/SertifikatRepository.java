package com.bsi.manajement_magang.modules.sertifikat;

import com.bsi.manajement_magang.modules.sertifikat.schemas.request.SertifikatRequest;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatResponse;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatStatResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class SertifikatRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public SertifikatRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // List all certificates with filters
    public List<SertifikatResponse> listSertifikat(String status, String namaMahasiswa, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       s.id as sertifikat_id, s.url, s.created_at " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN sertifikat s ON pm.id = s.periode_magang_id " +
            "WHERE pm.status = 'aktif' " // We filter only for active period students
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("semua status")) {
            if (status.equalsIgnoreCase("sudah diunggah")) {
                sql.append("AND s.id IS NOT NULL ");
            } else if (status.equalsIgnoreCase("belum diunggah")) {
                sql.append("AND s.id IS NULL ");
            }
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            sql.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        sql.append("ORDER BY m.nama ASC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbc.query(sql.toString(), params, this::mapSertifikatResponse);
    }

    public long countSertifikat(String status, String namaMahasiswa) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN sertifikat s ON pm.id = s.periode_magang_id " +
            "WHERE pm.status = 'aktif' "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("semua status")) {
            if (status.equalsIgnoreCase("sudah diunggah")) {
                sql.append("AND s.id IS NOT NULL ");
            } else if (status.equalsIgnoreCase("belum diunggah")) {
                sql.append("AND s.id IS NULL ");
            }
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            sql.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    // Find by ID
    public Optional<SertifikatResponse> findById(UUID id) {
        String sql =
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       s.id as sertifikat_id, s.url, s.created_at " +
            "FROM sertifikat s " +
            "JOIN periode_magang pm ON s.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE s.id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapSertifikatResponse).stream().findFirst();
    }

    // Find by PeriodeMagang ID
    public Optional<SertifikatResponse> findByPeriodeMagangId(UUID periodId) {
        String sql =
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       s.id as sertifikat_id, s.url, s.created_at " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN sertifikat s ON pm.id = s.periode_magang_id " +
            "WHERE pm.id = :periodId";

        MapSqlParameterSource params = new MapSqlParameterSource("periodId", periodId);
        return jdbc.query(sql, params, this::mapSertifikatResponse).stream().findFirst();
    }

    // Save / Insert new certificate record
    public void save(UUID id, SertifikatRequest req) {
        String sql = "INSERT INTO sertifikat (id, periode_magang_id, url, created_at) " +
                     "VALUES (:id, :periodeMagangId, :url, NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("periodeMagangId", req.periodeMagangId())
                .addValue("url", req.url());

        jdbc.update(sql, params);
    }

    // Update existing certificate URL
    public void update(UUID id, String url) {
        String sql = "UPDATE sertifikat SET url = :url WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("url", url);
        jdbc.update(sql, params);
    }

    // Get statistics
    public SertifikatStatResponse getSertifikatStatistics(String namaMahasiswa) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder filterClause = new StringBuilder();

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            filterClause.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        // 1. Total Jumlah Sertifikat (Active Periods)
        String sqlTotal =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE pm.status = 'aktif' " + filterClause;
        Long total = jdbc.queryForObject(sqlTotal, params, Long.class);

        // 2. Total Sertifikat Diunggah
        String sqlUploaded =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "JOIN sertifikat s ON pm.id = s.periode_magang_id " +
            "WHERE pm.status = 'aktif' " + filterClause;
        Long uploaded = jdbc.queryForObject(sqlUploaded, params, Long.class);

        // 3. Total Sertifikat Belum Diunggah
        String sqlUnuploaded =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN sertifikat s ON pm.id = s.periode_magang_id " +
            "WHERE pm.status = 'aktif' AND s.id IS NULL " + filterClause;
        Long unuploaded = jdbc.queryForObject(sqlUnuploaded, params, Long.class);

        return new SertifikatStatResponse(
            uploaded != null ? uploaded : 0L,
            unuploaded != null ? unuploaded : 0L,
            total != null ? total : 0L
        );
    }

    // Check if period exists
    public boolean existsPeriod(UUID periodId) {
        String sql = "SELECT COUNT(1) FROM periode_magang WHERE id = :periodId";
        MapSqlParameterSource params = new MapSqlParameterSource("periodId", periodId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // Row mapper helper
    private SertifikatResponse mapSertifikatResponse(ResultSet rs, int rowNum) throws SQLException {
        String sId = rs.getString("sertifikat_id");
        UUID certificateId = sId != null ? UUID.fromString(sId) : null;

        String url = rs.getString("url");
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;

        String statusSertifikat = sId != null ? "Sudah Diunggah" : "belum diunggah";

        return new SertifikatResponse(
            certificateId,
            UUID.fromString(rs.getString("periode_id")),
            UUID.fromString(rs.getString("mahasiswa_id")),
            rs.getString("nim"),
            rs.getString("nama_mahasiswa"),
            url != null ? url : "-",
            statusSertifikat,
            createdAt
        );
    }
}
