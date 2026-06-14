package com.bsi.manajement_magang.modules.iam.repository.mapper;

import com.bsi.manajement_magang.modules.iam.schema.entity.MentorEntity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MentorRowMapper implements RowMapper<MentorEntity> {
    @Override
    public MentorEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID userId = UUID.fromString(rs.getString("user_id"));
        String nama = rs.getString("nama");

        return new MentorEntity(id, userId, nama);
    }
}
