package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.UserDTO;

import java.util.Date;
import java.util.UUID;

@Getter
public final class User extends AbstractEntity {

    private final String name;
    private final Role role;

    public User(UUID uuid, String name, Role role, Date createdAt) {
        super(uuid, createdAt);
        this.name = name;
        this.role = role;
    }

    public UserDTO toApiModel(int carma, Date now) {
        return new UserDTO(
                this.getUuid(),
                this.getCreatedAt(),
                now,
                this.getName(),
                carma,
                this.getRole()
        );
    }
}
