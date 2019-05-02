package se.donut.postservice.model.domain;

import lombok.Data;
import se.donut.postservice.model.api.UserDTO;

import java.time.Instant;
import java.util.UUID;

@Data
public class User {
    private final UUID uuid;
    private final String name;
    private final Integer carma;
    private final Role role;
    private final Instant createdAt;
    private final Boolean isDeleted;

    public UserDTO toApiModel() {
        return new UserDTO(this.uuid, this.name, this.carma, this.role);
    }
}
