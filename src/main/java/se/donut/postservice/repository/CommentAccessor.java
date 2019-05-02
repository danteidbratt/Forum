package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentAccessor {

    Optional<Comment> getComment(UUID uuid);

    List<Comment> getCommentsByPostUuid(UUID postUuid);

    void createComment(Comment comment);

    void vote(Vote vote);

}
