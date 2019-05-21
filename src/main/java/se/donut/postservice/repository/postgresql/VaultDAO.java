package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import se.donut.postservice.model.domain.VaultEntry;

import java.util.UUID;

public interface VaultDAO {

    @SqlQuery("SELECT * FROM vault " +
            "WHERE user_uuid = :userUuid")
    VaultEntry getByUserUuid(@Bind("userUuid") UUID userUuid);

    @SqlUpdate("INSERT INTO vault (user_uuid, password_hash, salt) " +
            "VALUES (:userUuid, :passwordHash, :salt)")
    void create(
            @BindBean VaultEntry vaultEntry
    );

    @SqlUpdate("DELETE FROM vault WHERE user_uuid = :userUuid")
    void delete(@Bind("userUuid") UUID userUuid);

}
