package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.repository.VaultAccessor;

import java.util.Optional;
import java.util.UUID;

public class PostgresVaultDAO extends PostgresAbstractDAO implements VaultAccessor {

    public PostgresVaultDAO(Jdbi jdbi) {
        super(jdbi);
    }


    // TODO: put in UserDAO and make transaction
    @Override
    public void createEntry(String username, String password, UUID userUuid) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO vault (username, password, user_uuid) values (:username, :password, :userUuid)")
                        .bind("username", username)
                        .bind("password", password)
                        .bind("userUuid", userUuid).execute()
        );
    }

    @Override
    public Optional<UUID> authenticate(String username, String password) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT user_uuid FROM vault WHERE username = :username AND password = :password")
                        .bind("username", username)
                        .bind("password", password)
                        .mapTo(UUID.class)
                        .findFirst()
        );
    }
}
