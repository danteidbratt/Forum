package se.donut.postservice.model.domain;

import lombok.Getter;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.util.Date;
import java.util.UUID;

@Getter
public abstract class Submission extends AbstractEntity {

    private final UUID authorUuid;
    private final String content;
    private final int score;

    Submission(
            UUID uuid,
            UUID authorUuid,
            String content,
            int score,
            Date createdAt
    ) {
        super(uuid, createdAt);
        this.authorUuid = authorUuid;
        this.content = content;
        this.score = score;
    }
}
