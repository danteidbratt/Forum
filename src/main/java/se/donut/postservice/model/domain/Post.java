package se.donut.postservice.model.domain;

import lombok.Getter;
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
            String authorName,
            String content,
            int score,
            UUID forumUuid,
            String title,
            String link,
            Date createdAt
    ) {
        super(uuid, authorUuid, authorName, content, score, createdAt);
        this.forumUuid = forumUuid;
        this.title = title;
        this.link = link;
    }

    public PostDTO toApiModel() {
        return new PostDTO(
                this.getUuid(),
                this.getCreatedAt(),
                this.getAuthorUuid(),
                this.getAuthorName(),
                this.getTitle(),
                this.getLink(),
                this.getContent(),
                this.getScore()
        );
    }
}
