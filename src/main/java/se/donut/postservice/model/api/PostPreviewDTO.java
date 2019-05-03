package se.donut.postservice.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PostPreviewDTO {

    private final UUID uuid;
    private final UUID authorUuid;
    private final String authorName;
    private final String title;
    private final String link;
    private final String content;
    private final int score;
    private final Instant createdAt;

}
