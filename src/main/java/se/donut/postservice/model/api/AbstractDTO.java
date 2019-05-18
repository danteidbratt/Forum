package se.donut.postservice.model.api;

import lombok.Getter;
import se.donut.postservice.util.TimeAgoCalculator;

import java.util.Date;
import java.util.UUID;

@Getter
abstract class AbstractDTO {

    private final UUID uuid;
    private final String timeAgo;

    AbstractDTO(UUID uuid, Date createdAt, Date now) {
        this.uuid = uuid;
        this.timeAgo = TimeAgoCalculator.calculateTimeAgo(createdAt, now);
    }
}