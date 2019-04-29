package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.UserAccessor;

import java.time.Instant;
import java.util.UUID;

import static se.donut.postservice.exception.ExceptionType.NAME_ALREADY_TAKEN;
import static se.donut.postservice.exception.ExceptionType.USER_NOT_FOUND;
import static se.donut.postservice.model.domain.UserRole.MEMBER;

public class UserService {

    private final UserAccessor userAccessor;

    public UserService(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public UserDTO getPoster(UUID posterUuid) {
        User user = userAccessor.getUser(posterUuid)
                .orElseThrow(() -> new PostServiceException(USER_NOT_FOUND));
        return user.toApiModel();
    }

    public UUID createPoster(String name) {
        userAccessor.getUser(name).ifPresent(d -> {
            throw new PostServiceException(NAME_ALREADY_TAKEN);
        });
        UUID userUuid = UUID.randomUUID();
        User user = new User(
                userUuid,
                name,
                0,
                MEMBER,
                Instant.now(),
                false
        );
        userAccessor.createUser(user);
        return userUuid;
    }

}