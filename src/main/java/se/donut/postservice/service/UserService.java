package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.UserAccessor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static se.donut.postservice.exception.ExceptionType.*;
import static se.donut.postservice.model.domain.Role.USER;

public class UserService {

    private final UserAccessor userAccessor;

    public UserService(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public UserDTO getUser(UUID userUuid) {
        User user = userAccessor.getUser(userUuid)
                .orElseThrow(() -> new PostServiceException(USER_NOT_FOUND));
        return user.toApiModel();
    }

    public UUID createUser(String username, String password) {
        validateNewUser(username, password);

        UUID userUuid = UUID.randomUUID();
        User user = new User(
                userUuid,
                username,
                0,
                USER,
                Instant.now()
        );
        userAccessor.createUser(user, password);
        return userUuid;
    }

    public UserDTO login(String username, String password) {
        Optional<User> user = userAccessor.authenticate(username, password);
        if (user.isPresent()) {
            return user.get().toApiModel();
        }
        throw new PostServiceException(LOGIN_FAILED);
    }

    private void validateNewUser(String username, String password) {
        userAccessor.getUser(username).ifPresent(d -> {
            throw new PostServiceException(USERNAME_ALREADY_TAKEN);
        });

        if (username.length() < 3) {
            // TODO: fix type
            throw new PostServiceException(USERNAME_ALREADY_TAKEN);
        }
        if (password.length() < 3) {
            // TODO: fix type
            throw new PostServiceException(USERNAME_ALREADY_TAKEN);
        }

    }

}