package se.donut.postservice.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import se.donut.postservice.model.Direction;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@JsonInclude(NON_NULL)
public class CommentDTO {

    private UUID uuid;
    private UUID authorUuid;
    private String authorName;
    private String content;
    private int score;
    private Date createdAt;
    private Direction myVote;
    private List<CommentDTO> children;

    public CommentDTO(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            Date createdAt,
            Direction myVote
    ) {
        this.uuid = uuid;
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.content = content;
        this.score = score;
        this.createdAt = createdAt;
        this.myVote = myVote;
    }
}
