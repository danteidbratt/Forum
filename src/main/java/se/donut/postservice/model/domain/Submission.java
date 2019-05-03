package se.donut.postservice.model.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class Submission extends AbstractEntity {

    private final UUID authorUuid;
    private final String authorName;
    private final String content;
    private final int score;

    Submission(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            Instant createdAt
    ) {
        super(uuid, createdAt);
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.content = content;
        this.score = score;
    }
}
