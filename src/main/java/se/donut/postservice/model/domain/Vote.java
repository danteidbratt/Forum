package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Direction;

import java.util.UUID;

@Getter
public class Vote {

    private final UUID targetUuid;
    private final UUID userUuid;
    private final Direction direction;

    public Vote(
            UUID targetUuid,
            UUID userUuid,
            Direction direction
    ) {
        this.targetUuid = targetUuid;
        this.userUuid = userUuid;
        this.direction = direction;
    }
}
