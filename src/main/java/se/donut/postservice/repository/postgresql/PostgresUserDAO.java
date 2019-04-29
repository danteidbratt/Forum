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
                handle.createQuery("SELECT * FROM users WHERE uuid = :uuid").bind("uuid", uuid)
                        .mapTo(User.class).findFirst()
        );
    }

    public Optional<User> getUser(String name) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE name = :name").bind("name", name)
                        .mapTo(User.class).findFirst()
        );
    }

    public void createUser(User user) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO users " +
                        "(uuid, name, role, created_at, carma, is_deleted) " +
                        "VALUES " +
                        "(:uuid, :name, :role, :createdAt, :carma, :isDeleted)")
                        .bindBean(user)
                        .execute()
        );
    }

    // "UPDATE poster SET carma = carma + 1 WHERE uuid = :uuid"
}
