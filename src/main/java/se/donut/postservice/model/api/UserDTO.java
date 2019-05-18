package se.donut.postservice.model.api;

import lombok.Getter;
import se.donut.postservice.model.domain.Role;

import java.util.Date;
import java.util.UUID;

@Getter
public class UserDTO extends AbstractDTO {

    private final String name;
    private final Integer carma;
    private final Role role;

    public UserDTO(UUID uuid, Date createdAt, Date now, String name, Integer carma, Role role) {
        super(uuid, createdAt, now);
        this.name = name;
        this.carma = carma;
        this.role = role;
    }
}
