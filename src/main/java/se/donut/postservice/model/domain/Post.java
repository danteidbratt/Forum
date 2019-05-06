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
    private final String link;

    public Post(
            UUID uuid,
            UUID authorUuid,
            String content,
            int score,
            UUID forumUuid,
            String title,
            String link,
            Date createdAt
    ) {
        super(uuid, authorUuid, content, score, createdAt);
        this.forumUuid = forumUuid;
        this.title = title;
        this.link = link;
    }

    public PostDTO toApiModel() {
        return toApiModel(null, null);
    }

    public PostDTO toApiModel(String authorName) {
        return toApiModel(authorName, null);
    }

    public PostDTO toApiModel(String authorName, Direction direction) {
        return new PostDTO(
                this.getUuid(),
                this.getCreatedAt(),
                this.getAuthorUuid(),
                authorName,
                this.getTitle(),
                this.getLink(),
                this.getContent(),
                this.getScore(),
                direction
        );
    }
}
