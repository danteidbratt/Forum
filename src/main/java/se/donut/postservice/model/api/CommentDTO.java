package se.donut.postservice.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import se.donut.postservice.model.Direction;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@JsonInclude(NON_NULL)
public class CommentDTO extends AbstractDTO{

    private final UUID postUuid;
    private final UUID authorUuid;
    private final String authorName;
    private final String content;
    private final int score;
    private final Direction myVote;
    private List<CommentDTO> children;


    public CommentDTO(
            UUID uuid,
            Date createdAt,
            Date now,
            UUID postUuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            Direction myVote
    ) {
        super(uuid, createdAt, now);
        this.postUuid = postUuid;
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.content = content;
        this.score = score;
        this.myVote = myVote;
    }

}
