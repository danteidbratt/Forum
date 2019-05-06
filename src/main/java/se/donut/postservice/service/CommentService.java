package se.donut.postservice.service;

import org.jdbi.v3.sqlobject.transaction.Transaction;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.model.domain.Vote;
import se.donut.postservice.repository.postgresql.CommentDAO;
import se.donut.postservice.repository.postgresql.PostDAO;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.resource.request.SortType;

import java.util.*;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.*;

public class CommentService {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public CommentService(CommentDAO commentDAO, PostDAO postDAO, UserDAO userDAO) {
        this.commentDAO = commentDAO;
        this.postDAO = postDAO;
        this.userDAO = userDAO;
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
        commentDAO.createComment(comment);
        return commentUuid;
    }

//    public List<CommentDTO> getCommentTreeByPost(UUID postUuid, SortType sortType) {
//        List<Comment> comments = commentDAO.getCommentsByPostUuid(postUuid);
//        return buildCommentTree(comments, postUuid);
//    }

    public List<CommentDTO> getCommentTreeByPost(UUID userUuid, UUID postUuid, SortType sortType) {
        List<Comment> comments = commentDAO.getCommentsByPostUuid(postUuid);
        Map<UUID, Vote> myVotes = commentDAO.getVotes(userUuid, postUuid);

        List<UUID> userUuids = comments.stream()
                .map(Comment::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> authors = userDAO.get(userUuids);
        Map<UUID, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(Comment::getParentUuid));

        Map<UUID, List<CommentDTO>> dtos = new HashMap<>();
        commentMap.forEach((key, value) -> dtos.put(
                key,
                value.stream().map(c -> {
                    Vote myVote = myVotes.get(c.getUuid());
                    User author = authors.get(c.getAuthorUuid());
                    return c.toApiModel(
                            author != null ? author.getName() : null,
                            myVote != null ? myVote.getDirection() : null
                    );
                }).collect(Collectors.toList())
        ));

        return buildCommentTree(dtos, postUuid);

    }

    public void vote(UUID userUuid, UUID postUuid, UUID commentUuid, Direction direction) {
        Vote vote = new Vote(
                commentUuid,
                postUuid,
                userUuid,
                direction
        );
        commentDAO.vote(vote);
    }

    public void deleteVote(UUID userUuid, UUID commentUuid) {
        commentDAO.deleteVote(userUuid, commentUuid);
    }

    private void validateParent(UUID postUuid, UUID parentUuid) {
        Post post = postDAO.get(postUuid)
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));

        if (post.getUuid().equals(parentUuid)) {
            return;
        }
        Comment parentComment = commentDAO.getComment(parentUuid)
                .orElseThrow(() -> new PostServiceException(COMMENT_NOT_FOUND));

        if (!post.getUuid().equals(parentComment.getPostUuid())) {
            throw new PostServiceException(COMMENT_NOT_FOUND);
        }
    }

    private List<CommentDTO> buildCommentTree(Map<UUID, List<CommentDTO>> comments, UUID parentUuid) {
        if (!comments.containsKey(parentUuid)) {
            return new ArrayList<>();
        }

        for (CommentDTO comment : comments.get(parentUuid)) {
            comment.setChildren(buildCommentTree(comments, comment.getUuid()));
        }

        return comments.get(parentUuid);
    }
}
//    private List<CommentDTO> buildCommentTree(
//            UUID postUuid,
//            List<Comment> comments,
//            SortType sortType
//    ) {
//        Map<UUID, List<Comment>> commentMap = comments.stream()
//                .sorted(sortType.getComparator())
//                .collect(Collectors.groupingBy(Comment::getParentUuid));
//
//        return recurse(commentMap, postUuid);
//    }
//
//    private List<CommentDTO> recurse(Map<UUID, List<Comment>> commentMap, UUID parentUuid) {
//        if (!commentMap.containsKey(parentUuid)) {
//            return null;
//        }
//        List<CommentDTO> result = new ArrayList<>();
//        for (Comment comment : commentMap.get(parentUuid)) {
//            result.add(comment.toApiModel(
//                    recurse(
//                            commentMap,
//                            comment.getUuid()),
//                    null));
//        }
//        return result;
//    }