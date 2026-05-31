package com.bsi.manajement_magang.modules.iam.infrastructure.mapper;

import com.bsi.manajement_magang.modules.iam.domain.Role;
import com.bsi.manajement_magang.modules.iam.domain.User;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String email = rs.getString("email");
        String password = rs.getString("password");
        Role role = Role.fromString(rs.getString("role"));
        boolean isActive = rs.getBoolean("is_active");
        
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : null;
        
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        LocalDateTime updatedAt = updatedAtTs != null ? updatedAtTs.toLocalDateTime() : null;
        
        return new User(id, email, password, role, isActive, createdAt, updatedAt);
    }
}
