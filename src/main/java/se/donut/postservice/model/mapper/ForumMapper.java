package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.domain.Forum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class ForumMapper implements RowMapper<Forum> {

    @Override
    public Forum map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Forum(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("author_uuid")),
                rs.getString("author_name"),
                rs.getString("name"),
                rs.getString("content"),
                rs.getInt("score"),
                Date.from(rs.getTimestamp("created_at").toInstant())
        );
    }
}
