package se.donut.postservice.service;

import org.jdbi.v3.sqlobject.transaction.Transaction;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.model.domain.Vote;
import se.donut.postservice.repository.CommentAccessor;
import se.donut.postservice.repository.PostAccessor;
import se.donut.postservice.resource.request.SortType;

import java.util.*;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.*;

public class CommentService {

    private final CommentAccessor commentAccessor;
    private final PostAccessor postAccessor;

    public CommentService(CommentAccessor commentAccessor, PostAccessor postAccessor) {
        this.commentAccessor = commentAccessor;
        this.postAccessor = postAccessor;
    }

    @Transaction
    public UUID createComment(
            UUID postUuid,
            UUID parentUuid,
            UUID authorUuid,
            String content
    ) {
        validateParent(postUuid, parentUuid);
        UUID commentUuid = UUID.randomUUID();
        Comment comment = new Comment(
                commentUuid,
                authorUuid,
                content,
                0,
                parentUuid,
                postUuid,
                new Date()
        );
        commentAccessor.createComment(comment);
        return commentUuid;
    }

    public List<CommentDTO> getCommentTreeByPost(UUID postUuid, SortType sortType) {
        List<Comment> comments = commentAccessor.getCommentsByPostUuid(postUuid);
        return buildCommentTree(postUuid, comments, sortType);
    }

    public void vote(UUID userUuid, UUID commentUuid, Direction direction) {
        Optional<Vote> currentVote = commentAccessor.getVote(userUuid, commentUuid);
        if (currentVote.isPresent()) {
            throw new PostServiceException(VOTE_ALREADY_EXISTS);
        }

        Vote vote = new Vote(
                commentUuid,
                userUuid,
                direction
        );
        commentAccessor.vote(vote);
    }

    public void deleteVote(UUID userUuid, UUID commentUuid) {
        Vote currentVote = commentAccessor.getVote(userUuid, commentUuid)
                .orElseThrow(() -> new PostServiceException(VOTE_NOT_FOUND));
        commentAccessor.deleteVote(currentVote);
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
            result.add(comment.toApiModel(recurse(commentMap, comment.getUuid()), null, null));
        }
        return result;
    }

}
