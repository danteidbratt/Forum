package se.donut.postservice.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private UUID uuid;
    private UUID authorUuid;
    private String authorName;
    private String content;
    private int score;
    private Instant createdAt;
}
