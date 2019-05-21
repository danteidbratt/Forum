package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.model.domain.VaultEntry;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.util.DataValidator;
import se.donut.postservice.util.Security;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static se.donut.postservice.exception.ExceptionType.USERNAME_ALREADY_TAKEN;
import static se.donut.postservice.exception.ExceptionType.USER_NOT_FOUND;
import static se.donut.postservice.model.domain.Role.USER;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDTO getUser(UUID userUuid) {
        User user = userDAO.get(userUuid)
                .orElseThrow(() -> new PostServiceException(
                        USER_NOT_FOUND,
                        String.format("Could not find user with uuid %s.", userUuid)));
        int carma = userDAO.getCarma(user.getUuid());
        return user.toApiModel(carma, new Date());
    }

    public UserDTO createUser(String username, String password) {
        Optional<User> userWithSameName = userDAO.get(username);

        DataValidator.validateUsername(username);
        DataValidator.validatePassword(password);
        String salt = Security.generateSalt();
        String passwordHash = Security.encryptPassword(password, salt);

        if (userWithSameName.isPresent()) {
            throw new PostServiceException(
                    USERNAME_ALREADY_TAKEN,
                    String.format("Username \"%s\" is already taken.", username)
            );
        }

        UUID userUuid = UUID.randomUUID();
        User user = new User(
                userUuid,
                username,
                USER,
                new Date()
        );
        VaultEntry vaultEntry = new VaultEntry(
                userUuid,
                passwordHash,
                salt
        );
        userDAO.createUserWithPassword(user, vaultEntry);
        return user.toApiModel(0, new Date());
    }

}