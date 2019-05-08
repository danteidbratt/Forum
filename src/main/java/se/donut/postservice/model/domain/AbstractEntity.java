package se.donut.postservice.model.domain;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class AbstractEntity {

    private final UUID uuid;
    private final Date createdAt;

    AbstractEntity(UUID uuid, Date createAt) {
        this.uuid = uuid;
        this.createdAt = createAt;
    }

}
