package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.CommentDTO;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Comment extends Entry {

    private final UUID parentUuid;
    private final UUID postUuid;

    public Comment(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            Instant createdAt,
            Boolean isDeleted,
            UUID parentUuid,
            UUID postUuid
    ) {
        super(uuid, authorUuid, authorName, content, score, createdAt, isDeleted);
        this.parentUuid = parentUuid;
        this.postUuid = postUuid;
    }

    public CommentDTO toApiModel() {
        return new CommentDTO(
                this.getUuid(),
                this.getAuthorUuid(),
                this.getAuthorName(),
                this.getContent(),
                this.getScore(),
                this.getCreatedAt()
        );
    }
}
