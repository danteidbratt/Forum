package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Direction;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Vote extends AbstractEntity {

    private final UUID targetUuid;
    private final UUID userUuid;
    private final Direction direction;

    public Vote(
            UUID uuid,
            UUID targetUuid,
            UUID userUuid,
            Direction direction,
            Instant createdAt
    ) {
        super(uuid, createdAt);
        this.targetUuid = targetUuid;
        this.userUuid = userUuid;
        this.direction = direction;
    }
}
