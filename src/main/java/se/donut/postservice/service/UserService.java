package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.postgresql.UserDAO;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static se.donut.postservice.exception.ExceptionType.*;
import static se.donut.postservice.model.domain.Role.USER;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDTO getUser(UUID userUuid) {
        User user = userDAO.get(userUuid)
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
                new Date()
        );
        userDAO.createUserWithPassword(user, password);
        return userUuid;
    }

    public UserDTO login(String username, String password) {
        Optional<User> user = userDAO.authenticate(username, password);
        if (user.isPresent()) {
            return user.get().toApiModel();
        }
        throw new PostServiceException(LOGIN_FAILED);
    }

    private void validateNewUser(String username, String password) {
        userDAO.get(username).ifPresent(d -> {
            throw new PostServiceException(USERNAME_ALREADY_EXISTS);
        });

        if (username.length() < 3) {
            // TODO: fix type
            throw new PostServiceException(USERNAME_ALREADY_EXISTS);
        }
        if (password.length() < 3) {
            // TODO: fix type
            throw new PostServiceException(USERNAME_ALREADY_EXISTS);
        }

    }

}