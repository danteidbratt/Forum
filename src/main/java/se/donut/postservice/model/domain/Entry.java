package se.donut.postservice.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public abstract class Entry {

    private final UUID uuid;
    private final UUID authorUuid;
    private final String authorName;
    private final String content;
    private final int score;
    private final Instant createdAt;
    private final Boolean isDeleted;

}
