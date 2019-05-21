package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.CreateSqlObject;
import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.model.domain.VaultEntry;
import se.donut.postservice.model.mapper.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserDAO {

    @CreateSqlObject
    VaultDAO getVault();

    @SqlQuery("SELECT * FROM users " +
            "WHERE uuid = :uuid AND is_deleted = false")
    Optional<User> get(@Bind("uuid") UUID uuid);

    @SqlQuery("SELECT * FROM users " +
            "WHERE name = :name AND is_deleted = false")
    Optional<User> get(@Bind("name") String name);

    @SqlQuery("SELECT * FROM users " +
            "WHERE is_deleted = false AND uuid IN (<uuids>)")
    @KeyColumn("uuid")
    @RegisterConstructorMapper(UserMapper.class)
    Map<UUID, User> get(@BindList("uuids") List<UUID> uuids);

    @SqlQuery("SELECT " +
            "(SUM(c.score) + SUM(p.score)) AS total_score " +
            "FROM users u " +
            "INNER JOIN post p ON p.author_uuid = u.uuid " +
            "INNER JOIN comment c ON c.author_uuid = u.uuid " +
            "WHERE u.uuid = :userUuid")
    int getCarma(@Bind("userUuid") UUID userUuid);

    @SqlUpdate("INSERT INTO users " +
            "(uuid, name, role, created_at, is_deleted) " +
            "VALUES " +
            "(:uuid, :name, :role, :createdAt, false)")
    void createUser(@BindBean User user);

    @Transaction
    default void createUserWithPassword(User user, VaultEntry vaultEntry) {
        createUser(user);
        getVault().create(vaultEntry);
    }

}
