package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Post;

import java.util.Optional;
import java.util.UUID;

public interface PostAccessor {

    Optional<Post> getPost(UUID postUuid);

    void deletePost(UUID uuid);

    void createPost(Post post);

}
