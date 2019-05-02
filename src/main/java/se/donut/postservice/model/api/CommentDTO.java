package se.donut.postservice.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class CommentDTO {

    private UUID uuid;
    private UUID authorUuid;
    private String authorName;
    private String content;
    private int score;
    private Instant createdAt;
    private List<CommentDTO> children;

}
