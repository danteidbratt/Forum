package se.donut.postservice.service;

import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.repository.ForumAccessor;

import java.time.Instant;
import java.util.UUID;

public class ForumService {

    private final ForumAccessor forumAccessor;

    public ForumService(ForumAccessor ForumAccessor) {
        this.forumAccessor = ForumAccessor;
    }

    public UUID createForum(UUID userUuid, String name, String description) {
        UUID forumUuid = UUID.randomUUID();
        Forum forum = new Forum(
                forumUuid,
                userUuid,
                name,
                description,
                Instant.now(),
                false
        );
        forumAccessor.createForum(forum);
        return forumUuid;
    }
}
