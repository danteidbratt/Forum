package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.PostDTO;

import java.util.Date;
import java.util.UUID;

@Getter
public class Post extends Submission {

    private final UUID forumUuid;
    private final String title;

    public Post(
            UUID uuid,
            UUID authorUuid,
            String content,
            int score,
            UUID forumUuid,
            String title,
            Date createdAt
    ) {
        super(uuid, authorUuid, content, score, createdAt);
        this.forumUuid = forumUuid;
        this.title = title;
    }

    public PostDTO toApiModel(String authorName, Direction direction) {
        return new PostDTO(
                this.getUuid(),
                this.getCreatedAt(),
                this.getAuthorUuid(),
                authorName,
                this.getTitle(),
                this.getContent(),
                this.getScore(),
                direction
        );
    }
}
