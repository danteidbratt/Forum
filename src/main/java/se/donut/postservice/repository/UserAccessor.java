package se.donut.postservice.repository;

import se.donut.postservice.model.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserAccessor {

    Optional<User> getUser(UUID uuid);

    Optional<User> getUser(String name);

    void createUser(User user, String password);

    Optional<User> authenticate(String username, String password);

}
