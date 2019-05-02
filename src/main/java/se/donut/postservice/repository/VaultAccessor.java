package se.donut.postservice.repository;

import java.util.Optional;
import java.util.UUID;

public interface VaultAccessor {

    void createEntry(String username, String password, UUID userUuid);

    Optional<UUID> authenticate(String username, String password);

}
