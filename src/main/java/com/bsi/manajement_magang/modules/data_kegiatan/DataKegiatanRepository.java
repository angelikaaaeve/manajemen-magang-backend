package com.bsi.manajement_magang.modules.data_kegiatan;

import com.bsi.manajement_magang.enums.StatusKegiatan;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityStatResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
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

    public List<ActivityResponse> listActivities(String status, String namaMahasiswa, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
            "SELECT dk.id, pm.mahasiswa_id, m.nama as nama_mahasiswa, dk.judul, dk.deskripsi, " +
            "       dk.waktu, dk.status, me.nama as nama_mentor, " +
            "       ARRAY_AGG(fk.url ORDER BY fk.created_at DESC) FILTER (WHERE fk.url IS NOT NULL) as file_urls " +
            "FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN mentor me ON dk.mentor_id = me.id " +
            "LEFT JOIN file_kegiatan fk ON dk.id = fk.data_kegiatan_id " +
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

        sql.append("GROUP BY dk.id, pm.mahasiswa_id, m.nama, dk.judul, dk.deskripsi, dk.waktu, dk.status, me.nama ");
        sql.append("ORDER BY dk.waktu DESC, m.nama ASC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbc.query(sql.toString(), params, this::mapActivityResponse);
    }

    public long countActivities(String status, String namaMahasiswa) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
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
        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    public Optional<ActivityResponse> findById(UUID id) {
        String sql =
            "SELECT dk.id, pm.mahasiswa_id, m.nama as nama_mahasiswa, dk.judul, dk.deskripsi, " +
            "       dk.waktu, dk.status, me.nama as nama_mentor, " +
            "       ARRAY_AGG(fk.url ORDER BY fk.created_at DESC) FILTER (WHERE fk.url IS NOT NULL) as file_urls " +
            "FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "LEFT JOIN mentor me ON dk.mentor_id = me.id " +
            "LEFT JOIN file_kegiatan fk ON dk.id = fk.data_kegiatan_id " +
            "WHERE dk.id = :id " +
            "GROUP BY dk.id, pm.mahasiswa_id, m.nama, dk.judul, dk.deskripsi, dk.waktu, dk.status, me.nama";
        return jdbc.query(sql, new MapSqlParameterSource("id", id), this::mapActivityResponse).stream().findFirst();
    }

    public void updateStatus(UUID id, String status) {
        String sql = "UPDATE data_kegiatan SET status = :status, updated_at = NOW() WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource().addValue("id", id).addValue("status", status.toLowerCase().trim()));
    }

    public void updateMentorId(UUID kegiatanId, UUID mentorId) {
        String sql = "UPDATE data_kegiatan SET mentor_id = :mentorId, updated_at = NOW() WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource().addValue("id", kegiatanId).addValue("mentorId", mentorId));
    }

    public void clearMentorId(UUID kegiatanId) {
        String sql = "UPDATE data_kegiatan SET mentor_id = NULL, updated_at = NOW() WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource("id", kegiatanId));
    }

    public Optional<UUID> findMentorIdByUserId(UUID userId) {
        String sql = "SELECT id FROM mentor WHERE user_id = :userId";
        List<UUID> result = jdbc.queryForList(sql, new MapSqlParameterSource("userId", userId), UUID.class);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void deleteActivity(UUID id) {
        jdbc.update("DELETE FROM data_kegiatan WHERE id = :id", new MapSqlParameterSource("id", id));
    }

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
        String base = "FROM data_kegiatan dk JOIN periode_magang pm ON dk.periode_magang_id = pm.id JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE 1=1 ";
        Long total    = jdbc.queryForObject("SELECT COUNT(1) " + base + filterClause, params, Long.class);
        Long disetujui = jdbc.queryForObject("SELECT COUNT(1) " + base + "AND dk.status = 'disetujui' " +
            (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty() ? "AND m.nama ILIKE :namaMahasiswa" : ""), params, Long.class);
        Long ditolak   = jdbc.queryForObject("SELECT COUNT(1) " + base + "AND dk.status = 'ditolak' " +
            (namaMahasiswa != null && !namaMahasiswa.trim().isEmpty() ? "AND m.nama ILIKE :namaMahasiswa" : ""), params, Long.class);
        return new ActivityStatResponse(
            total != null ? total : 0L,
            disetujui != null ? disetujui : 0L,
            ditolak != null ? ditolak : 0L
        );
    }

    public List<ActivityResponse> listActivitiesByUserId(UUID userId) {
        String sql =
            "SELECT dk.id, pm.mahasiswa_id, m.nama as nama_mahasiswa, dk.judul, dk.deskripsi, " +
            "       dk.waktu, dk.status, me.nama as nama_mentor, " +
            "       ARRAY_AGG(fk.url ORDER BY fk.created_at DESC) FILTER (WHERE fk.url IS NOT NULL) as file_urls " +
            "FROM data_kegiatan dk " +
            "JOIN periode_magang pm ON dk.periode_magang_id = pm.id " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "JOIN \"user\" u ON m.user_id = u.id " +
            "LEFT JOIN mentor me ON dk.mentor_id = me.id " +
            "LEFT JOIN file_kegiatan fk ON dk.id = fk.data_kegiatan_id " +
            "WHERE u.id = :userId " +
            "GROUP BY dk.id, pm.mahasiswa_id, m.nama, dk.judul, dk.deskripsi, dk.waktu, dk.status, me.nama " +
            "ORDER BY dk.waktu DESC";
        return jdbc.query(sql, new MapSqlParameterSource("userId", userId), this::mapActivityResponse);
    }

    public Optional<UUID> findActivePeriodIdByUserId(UUID userId) {
        String sql =
            "SELECT pm.id FROM periode_magang pm " +
            "JOIN mahasiswa m ON pm.mahasiswa_id = m.id " +
            "JOIN \"user\" u ON m.user_id = u.id " +
            "WHERE u.id = :userId AND pm.status = 'aktif' LIMIT 1";
        List<UUID> result = jdbc.queryForList(sql, new MapSqlParameterSource("userId", userId), UUID.class);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public ActivityResponse createActivity(UUID periodeId, String judul, String deskripsi) {
        UUID newId = UUID.randomUUID();
        String sql =
            "INSERT INTO data_kegiatan (id, periode_magang_id, judul, deskripsi, waktu, status, created_at, updated_at) " +
            "VALUES (:id, :periodeId, :judul, :deskripsi, NOW(), 'belum disetujui', NOW(), NOW())";
        jdbc.update(sql, new MapSqlParameterSource()
            .addValue("id", newId)
            .addValue("periodeId", periodeId)
            .addValue("judul", judul)
            .addValue("deskripsi", deskripsi));
        return findById(newId).orElseThrow(() -> new RuntimeException("Failed to retrieve created activity"));
    }

    public void insertFileKegiatan(UUID kegiatanId, String url) {
        String sql =
            "INSERT INTO file_kegiatan (id, data_kegiatan_id, url, created_at) " +
            "VALUES (:id, :kegiatanId, :url, NOW())";
        jdbc.update(sql, new MapSqlParameterSource()
            .addValue("id", UUID.randomUUID())
            .addValue("kegiatanId", kegiatanId)
            .addValue("url", url));
    }

    public void insertFilesKegiatan(UUID kegiatanId, List<String> urls) {
        for (String url : urls) {
            insertFileKegiatan(kegiatanId, url);
        }
    }

    private ActivityResponse mapActivityResponse(ResultSet rs, int rowNum) throws SQLException {
        java.sql.Timestamp tWaktu = rs.getTimestamp("waktu");
        OffsetDateTime waktu = OffsetDateTime.ofInstant(tWaktu.toInstant(), ZoneId.systemDefault());

        List<String> fileUrls = new ArrayList<>();
        Array arr = rs.getArray("file_urls");
        if (arr != null) {
            Object[] raw = (Object[]) arr.getArray();
            for (Object o : raw) {
                if (o != null) fileUrls.add(o.toString());
            }
        }

        return new ActivityResponse(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("mahasiswa_id")),
            rs.getString("nama_mahasiswa"),
            rs.getString("judul"),
            rs.getString("deskripsi"),
            waktu,
            fileUrls,
            rs.getString("status") != null ? StatusKegiatan.fromString(rs.getString("status")) : null,
            rs.getString("nama_mentor")
        );
    }
}
