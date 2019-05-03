package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.UserDTO;

import java.util.Date;
import java.util.UUID;

@Getter
public class User extends AbstractEntity {

    private final String name;
    private final Integer carma;
    private final Role role;

    public User(UUID uuid, String name, Integer carma, Role role, Date createdAt) {
        super(uuid, createdAt);
        this.name = name;
        this.carma = carma;
        this.role = role;
    }

    public UserDTO toApiModel() {
        return new UserDTO(
                this.getUuid(),
                this.getCreatedAt(),
                this.getName(),
                this.getCarma(),
                this.getRole()
        );
    }
}
