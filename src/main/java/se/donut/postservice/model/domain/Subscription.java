package se.donut.postservice.model.domain;


import lombok.Data;

import java.util.UUID;

@Data
public final class Subscription {

    private final UUID userUuid;
    private final UUID forumUuid;

    public Subscription(UUID userUuid, UUID forumUuid) {
        this.userUuid = userUuid;
        this.forumUuid = forumUuid;
    }
}
