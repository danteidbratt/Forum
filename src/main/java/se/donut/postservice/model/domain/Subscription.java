package se.donut.postservice.model.domain;


import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Subscription extends AbstractEntity {

    private final UUID userUuid;
    private final UUID forumUuid;

    public Subscription(UUID uuid, UUID userUuid, UUID forumUuid, Instant createdAt) {
        super(uuid, createdAt);
        this.userUuid = userUuid;
        this.forumUuid = forumUuid;
    }
}
