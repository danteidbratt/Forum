package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.domain.Vote;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VoteMapper implements RowMapper<Vote> {

    @Override
    public Vote map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Vote(
                UUID.fromString(rs.getString("target_uuid")),
                UUID.fromString(rs.getString("user_uuid")),
                Direction.valueOf(rs.getString("direction"))
        );
    }
}
