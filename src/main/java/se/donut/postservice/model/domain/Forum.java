package se.donut.postservice.model.domain;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Forum {

    private final UUID uuid;
    private final UUID createdBy;
    private final String name;
    private final String description;
    private final Instant createdAt;
    private final Boolean isDeleted;

}
