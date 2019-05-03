package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.model.domain.Vote;
import se.donut.postservice.repository.CommentAccessor;
import se.donut.postservice.repository.PostAccessor;
import se.donut.postservice.resource.request.SortType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                parentUuid,
                postUuid,
                Instant.now()
        );
        commentAccessor.createComment(comment);
        return commentUuid;
    }

    List<CommentDTO> getCommentTreeByPost(UUID postUuid, SortType sortType) {
        List<Comment> comments = commentAccessor.getCommentsByPostUuid(postUuid);
        return buildCommentTree(postUuid, comments, sortType);
    }

    public void vote(UUID userUuid, UUID commentUuid, Direction direction) {
        Vote vote = new Vote(
                UUID.randomUUID(),
                commentUuid,
                userUuid,
                direction,
                Instant.now()
        );

        commentAccessor.vote(vote);
    }

    private void validateParent(UUID postUuid, UUID parentUuid) {
        Post post = postAccessor.get(postUuid)
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));

        if (post.getUuid().equals(parentUuid)) {
            return;
        }
        Comment parentComment = commentAccessor.getComment(parentUuid)
                .orElseThrow(() -> new PostServiceException(COMMENT_NOT_FOUND));

        if (!post.getUuid().equals(parentComment.getPostUuid())) {
            throw new PostServiceException(COMMENT_NOT_FOUND);
        }
    }

    private List<CommentDTO> buildCommentTree(UUID postUuid, List<Comment> comments, SortType sortType) {
        Map<UUID, List<Comment>> commentMap = comments.stream()
                .sorted(sortType.getComparator())
                .collect(Collectors.groupingBy(Comment::getParentUuid));

        return recurse(commentMap, postUuid);
    }

    private List<CommentDTO> recurse(Map<UUID, List<Comment>> commentMap, UUID parentUuid) {
        if (!commentMap.containsKey(parentUuid)) {
            return null;
        }
        List<CommentDTO> result = new ArrayList<>();
        for (Comment comment : commentMap.get(parentUuid)) {
            result.add(comment.toApiModel(recurse(commentMap, comment.getUuid())));
        }
        return result;
    }

}
