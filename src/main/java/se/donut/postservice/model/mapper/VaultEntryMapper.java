package se.donut.postservice.model.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import se.donut.postservice.model.domain.VaultEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VaultEntryMapper implements RowMapper<VaultEntry> {
    @Override
    public VaultEntry map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new VaultEntry(
                UUID.fromString(rs.getString("user_uuid")),
                rs.getString("password_hash"),
                rs.getString("salt")
        );
    }
}
