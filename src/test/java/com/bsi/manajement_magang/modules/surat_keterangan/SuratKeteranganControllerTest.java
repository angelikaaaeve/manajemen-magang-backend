package com.bsi.manajement_magang.modules.surat_keterangan;

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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class SuratKeteranganControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private UUID userId;
    private UUID mahasiswaId;
    private UUID periodeId;
    private UUID suratId;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userId = UUID.randomUUID();
        mahasiswaId = UUID.randomUUID();
        periodeId = UUID.randomUUID();
        suratId = UUID.randomUUID();

        // 1. Seed User
        String insertUser = "INSERT INTO \"user\" (id, email, password, role, is_active) " +
                "VALUES (:id, :email, :password, :role, :isActive)";
        jdbc.update(insertUser, new MapSqlParameterSource()
                .addValue("id", userId)
                .addValue("email", "mahasiswa.test.sk_" + userId + "@example.com")
                .addValue("password", "hashed_password")
                .addValue("role", "mahasiswa")
                .addValue("isActive", true));

        // 2. Seed Mahasiswa
        String insertMahasiswa = "INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp) " +
                "VALUES (:id, :userId, :nim, :nama, :noHp)";
        jdbc.update(insertMahasiswa, new MapSqlParameterSource()
                .addValue("id", mahasiswaId)
                .addValue("userId", userId)
                .addValue("nim", "NIM-SK-" + mahasiswaId.toString().substring(0, 6))
                .addValue("nama", "Putri Lestari")
                .addValue("noHp", "081234567891"));

        // 3. Seed Periode Magang
        String insertPeriode = "INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status) " +
                "VALUES (:id, :mahasiswaId, CURRENT_DATE, CURRENT_DATE + 30, 'aktif')";
        jdbc.update(insertPeriode, new MapSqlParameterSource()
                .addValue("id", periodeId)
                .addValue("mahasiswaId", mahasiswaId));
    }

    @Test
    public void testListSuratKeterangan_EmptyInitially() throws Exception {
        mockMvc.perform(get("/api/surat-keterangan")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.periodeMagangId == '" + periodeId + "')].statusSurat", contains("belum diunggah")));
    }

    @Test
    public void testUploadAndRetrieve_Success() throws Exception {
        // 1. Upload new letter
        String payload = "{" +
                "\"periodeMagangId\":\"" + periodeId + "\"," +
                "\"url\":\"https://storage.internflow.com/letters/putri-completion.pdf\"" +
                "}";

        mockMvc.perform(post("/api/surat-keterangan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.statusSurat", is("Sudah Diunggah")))
                .andExpect(jsonPath("$.url", is("https://storage.internflow.com/letters/putri-completion.pdf")));

        // 2. Query letters list to verify status changed
        mockMvc.perform(get("/api/surat-keterangan")
                .param("status", "Sudah Diunggah")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.periodeMagangId == '" + periodeId + "')].statusSurat", contains("Sudah Diunggah")));
    }

    @Test
    public void testUpload_UpdateExisting() throws Exception {
        // 1. Upload first time
        String payload1 = "{" +
                "\"periodeMagangId\":\"" + periodeId + "\"," +
                "\"url\":\"https://storage.internflow.com/letters/first-upload.pdf\"" +
                "}";
        mockMvc.perform(post("/api/surat-keterangan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 2. Upload second time to update url
        String payload2 = "{" +
                "\"periodeMagangId\":\"" + periodeId + "\"," +
                "\"url\":\"https://storage.internflow.com/letters/second-upload.pdf\"" +
                "}";
        mockMvc.perform(post("/api/surat-keterangan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", is("https://storage.internflow.com/letters/second-upload.pdf")));
    }

    @Test
    public void testUpload_PeriodNotFound() throws Exception {
        String payload = "{" +
                "\"periodeMagangId\":\"" + UUID.randomUUID() + "\"," +
                "\"url\":\"https://storage.internflow.com/letters/test.pdf\"" +
                "}";

        mockMvc.perform(post("/api/surat-keterangan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("was not found")));
    }

    @Test
    public void testGetStatistics() throws Exception {
        // 1. Statistics before upload
        mockMvc.perform(get("/api/surat-keterangan/statistik")
                .param("namaMahasiswa", "Putri")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSuratDiunggah", is(0)))
                .andExpect(jsonPath("$.totalJumlahSurat", is(1)));

        // 2. Upload letter
        String payload = "{" +
                "\"periodeMagangId\":\"" + periodeId + "\"," +
                "\"url\":\"https://storage.internflow.com/letters/putri.pdf\"" +
                "}";
        mockMvc.perform(post("/api/surat-keterangan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());

        // 3. Statistics after upload
        mockMvc.perform(get("/api/surat-keterangan/statistik")
                .param("namaMahasiswa", "Putri")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSuratDiunggah", is(1)))
                .andExpect(jsonPath("$.totalSuratBelumDiunggah", is(0)))
                .andExpect(jsonPath("$.totalJumlahSurat", is(1)));
    }
}
