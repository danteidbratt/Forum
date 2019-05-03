package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.api.PostDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Post extends Submission {

    private final UUID forumUuid;
    private final String title;
    private final String link;

    public Post(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            UUID forumUuid,
            String title,
            String link,
            Instant createdAt
    ) {
        super(uuid, authorUuid, authorName, content, score, createdAt);
        this.forumUuid = forumUuid;
        this.title = title;
        this.link = link;
    }

    public PostDTO toApiModel(List<CommentDTO> commentTree) {
        return new PostDTO(
                this.getUuid(),
                this.getAuthorUuid(),
                this.getAuthorName(),
                this.getTitle(),
                this.getLink(),
                this.getContent(),
                this.getScore(),
                this.getCreatedAt(),
                commentTree
        );
    }
}
