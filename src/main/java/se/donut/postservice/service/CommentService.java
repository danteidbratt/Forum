package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.CommentAccessor;
import se.donut.postservice.repository.PostAccessor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.COMMENT_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

public class CommentService {

    private final CommentAccessor commentAccessor;
    private final PostAccessor postAccessor;

    public CommentService(CommentAccessor commentAccessor, PostAccessor postAccessor) {
        this.commentAccessor = commentAccessor;
        this.postAccessor = postAccessor;
    }

    public UUID createComment(
            UUID postUuid,
            UUID parentUuid,
            UUID authorUuid,
            String authorName,
            String content
    ) {
        validateParent(postUuid, parentUuid);
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
                postUuid
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

    private void validateParent(UUID postUuid, UUID parentUuid) {
        Post post = postAccessor.getPost(postUuid)
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));

        if (postUuid == parentUuid) {
            // if comment is root comment
            return;
        }
        Comment parentComment = commentAccessor.getComment(parentUuid)
                .orElseThrow(() -> new PostServiceException(COMMENT_NOT_FOUND));

        if (!postUuid.equals(parentComment.getPostUuid())) {
            throw new PostServiceException(COMMENT_NOT_FOUND);
        }
    }

}
