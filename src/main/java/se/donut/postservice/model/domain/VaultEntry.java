package se.donut.postservice.model.domain;

import lombok.Data;

import java.util.UUID;

@Data
public final class VaultEntry {

    private final UUID userUuid;
    private final String passwordHash;
    private final String salt;

}
