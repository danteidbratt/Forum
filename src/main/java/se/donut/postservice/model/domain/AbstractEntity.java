package se.donut.postservice.model.domain;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AbstractEntity {

    private final UUID uuid;
    private final Date createdAt;

}
