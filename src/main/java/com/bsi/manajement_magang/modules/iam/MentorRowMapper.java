package com.bsi.manajement_magang.modules.iam;

import com.bsi.manajement_magang.modules.iam.schemas.entity.MentorEntity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MentorRowMapper implements RowMapper<MentorEntity> {
    public MentorEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID userId = UUID.fromString(rs.getString("user_id"));
        String nama = rs.getString("nama");
        String noHp = rs.getString("no_hp");

        return new MentorEntity(id, userId, nama, noHp);
    }
}
