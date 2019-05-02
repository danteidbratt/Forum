package se.donut.postservice.auth;

import java.security.Principal;
import java.util.UUID;

public class AuthenticatedUser implements Principal {

    private final UUID uuid;
    private final String name;
    private final String role;

    public AuthenticatedUser(UUID uuid, String name, String role) {
        this.uuid = uuid;
        this.name = name;
        this.role = role;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
