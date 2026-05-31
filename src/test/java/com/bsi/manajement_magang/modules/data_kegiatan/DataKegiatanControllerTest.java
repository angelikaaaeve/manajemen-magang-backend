package com.bsi.manajement_magang.modules.data_kegiatan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class DataKegiatanControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private UUID userId;
    private UUID mahasiswaId;
    private UUID periodeId;
    private UUID kegiatanId;
    private UUID fileId;

    @BeforeEach
    public void setUp() {
        // Initialize MockMvc manually
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean and Seed test data
        userId = UUID.randomUUID();
        mahasiswaId = UUID.randomUUID();
        periodeId = UUID.randomUUID();
        kegiatanId = UUID.randomUUID();
        fileId = UUID.randomUUID();

        // 1. Insert User
        String insertUser = "INSERT INTO \"user\" (id, email, password, role, is_active) " +
                "VALUES (:id, :email, :password, :role, :isActive)";
        jdbc.update(insertUser, new MapSqlParameterSource()
                .addValue("id", userId)
                .addValue("email", "mahasiswa.test_" + userId + "@example.com")
                .addValue("password", "hashed_password")
                .addValue("role", "mahasiswa")
                .addValue("isActive", true));

        // 2. Insert Mahasiswa
        String insertMahasiswa = "INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp) " +
                "VALUES (:id, :userId, :nim, :nama, :noHp)";
        jdbc.update(insertMahasiswa, new MapSqlParameterSource()
                .addValue("id", mahasiswaId)
                .addValue("userId", userId)
                .addValue("nim", "NIM-" + mahasiswaId.toString().substring(0, 8))
                .addValue("nama", "Budi Santoso")
                .addValue("noHp", "081234567890"));

        // 3. Insert Periode Magang
        String insertPeriode = "INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status) " +
                "VALUES (:id, :mahasiswaId, CURRENT_DATE, CURRENT_DATE + 30, 'aktif')";
        jdbc.update(insertPeriode, new MapSqlParameterSource()
                .addValue("id", periodeId)
                .addValue("mahasiswaId", mahasiswaId));

        // 4. Insert Data Kegiatan
        String insertKegiatan = "INSERT INTO data_kegiatan (id, periode_magang_id, judul, deskripsi, waktu, status) " +
                "VALUES (:id, :periodeId, :judul, :deskripsi, :waktu, :status)";
        jdbc.update(insertKegiatan, new MapSqlParameterSource()
                .addValue("id", kegiatanId)
                .addValue("periodeId", periodeId)
                .addValue("judul", "Implementasi Layout Refactoring")
                .addValue("deskripsi", "Melakukan refactoring terhadap layout utama admin dashboard.")
                .addValue("waktu", Timestamp.from(OffsetDateTime.now().toInstant()))
                .addValue("status", "belum disetujui"));

        // 5. Insert File Kegiatan
        String insertFile = "INSERT INTO file_kegiatan (id, data_kegiatan_id, nama_file, url, tipe_file) " +
                "VALUES (:id, :kegiatanId, :namaFile, :url, :tipeFile)";
        jdbc.update(insertFile, new MapSqlParameterSource()
                .addValue("id", fileId)
                .addValue("kegiatanId", kegiatanId)
                .addValue("namaFile", "budi-weekly-report.pdf")
                .addValue("url", "https://storage.internflow.com/logbook/budi-weekly-report.pdf")
                .addValue("tipeFile", "application/pdf"));
    }

    @Test
    public void testListActivities_NoFilters() throws Exception {
        mockMvc.perform(get("/api/kegiatan")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == '" + kegiatanId + "')].judul", contains("Implementasi Layout Refactoring")))
                .andExpect(jsonPath("$[?(@.id == '" + kegiatanId + "')].namaMahasiswa", contains("Budi Santoso")))
                .andExpect(jsonPath("$[?(@.id == '" + kegiatanId + "')].status", contains("belum disetujui")))
                .andExpect(jsonPath("$[?(@.id == '" + kegiatanId + "')].fileUrl", contains("https://storage.internflow.com/logbook/budi-weekly-report.pdf")));
    }

    @Test
    public void testListActivities_WithFilterStatus() throws Exception {
        mockMvc.perform(get("/api/kegiatan")
                .param("status", "belum disetujui")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + kegiatanId + "')].status", contains("belum disetujui")));
    }

    @Test
    public void testListActivities_WithFilterName() throws Exception {
        mockMvc.perform(get("/api/kegiatan")
                .param("namaMahasiswa", "Budi")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + kegiatanId + "')].namaMahasiswa", contains("Budi Santoso")));
    }

    @Test
    public void testUpdateStatus_Success() throws Exception {
        mockMvc.perform(put("/api/kegiatan/{id}/status", kegiatanId)
                .param("status", "disetujui")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(kegiatanId.toString())))
                .andExpect(jsonPath("$.status", is("disetujui")));

        // Verify in DB
        String checkStatus = "SELECT status FROM data_kegiatan WHERE id = :id";
        String statusInDb = jdbc.queryForObject(checkStatus, new MapSqlParameterSource("id", kegiatanId), String.class);
        assert "disetujui".equals(statusInDb);
    }

    @Test
    public void testUpdateStatus_InvalidStatus() throws Exception {
        mockMvc.perform(put("/api/kegiatan/{id}/status", kegiatanId)
                .param("status", "invalid-status")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateStatus_RecordNotFound() throws Exception {
        mockMvc.perform(put("/api/kegiatan/{id}/status", UUID.randomUUID())
                .param("status", "disetujui")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Service throws IllegalArgumentException
    }

    @Test
    public void testGetActivityFile_Success() throws Exception {
        mockMvc.perform(get("/api/kegiatan/{id}/file", kegiatanId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", is("https://storage.internflow.com/logbook/budi-weekly-report.pdf")));
    }

    @Test
    public void testGetActivityFile_RecordNotFound() throws Exception {
        mockMvc.perform(get("/api/kegiatan/{id}/file", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetActivityStatistics() throws Exception {
        mockMvc.perform(get("/api/kegiatan/statistik")
                .param("namaMahasiswa", "Budi")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKegiatan", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.disetujui", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.ditolak", greaterThanOrEqualTo(0)));
    }

    @Test
    public void testDeleteActivity_Success() throws Exception {
        mockMvc.perform(delete("/api/kegiatan/{id}", kegiatanId))
                .andExpect(status().isNoContent());

        // Verify in DB
        String checkCount = "SELECT COUNT(1) FROM data_kegiatan WHERE id = :id";
        Long count = jdbc.queryForObject(checkCount, new MapSqlParameterSource("id", kegiatanId), Long.class);
        assert count != null && count == 0L;
    }
}
