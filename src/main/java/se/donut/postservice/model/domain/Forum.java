package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.ForumDTO;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Forum extends Submission {

    private final String name;

    public Forum(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String name,
            String content,
            int score,
            Instant createdAt
    ) {
        super(uuid, authorUuid, authorName, content, score, createdAt);
        this.name = name;
    }

    public ForumDTO toApiModel() {
        return new ForumDTO(
                this.getUuid(),
                this.getAuthorUuid(),
                this.getAuthorName(),
                this.getName(),
                this.getName(),
                this.getScore(),
                this.getCreatedAt()
        );
    }
}
