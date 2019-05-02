package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentAccessor {

    Optional<Comment> getComment(UUID uuid);

    List<Comment> getComments(UUID parentUuid);

    void createComment(Comment comment);

}
