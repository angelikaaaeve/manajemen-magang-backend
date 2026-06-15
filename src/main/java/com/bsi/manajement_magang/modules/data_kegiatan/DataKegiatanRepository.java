package com.bsi.manajement_magang.modules.data_kegiatan;

import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityStatResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Repository
public class DataKegiatanRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public DataKegiatanRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // List all activities with filters
    public List<ActivityResponse> listActivities(String status, String namaMahasiswa) {
        StringBuilder sql = new StringBuilder(
            "SELECT dk.id, pm.mahasiswa_id, m.nama as nama_mahasiswa, dk.judul, dk.deskripsi, " +
            "       dk.waktu, fk.url as file_url, dk.status " +
            "FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN ( " +
            "    SELECT DISTINCT ON (data_kegiatan_id) data_kegiatan_id, url " +
            "    FROM file_kegiatan " +
            "    ORDER BY data_kegiatan_id, created_at DESC " +
            ") fk ON dk.id = fk.data_kegiatan_id " +
            "WHERE 1=1 "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND dk.status = :status ");
            params.addValue("status", status.toLowerCase().trim());
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            sql.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        sql.append("ORDER BY dk.waktu DESC, m.nama ASC");

        return jdbc.query(sql.toString(), params, this::mapActivityResponse);
    }

    // Find activity record by ID
    public Optional<ActivityResponse> findById(UUID id) {
        String sql =
            "SELECT dk.id, pm.mahasiswa_id, m.nama as nama_mahasiswa, dk.judul, dk.deskripsi, " +
            "       dk.waktu, fk.url as file_url, dk.status " +
            "FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN ( " +
            "    SELECT DISTINCT ON (data_kegiatan_id) data_kegiatan_id, url " +
            "    FROM file_kegiatan " +
            "    ORDER BY data_kegiatan_id, created_at DESC " +
            ") fk ON dk.id = fk.data_kegiatan_id " +
            "WHERE dk.id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapActivityResponse).stream().findFirst();
    }

    // Update activity status
    public void updateStatus(UUID id, String status) {
        String sql = "UPDATE data_kegiatan SET status = :status, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status.toLowerCase().trim());
        jdbc.update(sql, params);
    }

    // Delete activity record
    public void deleteActivity(UUID id) {
        String sql = "DELETE FROM data_kegiatan WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbc.update(sql, params);
    }

    // Get activities statistics following filters
    public ActivityStatResponse getActivityStatistics(String status, String namaMahasiswa) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder filterClause = new StringBuilder();

        if (status != null && !status.trim().isEmpty()) {
            filterClause.append("AND dk.status = :status ");
            params.addValue("status", status.toLowerCase().trim());
        }

        if (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty()) {
            filterClause.append("AND m.nama ILIKE :namaMahasiswa ");
            params.addValue("namaMahasiswa", "%" + namaMahasiswa.trim() + "%");
        }

        // Count Total
        String sqlTotal =
            "SELECT COUNT(1) FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE 1=1 " + filterClause;
        Long total = jdbc.queryForObject(sqlTotal, params, Long.class);

        // Count Disetujui
        String sqlDisetujui =
            "SELECT COUNT(1) FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE dk.status = 'disetujui' " +
            (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty() ? "AND m.nama ILIKE :namaMahasiswa" : "");
        Long disetujui = jdbc.queryForObject(sqlDisetujui, params, Long.class);

        // Count Ditolak
        String sqlDitolak =
            "SELECT COUNT(1) FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "WHERE dk.status = 'ditolak' " +
            (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty() ? "AND m.nama ILIKE :namaMahasiswa" : "");
        Long ditolak = jdbc.queryForObject(sqlDitolak, params, Long.class);

        return new ActivityStatResponse(
            total != null ? total : 0L,
            disetujui != null ? disetujui : 0L,
            ditolak != null ? ditolak : 0L
        );
    }

    // Row mapper helper
    private ActivityResponse mapActivityResponse(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID mahasiswaId = UUID.fromString(rs.getString("mahasiswa_id"));
        String namaMahasiswa = rs.getString("nama_mahasiswa");
        String judul = rs.getString("judul");
        String deskripsi = rs.getString("deskripsi");

        java.sql.Timestamp tWaktu = rs.getTimestamp("waktu");
        OffsetDateTime waktu = OffsetDateTime.ofInstant(tWaktu.toInstant(), ZoneId.systemDefault());

        return new ActivityResponse(
            id,
            mahasiswaId,
            namaMahasiswa,
            judul,
            deskripsi,
            waktu,
            rs.getString("file_url"),
            rs.getString("status")
        );
    }
}
