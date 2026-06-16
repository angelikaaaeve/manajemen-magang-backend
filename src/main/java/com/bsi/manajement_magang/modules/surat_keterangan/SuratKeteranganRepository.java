package com.bsi.manajement_magang.modules.surat_keterangan;

import com.bsi.manajement_magang.modules.surat_keterangan.schemas.request.SuratKeteranganRequest;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganStatResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class SuratKeteranganRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public SuratKeteranganRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // List all letters with filters
    public List<SuratKeteranganResponse> listSuratKeterangan(String status, String namaMahasiswa, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       sk.id as surat_id, sk.url, sk.created_at " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id " +
            "WHERE pm.status = 'aktif' " // We filter only for active period students
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("semua status")) {
            if (status.equalsIgnoreCase("sudah diunggah")) {
                sql.append("AND sk.id IS NOT NULL ");
            } else if (status.equalsIgnoreCase("belum diunggah")) {
                sql.append("AND sk.id IS NULL ");
            }
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            sql.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        sql.append("ORDER BY m.nama ASC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbc.query(sql.toString(), params, this::mapSuratKeteranganResponse);
    }

    public long countSuratKeterangan(String status, String namaMahasiswa) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id " +
            "WHERE pm.status = 'aktif' "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("semua status")) {
            if (status.equalsIgnoreCase("sudah diunggah")) {
                sql.append("AND sk.id IS NOT NULL ");
            } else if (status.equalsIgnoreCase("belum diunggah")) {
                sql.append("AND sk.id IS NULL ");
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
    public Optional<SuratKeteranganResponse> findById(UUID id) {
        String sql =
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       sk.id as surat_id, sk.url, sk.created_at " +
            "FROM surat_keterangan_magang sk " +
            "JOIN periode_magang pm ON sk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE sk.id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapSuratKeteranganResponse).stream().findFirst();
    }

    // Find by PeriodeMagang ID
    public Optional<SuratKeteranganResponse> findByPeriodeMagangId(UUID periodId) {
        String sql =
            "SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, " +
            "       sk.id as surat_id, sk.url, sk.created_at " +
            "FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id " +
            "WHERE pm.id = :periodId";

        MapSqlParameterSource params = new MapSqlParameterSource("periodId", periodId);
        return jdbc.query(sql, params, this::mapSuratKeteranganResponse).stream().findFirst();
    }

    // Save / Insert new letter record
    public void save(UUID id, SuratKeteranganRequest req) {
        String sql = "INSERT INTO surat_keterangan_magang (id, periode_magang_id, url, created_at) " +
                     "VALUES (:id, :periodeMagangId, :url, NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("periodeMagangId", req.periodeMagangId())
                .addValue("url", req.url());

        jdbc.update(sql, params);
    }

    // Update existing letter URL
    public void update(UUID id, String url) {
        String sql = "UPDATE surat_keterangan_magang SET url = :url WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("url", url);
        jdbc.update(sql, params);
    }

    // Get statistics
    public SuratKeteranganStatResponse getSuratKeteranganStatistics(String namaMahasiswa) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder filterClause = new StringBuilder();

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            filterClause.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        // 1. Total Jumlah Surat Keterangan (Active Periods)
        String sqlTotal =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE pm.status = 'aktif' " + filterClause;
        Long total = jdbc.queryForObject(sqlTotal, params, Long.class);

        // 2. Total Surat Keterangan Diunggah
        String sqlUploaded =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id " +
            "WHERE pm.status = 'aktif' " + filterClause;
        Long uploaded = jdbc.queryForObject(sqlUploaded, params, Long.class);

        // 3. Total Surat Keterangan Belum Diunggah
        String sqlUnuploaded =
            "SELECT COUNT(1) FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id " +
            "WHERE pm.status = 'aktif' AND sk.id IS NULL " + filterClause;
        Long unuploaded = jdbc.queryForObject(sqlUnuploaded, params, Long.class);

        return new SuratKeteranganStatResponse(
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
    private SuratKeteranganResponse mapSuratKeteranganResponse(ResultSet rs, int rowNum) throws SQLException {
        String sId = rs.getString("surat_id");
        UUID letterId = sId != null ? UUID.fromString(sId) : null;

        String url = rs.getString("url");
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;

        String statusSurat = sId != null ? "Sudah Diunggah" : "belum diunggah";

        return new SuratKeteranganResponse(
            letterId,
            UUID.fromString(rs.getString("periode_id")),
            UUID.fromString(rs.getString("mahasiswa_id")),
            rs.getString("nim"),
            rs.getString("nama_mahasiswa"),
            url != null ? url : "-",
            statusSurat,
            createdAt
        );
    }
}
