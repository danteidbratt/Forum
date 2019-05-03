package se.donut.postservice.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.donut.postservice.model.Direction;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@AllArgsConstructor
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

}