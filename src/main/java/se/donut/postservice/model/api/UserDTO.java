package se.donut.postservice.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.donut.postservice.model.domain.Role;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID uuid;
    private String name;
    private Integer carma;
    private Role role;
}
