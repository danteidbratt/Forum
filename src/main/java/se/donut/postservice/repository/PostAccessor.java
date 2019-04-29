package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Post;

public interface PostAccessor {

    void createPost(Post post);

}
