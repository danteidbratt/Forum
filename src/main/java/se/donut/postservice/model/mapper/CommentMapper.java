package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.domain.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CommentMapper implements RowMapper<Comment> {
    @Override
    public Comment map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Comment(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("author_uuid")),
                rs.getString("author_name"),
                rs.getString("content"),
                rs.getInt("score"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getBoolean("is_deleted"),
                UUID.fromString(rs.getString("parent_uuid")),
                UUID.fromString(rs.getString("post_uuid"))
        );
    }
}
