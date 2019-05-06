package se.donut.postservice.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import se.donut.postservice.model.Direction;

import java.util.Date;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO extends AbstractDTO {

    private final UUID authorUuid;
    private final String authorName;
    private final String title;
    private final String link;
    private final String content;
    private final int score;
    private final Direction myVote;

    public PostDTO(
            UUID uuid,
            Date createdAt,
            UUID authorUuid,
            String authorName,
            String title,
            String link,
            String content,
            int score,
            Direction myVote) {
        super(uuid, createdAt);
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.title = title;
        this.link = link;
        this.content = content;
        this.score = score;
        this.myVote = myVote;
    }
}
