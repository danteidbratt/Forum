package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentAccessor {

    List<Comment> getComments(UUID parentUuid);

    void createComment(Comment comment);

}
