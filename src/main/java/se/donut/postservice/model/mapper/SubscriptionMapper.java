package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.domain.Subscription;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class SubscriptionMapper implements RowMapper<Subscription> {

    @Override
    public Subscription map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Subscription(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("user_uuid")),
                UUID.fromString(rs.getString("forum_uuid")),
                Date.from(rs.getTimestamp("created_at").toInstant())
        );
    }
}
