package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.UserAccessor;

import java.util.Optional;
import java.util.UUID;

public class PostgresUserDAO extends PostgresAbstractDAO implements UserAccessor {


    public PostgresUserDAO(Jdbi jdbi) {
        super(jdbi);
    }

    public Optional<User> getUser(UUID uuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE uuid = :uuid")
                        .bind("uuid", uuid)
                        .mapTo(User.class).findFirst()
        );
    }

    public Optional<User> getUser(String name) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE name = :name")
                        .bind("name", name)
                        .mapTo(User.class).findFirst()
        );
    }

    public void createUser(User user, String password) {
        jdbi.useTransaction(handle -> {
            handle.createUpdate("INSERT INTO users " +
                    "(uuid, name, role, created_at, carma, is_deleted) " +
                    "VALUES " +
                    "(:uuid, :name, :role, :createdAt, :carma, false)")
                    .bindBean(user)
                    .execute();
            handle.createUpdate("INSERT INTO vault (user_uuid, password) values (:userUuid, :password)")
                    .bind("password", password)
                    .bind("userUuid", user.getUuid())
                    .execute();
        });
    }

    public Optional<User> authenticate(String username, String password) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT u.* FROM users u " +
                        "INNER JOIN vault v ON u.uuid = v.user_uuid " +
                        "WHERE u.name = :username AND v.password = :password")
                        .bind("username", username)
                        .bind("password", password)
                        .mapTo(User.class)
                        .findFirst()
        );
    }

    // "UPDATE poster SET carma = carma + 1 WHERE uuid = :uuid"
}
