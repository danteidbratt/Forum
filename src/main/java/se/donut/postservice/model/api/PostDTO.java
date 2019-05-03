package se.donut.postservice.model.api;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class PostDTO extends AbstractDTO {

    private final UUID authorUuid;
    private final String authorName;
    private final String title;
    private final String link;
    private final String content;
    private final int score;

    public PostDTO(UUID uuid, Date createdAt, UUID authorUuid, String authorName, String title, String link, String content, int score) {
        super(uuid, createdAt);
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.title = title;
        this.link = link;
        this.content = content;
        this.score = score;
    }
}
