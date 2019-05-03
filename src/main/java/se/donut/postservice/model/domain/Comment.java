package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.CommentDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Comment extends Submission {

    private final UUID parentUuid;
    private final UUID postUuid;

    public Comment(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            UUID parentUuid,
            UUID postUuid,
            Instant createdAt
    ) {
        super(uuid, authorUuid, authorName, content, score, createdAt);
        this.parentUuid = parentUuid;
        this.postUuid = postUuid;
    }

    public CommentDTO toApiModel(List<CommentDTO> children) {
        return new CommentDTO(
                this.getUuid(),
                this.getAuthorUuid(),
                this.getAuthorName(),
                this.getContent(),
                this.getScore(),
                this.getCreatedAt(),
                children
        );
    }

}
