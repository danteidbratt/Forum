package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.CommentDTO;

import java.util.Date;
import java.util.UUID;

@Getter
public class CommentView extends Comment {

    private final String authorName;
    private final Direction myVote;

    public CommentView(
            UUID uuid,
            UUID authorUuid,
            UUID parentUuid,
            UUID postUuid,
            String content,
            int score,
            Date createdAt,
            String authorName,
            Direction myVote
    ) {
        super(uuid, authorUuid, content, score, parentUuid, postUuid, createdAt);
        this.authorName = authorName;
        this.myVote = myVote;
    }

    public CommentDTO toApiModel() {
        return new CommentDTO(
                this.getUuid(),
                this.getCreatedAt(),
                this.getAuthorUuid(),
                this.getAuthorName(),
                this.getContent(),
                this.getScore(),
                this.myVote
        );
    }

}
