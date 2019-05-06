package se.donut.postservice.service;

import org.jdbi.v3.sqlobject.transaction.Transaction;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.*;
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

    public List<CommentDTO> getCommentViews(UUID userUuid, UUID postUuid, SortType sortType) {
        List<CommentView> commentViews = commentDAO.getCommentViews(postUuid, userUuid, sortType.getColumnName());
        Map<UUID, List<CommentView>> commentViewMap = commentViews.stream()
//                .sorted(sortType.getComparator())
                .collect(Collectors.groupingBy(CommentView::getParentUuid));
        Map<UUID, List<CommentDTO>> comments = commentViewMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        x -> x.getValue()
                                .stream()
                                .map(CommentView::toApiModel)
                                .collect(Collectors.toList())
                ));

        return stackCommentsRecursively(comments, postUuid);
    }

    public List<CommentDTO> getCommentsByPost(UUID postUuid, SortType sortType) {
        return getCommentsByPost(null, postUuid, sortType);
    }

    public List<CommentDTO> getCommentsByPost(UUID userUuid, UUID postUuid, SortType sortType) {
        List<Comment> comments = commentDAO.getCommentsByPostUuid(postUuid);

        List<UUID> authorUuids = comments.stream()
                .map(Comment::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> authors = userDAO.get(authorUuids);

        Map<UUID, Vote> myVotes = userUuid != null ?
                commentDAO.getVotes(userUuid, postUuid).stream()
                        .collect(Collectors.toMap(Vote::getTargetUuid, x -> x))
                : new HashMap<>();

        Map<UUID, List<Comment>> commentMap = comments.stream()
                .sorted(sortType.getComparator())
                .collect(Collectors.groupingBy(Comment::getParentUuid));

        return buildCommentTree(commentMap, myVotes, authors, postUuid);
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

    private List<CommentDTO> buildCommentTree(
            Map<UUID, List<Comment>> comments,
            Map<UUID, Vote> myVotes,
            Map<UUID, User> authors,
            UUID postUuid
    ) {
        Map<UUID, List<CommentDTO>> commentDTOs = comments.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        x -> x.getValue().stream().map(c -> {
                            Vote myVote = myVotes.get(c.getUuid());
                            User author = authors.get(c.getAuthorUuid());
                            return c.toApiModel(
                                    author != null ? author.getName() : null,
                                    myVote != null ? myVote.getDirection() : null
                            );
                        }).collect(Collectors.toList())
                ));
        return stackCommentsRecursively(commentDTOs, postUuid);
    }

    private List<CommentDTO> stackCommentsRecursively(Map<UUID, List<CommentDTO>> comments, UUID parentUuid) {
        if (!comments.containsKey(parentUuid)) {
            return new ArrayList<>();
        }

        for (CommentDTO comment : comments.get(parentUuid)) {
            comment.setChildren(stackCommentsRecursively(comments, comment.getUuid()));
        }

        return comments.get(parentUuid);
    }

}