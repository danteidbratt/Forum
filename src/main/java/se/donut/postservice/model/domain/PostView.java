package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.PostDTO;

import java.util.Date;
import java.util.UUID;

@Getter
public class PostView extends Post {

    private final String authorName;
    private final Direction myVote;

    public PostView(
            UUID uuid,
            UUID authorUuid,
            String content,
            int score,
            UUID forumUuid,
            String title,
            String link,
            Date createdAt,
            String authorName,
            Direction myVote
    ) {
        super(uuid, authorUuid, content, score, forumUuid, title, link, createdAt);
        this.authorName = authorName;
        this.myVote = myVote;
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
                this.getScore(),
                this.getMyVote()
        );
    }
}
