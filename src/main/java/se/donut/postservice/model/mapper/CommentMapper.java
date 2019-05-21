package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.domain.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public final class CommentMapper implements RowMapper<Comment> {

    @Override
    public Comment map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Comment(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("author_uuid")),
                rs.getString("content"),
                rs.getInt("score"),
                UUID.fromString(rs.getString("parent_uuid")),
                UUID.fromString(rs.getString("post_uuid")),
                Date.from(rs.getTimestamp("created_at").toInstant())
        );
    }
}
