package com.bsi.manajement_magang.modules.data_mahasiswa;

import com.bsi.manajement_magang.enums.Gender;
import com.bsi.manajement_magang.enums.StatusPeriode;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response.StudentResponse;
import com.bsi.manajement_magang.shared.DomainException;
import com.bsi.manajement_magang.modules.data_mahasiswa.schemas.response.StudentStatResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class DataMahasiswaRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public DataMahasiswaRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Check if email already exists
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(1) FROM \"user\" WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // Check if email already exists for another user
    public boolean existsByEmailAndIdNot(String email, UUID userId) {
        String sql = "SELECT COUNT(1) FROM \"user\" WHERE email = :email AND id <> :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("userId", userId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // Check if NIM already exists
    public boolean existsByNim(String nim) {
        String sql = "SELECT COUNT(1) FROM mahasiswa WHERE nim = :nim";
        MapSqlParameterSource params = new MapSqlParameterSource("nim", nim);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // Check if NIM already exists for another student
    public boolean existsByNimAndIdNot(String nim, UUID studentId) {
        String sql = "SELECT COUNT(1) FROM mahasiswa WHERE nim = :nim AND id <> :studentId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nim", nim)
                .addValue("studentId", studentId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // Find university ID by name (case-insensitive), or null if not found
    public Optional<Long> findUniversityIdByName(String name) {
        String sql = "SELECT id FROM university WHERE LOWER(name_university) = LOWER(:name) LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("name", name);
        List<Long> result = jdbc.queryForList(sql, params, Long.class);
        return result.stream().findFirst();
    }

    // Find or create a university by name, returning its id
    public Long findOrCreateUniversityByName(String name) {
        return findUniversityIdByName(name).orElseGet(() -> {
            String sql = "INSERT INTO university (name_university, created_at) VALUES (:name, NOW()) RETURNING id";
            MapSqlParameterSource params = new MapSqlParameterSource("name", name);
            Long id = jdbc.queryForObject(sql, params, Long.class);
            if (id == null) {
                throw DomainException.databaseError("Failed to create university: " + name);
            }
            return id;
        });
    }

    // Save user record
    public void saveUser(UUID id, String email, String hashedPassword) {
        String sql = "INSERT INTO \"user\" (id, email, password, role, is_active, created_at, updated_at) " +
                     "VALUES (:id, :email, :password, 'mahasiswa', true, NOW(), NOW())";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("email", email)
                .addValue("password", hashedPassword);
        jdbc.update(sql, params);
    }

    // Update user email
    public void updateUserEmail(UUID userId, String email) {
        String sql = "UPDATE \"user\" SET email = :email, updated_at = NOW() WHERE id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("email", email);
        jdbc.update(sql, params);
    }

    // Save mahasiswa profile (id_university is the FK to university table)
    public void saveMahasiswa(UUID id, UUID userId, String nim, String nama, String noHp, String gender, Long idUniversity) {
        String sql = "INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp, gender, id_university) " +
                     "VALUES (:id, :userId, :nim, :nama, :noHp, :gender, :idUniversity)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId)
                .addValue("nim", nim)
                .addValue("nama", nama)
                .addValue("noHp", noHp)
                .addValue("gender", gender)
                .addValue("idUniversity", idUniversity);
        jdbc.update(sql, params);
    }

    // Update mahasiswa profile (id_university is the FK to university table)
    public void updateMahasiswa(UUID id, String nim, String nama, String noHp, String gender, Long idUniversity) {
        String sql = "UPDATE mahasiswa SET nim = :nim, nama = :nama, no_hp = :noHp, gender = :gender, id_university = :idUniversity " +
                     "WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("nim", nim)
                .addValue("nama", nama)
                .addValue("noHp", noHp)
                .addValue("gender", gender)
                .addValue("idUniversity", idUniversity);
        jdbc.update(sql, params);
    }

    // Find active or latest period by student ID
    public Optional<Map<String, Object>> findLatestPeriodByStudentId(UUID studentId) {
        String sql = "SELECT id, tanggal_mulai, tanggal_berakhir, status FROM periode_magang " +
                     "WHERE mahasiswa_id = :studentId ORDER BY created_at DESC LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("studentId", studentId);
        return jdbc.queryForList(sql, params).stream().findFirst();
    }

    // Save period record
    public void savePeriod(UUID id, UUID mahasiswaId, LocalDate tanggalMulai, LocalDate tanggalBerakhir, String status) {
        String sql = "INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status, created_at) " +
                     "VALUES (:id, :mahasiswaId, :tanggalMulai, :tanggalBerakhir, :status, NOW())";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("mahasiswaId", mahasiswaId)
                .addValue("tanggalMulai", tanggalMulai)
                .addValue("tanggalBerakhir", tanggalBerakhir)
                .addValue("status", status != null ? status : "aktif");
        jdbc.update(sql, params);
    }

    // Update period record
    public void updatePeriod(UUID id, LocalDate tanggalMulai, LocalDate tanggalBerakhir, String status) {
        String sql = "UPDATE periode_magang SET tanggal_mulai = :tanggalMulai, tanggal_berakhir = :tanggalBerakhir, status = :status " +
                     "WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("tanggalMulai", tanggalMulai)
                .addValue("tanggalBerakhir", tanggalBerakhir)
                .addValue("status", status);
        jdbc.update(sql, params);
    }

    // Read list of students with filters
    public List<StudentResponse> listStudents(String gender, String universitas, String status) {
        StringBuilder sql = new StringBuilder(
            "SELECT m.id, m.user_id, u.email, m.nim, m.nama, m.no_hp, m.gender, " +
            "       m.id_university, univ.name_university AS universitas, " +
            "       pm.id as periode_id, pm.tanggal_mulai, pm.tanggal_berakhir, pm.status as status_periode, " +
            "       men.id as mentor_id, men.nama as nama_mentor " +
            "FROM mahasiswa m " +
            "JOIN \"user\" u ON m.user_id = u.id " +
            "LEFT JOIN university univ ON m.id_university = univ.id " +
            "LEFT JOIN ( " +
            "    SELECT DISTINCT ON (mahasiswa_id) id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status " +
            "    FROM periode_magang " +
            "    ORDER BY mahasiswa_id, created_at DESC " +
            ") pm ON m.id = pm.mahasiswa_id " +
            "LEFT JOIN mentor_mahasiswa mm ON m.id = mm.mahasiswa_id " +
            "LEFT JOIN mentor men ON mm.mentor_id = men.id " +
            "WHERE 1=1 "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (gender != null && !gender.trim().isEmpty()) {
            sql.append("AND m.gender = :gender ");
            params.addValue("gender", gender);
        }

        if (universitas != null && !universitas.trim().isEmpty()) {
            sql.append("AND LOWER(univ.name_university) = LOWER(:universitas) ");
            params.addValue("universitas", universitas);
        }

        if (status != null && !status.trim().isEmpty()) {
            if (status.equalsIgnoreCase("Belum Penempatan")) {
                sql.append("AND pm.status IS NULL ");
            } else {
                sql.append("AND pm.status = :status ");
                params.addValue("status", status.toLowerCase());
            }
        }

        sql.append("ORDER BY m.nama ASC");

        return jdbc.query(sql.toString(), params, this::mapStudentResponse);
    }

    // Detail student profile
    public Optional<StudentResponse> findStudentDetailById(UUID id) {
        String sql =
            "SELECT m.id, m.user_id, u.email, m.nim, m.nama, m.no_hp, m.gender, " +
            "       m.id_university, univ.name_university AS universitas, " +
            "       pm.id as periode_id, pm.tanggal_mulai, pm.tanggal_berakhir, pm.status as status_periode, " +
            "       men.id as mentor_id, men.nama as nama_mentor " +
            "FROM mahasiswa m " +
            "JOIN \"user\" u ON m.user_id = u.id " +
            "LEFT JOIN university univ ON m.id_university = univ.id " +
            "LEFT JOIN ( " +
            "    SELECT DISTINCT ON (mahasiswa_id) id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status " +
            "    FROM periode_magang " +
            "    ORDER BY mahasiswa_id, created_at DESC " +
            ") pm ON m.id = pm.mahasiswa_id " +
            "LEFT JOIN mentor_mahasiswa mm ON m.id = mm.mahasiswa_id " +
            "LEFT JOIN mentor men ON mm.mentor_id = men.id " +
            "WHERE m.id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, this::mapStudentResponse).stream().findFirst();
    }

    // Get statistics following filters
    public StudentStatResponse getStudentStatistics(String gender, String universitas) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder filterClause = new StringBuilder();

        if (gender != null && !gender.trim().isEmpty()) {
            filterClause.append("AND m.gender = :gender ");
            params.addValue("gender", gender);
        }

        if (universitas != null && !universitas.trim().isEmpty()) {
            filterClause.append("AND LOWER(univ.name_university) = LOWER(:universitas) ");
            params.addValue("universitas", universitas);
        }

        // Determine if we need a JOIN with university for filter
        String univJoin = (universitas != null && !universitas.trim().isEmpty())
            ? "LEFT JOIN university univ ON m.id_university = univ.id "
            : "";

        // 1. Count active
        String sqlActive =
            "SELECT COUNT(DISTINCT m.id) " +
            "FROM mahasiswa m " +
            univJoin +
            "JOIN ( " +
            "    SELECT DISTINCT ON (mahasiswa_id) mahasiswa_id, status " +
            "    FROM periode_magang ORDER BY mahasiswa_id, created_at DESC " +
            ") pm ON m.id = pm.mahasiswa_id " +
            "WHERE pm.status = 'aktif' " + filterClause;
        Long totalActive = jdbc.queryForObject(sqlActive, params, Long.class);

        // 2. Count completed
        String sqlCompleted =
            "SELECT COUNT(DISTINCT m.id) " +
            "FROM mahasiswa m " +
            univJoin +
            "JOIN ( " +
            "    SELECT DISTINCT ON (mahasiswa_id) mahasiswa_id, status " +
            "    FROM periode_magang ORDER BY mahasiswa_id, created_at DESC " +
            ") pm ON m.id = pm.mahasiswa_id " +
            "WHERE pm.status = 'selesai' " + filterClause;
        Long totalCompleted = jdbc.queryForObject(sqlCompleted, params, Long.class);

        // 3. Count active and unassessed
        String sqlUnassessed =
            "SELECT COUNT(DISTINCT m.id) " +
            "FROM mahasiswa m " +
            univJoin +
            "JOIN periode_magang pm ON m.id = pm.mahasiswa_id " +
            "LEFT JOIN penilaian p ON pm.id = p.periode_magang_id " +
            "WHERE pm.status = 'aktif' AND p.id IS NULL " + filterClause;
        Long totalUnassessed = jdbc.queryForObject(sqlUnassessed, params, Long.class);

        return new StudentStatResponse(
            totalActive != null ? totalActive : 0L,
            totalCompleted != null ? totalCompleted : 0L,
            totalUnassessed != null ? totalUnassessed : 0L
        );
    }

    // Get sisa waktu magang in days by user ID
    public Long getSisaWaktuMagangByUserId(UUID userId) {
        String sql = "SELECT pm.tanggal_berakhir - CURRENT_DATE " +
                     "FROM mahasiswa m " +
                     "JOIN periode_magang pm ON m.id = pm.mahasiswa_id " +
                     "WHERE m.user_id = :userId " +
                     "ORDER BY pm.created_at DESC LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        try {
            Integer sisaHari = jdbc.queryForObject(sql, params, Integer.class);
            if (sisaHari == null || sisaHari < 0) {
                return 0L;
            }
            return sisaHari.longValue();
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return 0L;
        }
    }

    // Row mapper helper
    private StudentResponse mapStudentResponse(ResultSet rs, int rowNum) throws SQLException {
        String pId = rs.getString("periode_id");
        String mId = rs.getString("mentor_id");

        UUID periodeId = pId != null ? UUID.fromString(pId) : null;
        LocalDate tanggalMulai = rs.getDate("tanggal_mulai") != null ? rs.getDate("tanggal_mulai").toLocalDate() : null;
        LocalDate tanggalBerakhir = rs.getDate("tanggal_berakhir") != null ? rs.getDate("tanggal_berakhir").toLocalDate() : null;
        UUID mentorId = mId != null ? UUID.fromString(mId) : null;

        return new StudentResponse(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("user_id")),
            rs.getString("email"),
            rs.getString("nim"),
            rs.getString("nama"),
            rs.getString("no_hp"),
            rs.getString("gender") != null ? Gender.fromString(rs.getString("gender")) : null,
            rs.getObject("id_university") != null ? rs.getLong("id_university") : null,
            rs.getString("universitas"),
            periodeId,
            tanggalMulai,
            tanggalBerakhir,
            rs.getString("status_periode") != null ? StatusPeriode.fromString(rs.getString("status_periode")) : null,
            mentorId,
            rs.getString("nama_mentor")
        );
    }
}
