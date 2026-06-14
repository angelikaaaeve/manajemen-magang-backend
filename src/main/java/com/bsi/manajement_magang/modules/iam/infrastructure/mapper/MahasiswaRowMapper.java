package com.bsi.manajement_magang.modules.iam.infrastructure.mapper;

import com.bsi.manajement_magang.modules.iam.domain.Mahasiswa;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MahasiswaRowMapper implements RowMapper<Mahasiswa> {
    @Override
    public Mahasiswa mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID userId = UUID.fromString(rs.getString("user_id"));
        String nim = rs.getString("nim");
        String nama = rs.getString("nama");
        String noHp = rs.getString("no_hp");
        String gender = rs.getString("gender");
        long idUniversityRaw = rs.getLong("id_university");
        Long idUniversity = rs.wasNull() ? null : idUniversityRaw;

        // "universitas" is the aliased column from LEFT JOIN university in find queries
        String universitas = null;
        try {
            universitas = rs.getString("universitas");
        } catch (SQLException ignored) {
            // Column may not be present if query doesn't JOIN university
        }

        Mahasiswa mahasiswa = new Mahasiswa(id, userId, nim, nama, noHp, gender, universitas);
        mahasiswa.setIdUniversity(idUniversity);
        return mahasiswa;
    }
}
