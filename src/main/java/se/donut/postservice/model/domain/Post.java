package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.util.TimeAgoCalculator;

import java.util.Date;
import java.util.UUID;

@Getter
public class Post extends Submission {

    private final int commentCount;
    private final UUID forumUuid;
    private final String title;

    public Post(
            UUID uuid,
            UUID authorUuid,
            String content,
            int score,
            UUID forumUuid,
            String title,
            Date createdAt,
            int commentCount
    ) {
        super(uuid, authorUuid, content, score, createdAt);
        this.forumUuid = forumUuid;
        this.title = title;
        this.commentCount = commentCount;
    }

    public PostDTO toApiModel(String forumName, String authorName, Date now, Direction direction) {
        return new PostDTO(
                this.getUuid(),
                getCreatedAt(),
                now,
                this.getForumUuid(),
                forumName,
                this.getAuthorUuid(),
                authorName,
                this.getCommentCount(),
                this.getTitle(),
                this.getContent(),
                this.getScore(),
                direction
        );
    }
}
