package se.donut.postservice.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ForumDTO {

    private final UUID uuid;
    private final UUID authorUuid;
    private final String authorName;
    private final String name;
    private final String description;
    private final int subscribers;
    private final Instant createdAt;

}
