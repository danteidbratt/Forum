package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.CommentDTO;

import java.util.Date;
import java.util.UUID;

@Getter
public class Comment extends Submission {

    private final UUID parentUuid;
    private final UUID postUuid;

    public Comment(
            UUID uuid,
            UUID authorUuid,
            String content,
            int score,
            UUID parentUuid,
            UUID postUuid,
            Date createdAt
    ) {
        super(uuid, authorUuid, content, score, createdAt);
        this.parentUuid = parentUuid;
        this.postUuid = postUuid;
    }

    public CommentDTO toApiModel(String authorName, Direction myVote) {
        return new CommentDTO(
                this.getUuid(),
                this.getAuthorUuid(),
                authorName,
                this.getContent(),
                this.getScore(),
                this.getCreatedAt(),
                myVote
        );
    }

}
