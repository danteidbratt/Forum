package se.donut.postservice.service;

import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.PostAccessor;

import java.time.Instant;
import java.util.UUID;

public class PostService {

    private final PostAccessor postAccessor;

    public PostService(PostAccessor postAccessor) {
        this.postAccessor = postAccessor;
    }

    public UUID createPost(UUID forumUuid, UUID authorUuid, String authorName, String title, String link, String content) {
        UUID postUuid = UUID.randomUUID();
        Post post = new Post(
                postUuid,
                authorUuid,
                authorName,
                content,
                0,
                Instant.now(),
                false,
                forumUuid,
                title,
                link
        );
        postAccessor.createPost(post);
        return postUuid;
    }
}
