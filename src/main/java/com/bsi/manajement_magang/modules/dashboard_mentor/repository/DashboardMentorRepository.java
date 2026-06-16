package com.bsi.manajement_magang.modules.dashboard_mentor.repository;

import com.bsi.manajement_magang.enums.Gender;
import com.bsi.manajement_magang.enums.StatusPeriode;

import com.bsi.manajement_magang.modules.dashboard_mentor.schema.response.SearchStudentResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class DashboardMentorRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public DashboardMentorRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 1. Check if email already exists
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(1) FROM \"user\" WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // 2. Check if NIM already exists
    public boolean existsByNim(String nim) {
        String sql = "SELECT COUNT(1) FROM mahasiswa WHERE nim = :nim";
        MapSqlParameterSource params = new MapSqlParameterSource("nim", nim);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    // 3. Save User
    public void saveUser(UUID id, String email, String password) {
        String sql = "INSERT INTO \"user\" (id, email, password, role, is_active, created_at, updated_at) " +
                     "VALUES (:id, :email, :password, 'mahasiswa', true, NOW(), NOW())";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("email", email)
                .addValue("password", password);
        jdbc.update(sql, params);
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
                throw new IllegalStateException("Failed to create university: " + name);
            }
            return id;
        });
    }

    // 4. Save Mahasiswa (universitas is a name string; resolved to id_university FK)
    public void saveMahasiswa(UUID id, UUID userId, String nim, String nama, String noHp, String gender, String universitas) {
        Long idUniversity = (universitas != null && !universitas.trim().isEmpty())
            ? findOrCreateUniversityByName(universitas)
            : null;

        String sql = "INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp, gender, id_university) " +
                     "VALUES (:id, :userId, :nim, :nama, :noHp, :gender, :idUniversity)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId)
                .addValue("nim", nim)
                .addValue("nama", nama)
                .addValue("noHp", noHp != null ? noHp : "-")
                .addValue("gender", gender)
                .addValue("idUniversity", idUniversity);
        jdbc.update(sql, params);
    }

    // 5. Save Periode Magang
    public void savePeriod(UUID id, UUID mahasiswaId, LocalDate tanggalMulai, LocalDate tanggalBerakhir) {
        String sql = "INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status, created_at) " +
                     "VALUES (:id, :mahasiswaId, :tanggalMulai, :tanggalBerakhir, 'aktif', NOW())";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("mahasiswaId", mahasiswaId)
                .addValue("tanggalMulai", tanggalMulai)
                .addValue("tanggalBerakhir", tanggalBerakhir);
        jdbc.update(sql, params);
    }

    // 6. Query / Search students by name
    public List<Map<String, Object>> searchStudentsByName(String name) {
        StringBuilder sql = new StringBuilder(
            "SELECT m.id, m.user_id, u.email, m.nim, m.nama, m.no_hp, m.gender, " +
            "       univ.name_university AS universitas, " +
            "       pm.id as periode_id, pm.tanggal_mulai, pm.tanggal_berakhir, pm.status as status_periode " +
            "FROM mahasiswa m " +
            "JOIN \"user\" u ON m.user_id = u.id " +
            "LEFT JOIN university univ ON m.id_university = univ.id " +
            "LEFT JOIN ( " +
            "    SELECT DISTINCT ON (mahasiswa_id) id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status " +
            "    FROM periode_magang " +
            "    ORDER BY mahasiswa_id, created_at DESC " +
            ") pm ON m.id = pm.mahasiswa_id " +
            "WHERE 1=1 "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (name != null && !name.trim().isEmpty()) {
            sql.append("AND LOWER(m.nama) LIKE LOWER(:name) ");
            params.addValue("name", "%" + name.trim() + "%");
        }

        sql.append("ORDER BY m.nama ASC");

        return jdbc.queryForList(sql.toString(), params);
    }

    // 7. Count active students (global or by mentor)
    public long countActiveStudents(UUID mentorId) {
        String sql;
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (mentorId != null) {
            sql = "SELECT COUNT(DISTINCT mm.mahasiswa_id) " +
                  "FROM mentor_mahasiswa mm " +
                  "JOIN periode_magang pm ON mm.mahasiswa_id = pm.mahasiswa_id " +
                  "WHERE mm.mentor_id = :mentorId AND pm.status = 'aktif'";
            params.addValue("mentorId", mentorId);
        } else {
            sql = "SELECT COUNT(DISTINCT mahasiswa_id) FROM periode_magang WHERE status = 'aktif'";
        }
        Long count = jdbc.queryForObject(sql, params, Long.class);
        return count != null ? count : 0L;
    }

    // 8. Count completed students (global or by mentor)
    public long countCompletedStudents(UUID mentorId) {
        String sql;
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (mentorId != null) {
            sql = "SELECT COUNT(DISTINCT mm.mahasiswa_id) " +
                  "FROM mentor_mahasiswa mm " +
                  "JOIN periode_magang pm ON mm.mahasiswa_id = pm.mahasiswa_id " +
                  "WHERE mm.mentor_id = :mentorId AND pm.status = 'selesai'";
            params.addValue("mentorId", mentorId);
        } else {
            sql = "SELECT COUNT(DISTINCT mahasiswa_id) FROM periode_magang WHERE status = 'selesai'";
        }
        Long count = jdbc.queryForObject(sql, params, Long.class);
        return count != null ? count : 0L;
    }

    // 9. Accumulation of attendance (global or by mentor)
    public Map<String, Long> getAttendanceAccumulation(UUID mentorId) {
        String sql;
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (mentorId != null) {
            sql = "SELECT a.status, COUNT(*) as status_count " +
                  "FROM absensi a " +
                  "JOIN periode_magang pm ON a.periode_magang_id = pm.id " +
                  "JOIN mentor_mahasiswa mm ON pm.mahasiswa_id = mm.mahasiswa_id " +
                  "WHERE mm.mentor_id = :mentorId AND a.status IN ('hadir', 'izin', 'sakit') " +
                  "GROUP BY a.status";
            params.addValue("mentorId", mentorId);
        } else {
            sql = "SELECT status, COUNT(*) as status_count " +
                  "FROM absensi " +
                  "WHERE status IN ('hadir', 'izin', 'sakit') " +
                  "GROUP BY status";
        }

        List<Map<String, Object>> rows = jdbc.queryForList(sql, params);
        Map<String, Long> result = new HashMap<>();
        // Initialize with 0s to guarantee fields are returned
        result.put("hadir", 0L);
        result.put("izin", 0L);
        result.put("sakit", 0L);

        for (Map<String, Object> row : rows) {
            String status = (String) row.get("status");
            Long count = ((Number) row.get("status_count")).longValue();
            if (status != null) {
                result.put(status.toLowerCase(), count);
            }
        }
        return result;
    }

    // 10. Find single student details by ID
    public Optional<SearchStudentResponse> findStudentById(UUID id) {
        String sql =
            "SELECT m.id, m.user_id, u.email, m.nim, m.nama, m.no_hp, m.gender, " +
            "       univ.name_university AS universitas, " +
            "       pm.id as periode_id, pm.tanggal_mulai, pm.tanggal_berakhir, pm.status as status_periode " +
            "FROM mahasiswa m " +
            "JOIN \"user\" u ON m.user_id = u.id " +
            "LEFT JOIN university univ ON m.id_university = univ.id " +
            "LEFT JOIN ( " +
            "    SELECT DISTINCT ON (mahasiswa_id) id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status " +
            "    FROM periode_magang " +
            "    ORDER BY mahasiswa_id, created_at DESC " +
            ") pm ON m.id = pm.mahasiswa_id " +
            "WHERE m.id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.query(sql, params, (rs, rowNum) -> new SearchStudentResponse(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("user_id")),
            rs.getString("email"),
            rs.getString("nim"),
            rs.getString("nama"),
            rs.getString("no_hp"),
            rs.getString("gender") != null ? Gender.fromString(rs.getString("gender")) : null,
            rs.getString("universitas"),
            rs.getString("periode_id") != null ? UUID.fromString(rs.getString("periode_id")) : null,
            rs.getDate("tanggal_mulai") != null ? rs.getDate("tanggal_mulai").toLocalDate() : null,
            rs.getDate("tanggal_berakhir") != null ? rs.getDate("tanggal_berakhir").toLocalDate() : null,
            rs.getString("status_periode") != null ? StatusPeriode.fromString(rs.getString("status_periode")) : null
        )).stream().findFirst();
    }
}
