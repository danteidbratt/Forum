package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.domain.CommentView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;


public class CommentViewMapper implements RowMapper<CommentView> {

    @Override
    public CommentView map(ResultSet rs, StatementContext ctx) throws SQLException {
        String myVote = rs.getString("my_vote");
        return new CommentView(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("author_uuid")),
                UUID.fromString(rs.getString("parent_uuid")),
                UUID.fromString(rs.getString("post_uuid")),
                rs.getString("author_name"),
                rs.getInt("score"),
                Date.from(rs.getTimestamp("created_at").toInstant()),
                rs.getString("content"),
                myVote != null ? Direction.valueOf(myVote) : null
        );
    }
}
