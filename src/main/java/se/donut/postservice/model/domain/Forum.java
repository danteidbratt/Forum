package se.donut.postservice.model.domain;

import lombok.Getter;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import se.donut.postservice.model.api.ForumDTO;

import java.util.Date;
import java.util.UUID;

@Getter
public class Forum extends Submission {

    private final String name;

    public Forum(
            UUID uuid,
            UUID authorUuid,
            String name,
            String content,
            int score,
            Date createdAt
    ) {
        super(uuid, authorUuid, content, score, createdAt);
        this.name = name;
    }

    public ForumDTO toApiModel(String authorName) {
        return toApiModel(authorName, null);
    }

    public ForumDTO toApiModel(String authorName, Boolean subscribed) {
        return new ForumDTO(
                this.getUuid(),
                this.getCreatedAt(),
                this.getAuthorUuid(),
                authorName,
                this.getName(),
                this.getName(),
                this.getScore(),
                subscribed
        );
    }
}
