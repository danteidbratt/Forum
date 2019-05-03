package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Post;

import java.util.List;
import java.util.UUID;

public interface PostAccessor extends EntityAccessor<Post> {

    List<Post> getByForum(UUID forumUuid);

}
