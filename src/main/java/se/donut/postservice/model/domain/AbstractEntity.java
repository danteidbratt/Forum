package se.donut.postservice.model.domain;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AbstractEntity {

    private final UUID uuid;
    private final Instant createdAt;
    private final Boolean isDeleted;

}
