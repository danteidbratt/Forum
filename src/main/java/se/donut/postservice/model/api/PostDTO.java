package se.donut.postservice.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private UUID uuid;
    private UUID authorUuid;
    private String authorName;
    private String title;
    private String link;
    private String content;
    private int score;
    private Instant createdAt;
    private List<CommentDTO> comments;

}
