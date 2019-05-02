package se.donut.postservice.model.api;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class PostThreadDTO extends PostDTO {

    private final List<CommentDTO> comments;

    public PostThreadDTO(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String title,
            String link,
            String content,
            int score,
            Instant createdAt,
            List<CommentDTO> comments
    ) {
        super(uuid, authorUuid, authorName, title, link, content, score, createdAt);
        this.comments = comments;
    }
}
