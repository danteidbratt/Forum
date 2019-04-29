package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentAccessor {

    List<Comment> getComments(UUID forumUuid, UUID postUuid, List<UUID> path);

    void createComment(Comment comment);

}
