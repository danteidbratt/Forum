package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.UUID;

public interface VaultDAO {

    @SqlQuery("SELECT count(*) FROM vault " +
            "WHERE user_uuid = :userUuid AND password = :password")
    int match(@Bind("userUuid") UUID userUuid,
              @Bind("password") String password
    );

    @SqlUpdate("INSERT INTO vault (user_uuid, password) " +
            "VALUES (:userUuid, :password)")
    void create(
            @Bind("userUuid") UUID userUuid,
            @Bind("password") String password
    );

    @SqlUpdate("DELETE FROM vault WHERE user_uuid = :userUuid")
    void delete(@Bind("userUuid") UUID userUuid);

}
