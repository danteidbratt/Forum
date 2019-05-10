package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.domain.Role;
import se.donut.postservice.model.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class UserMapper implements RowMapper<User> {
    @Override
    public User map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new User(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                Role.valueOf(rs.getString("role")),
                Date.from(rs.getTimestamp("created_at").toInstant())
        );
    }
}
