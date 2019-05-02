package se.donut.postservice.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.donut.postservice.model.api.UserDTO;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class User {
    private UUID uuid;
    private String name;
    private Integer carma;
    private Role role;
    private Instant createdAt;
    private Boolean isDeleted;

    public UserDTO toApiModel() {
        return new UserDTO(this.uuid, this.name, this.carma, this.role);
    }
}
