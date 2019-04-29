package se.donut.postservice.service;

import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.repository.CommentAccessor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommentService {

    private final CommentAccessor commentAccessor;

    public CommentService(CommentAccessor commentAccessor) {
        this.commentAccessor = commentAccessor;
    }

    public UUID createComment(UUID postUUid, UUID parentUuid, UUID authorUuid, String authorName, String content) {
        UUID commentUuid = UUID.randomUUID();
        Comment comment = new Comment(
                commentUuid,
                authorUuid,
                authorName,
                content,
                1,
                Instant.now(),
                false,
                parentUuid,
                postUUid
        );
        commentAccessor.createComment(comment);
        return commentUuid;
    }

    public List<CommentDTO> getComments(UUID parentUuid) {
        List<CommentDTO> comments = commentAccessor.getComments(parentUuid)
                .stream()
                .map(Comment::toApiModel)
                .collect(Collectors.toList());
        return comments;
    }

}
