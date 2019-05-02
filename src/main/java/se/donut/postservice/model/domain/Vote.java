package se.donut.postservice.model.domain;

import lombok.Data;
import se.donut.postservice.model.Direction;

import java.time.Instant;
import java.util.UUID;

@Data
public class Vote {

    private final UUID uuid;
    private final UUID targetUuid;
    private final UUID userUuid;
    private final Direction direction;
    private final Instant createdAt;
    private final Boolean isDeleted;

}
