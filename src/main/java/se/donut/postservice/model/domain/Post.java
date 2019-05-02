package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.api.PostThreadDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Post extends Entry {

    private final UUID forumUuid;
    private final String title;
    private final String link;

    public Post(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            Instant createdAt,
            Boolean isDeleted,
            UUID forumUuid,
            String title,
            String link
    ) {
        super(uuid, authorUuid, authorName, content, score, createdAt, isDeleted);
        this.forumUuid = forumUuid;
        this.title = title;
        this.link = link;
    }

    public PostThreadDTO toApiModel(List<CommentDTO> commentTree) {
        return new PostThreadDTO(
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
