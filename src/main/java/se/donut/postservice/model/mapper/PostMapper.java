package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.domain.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public final class PostMapper implements RowMapper<Post> {
    @Override
    public Post map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Post(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("author_uuid")),
                rs.getString("content"),
                rs.getInt("score"),
                UUID.fromString(rs.getString("forum_uuid")),
                rs.getString("title"),
                Date.from(rs.getTimestamp("created_at").toInstant()),
                rs.getInt("comment_count"));
    }
}
