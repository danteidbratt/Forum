package se.donut.postservice.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import se.donut.postservice.model.Direction;

import java.util.Date;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PostDTO extends AbstractDTO {

    private final UUID forumUuid;
    private final String forumName;
    private final UUID authorUuid;
    private final String authorName;
    private final int commentCount;
    private final String title;
    private final String content;
    private final int score;
    private final Direction myVote;

    public PostDTO(
            UUID uuid,
            Date createdAt,
            Date now,
            UUID forumUuid,
            String forumName,
            UUID authorUuid,
            String authorName,
            int commentCount,
            String title,
            String content,
            int score,
            Direction myVote
    ) {
        super(uuid, createdAt, now);
        this.forumUuid = forumUuid;
        this.forumName = forumName;
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.commentCount = commentCount;
        this.title = title;
        this.content = content;
        this.score = score;
        this.myVote = myVote;
    }
}
