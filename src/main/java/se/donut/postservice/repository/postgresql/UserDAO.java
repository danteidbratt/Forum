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

    @SqlUpdate("INSERT INTO users " +
            "(uuid, name, role, created_at, carma, is_deleted) " +
            "VALUES " +
            "(:uuid, :name, :role, :createdAt, :carma, false)")
    void createUser(@BindBean User user);

    @Transaction
    default void createUserWithPassword(User user, String password) {
        createUser(user);
        getVault().create(user.getUuid(), password);
    }

    default Optional<User> authenticate(String username, String password) {
        Optional<User> user = get(username);
        if (user.isPresent()) {
            int match = getVault().match(user.get().getUuid(), password);
            if (match == 1) {
                return user;
            }
        }
        return Optional.empty();
    }



}
