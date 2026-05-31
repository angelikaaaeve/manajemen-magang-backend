package com.bsi.manajement_magang.modules.iam.infrastructure.mapper;

import com.bsi.manajement_magang.modules.iam.domain.Mentor;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MentorRowMapper implements RowMapper<Mentor> {
    @Override
    public Mentor mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID userId = UUID.fromString(rs.getString("user_id"));
        String nama = rs.getString("nama");
        
        return new Mentor(id, userId, nama);
    }
}
