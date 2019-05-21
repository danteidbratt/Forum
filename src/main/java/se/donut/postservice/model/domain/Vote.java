package se.donut.postservice.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import se.donut.postservice.model.Direction;

import java.util.UUID;

@Getter
@AllArgsConstructor
public final class Vote {

    private final UUID targetUuid;
    private final UUID userUuid;
    private final Direction direction;

}
