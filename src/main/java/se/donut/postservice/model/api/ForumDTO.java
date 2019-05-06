package se.donut.postservice.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForumDTO extends AbstractDTO {

    private final UUID authorUuid;
    private final String authorName;
    private final String name;
    private final String description;
    private final int subscribers;
    private final Boolean subscribed;

    public ForumDTO(
            UUID uuid,
            Date createdAt,
            UUID authorUuid,
            String authorName,
            String name,
            String description,
            int subscribers,
            Boolean subscribed) {
        super(uuid, createdAt);
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.name = name;
        this.description = description;
        this.subscribers = subscribers;
        this.subscribed = subscribed;
    }
}
