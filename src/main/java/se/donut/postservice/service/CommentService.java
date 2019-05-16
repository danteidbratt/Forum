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
import se.donut.postservice.util.DataValidator;

import java.util.*;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.COMMENT_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

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
        content = DataValidator.validateCommentContent(content);
        Post post = postDAO.get(postUuid)
                .orElseThrow(() -> new PostServiceException(
                        POST_NOT_FOUND,
                        String.format("Could not find post with uuid %s.", postUuid))
                );

        if (!post.getUuid().equals(parentUuid)) {
            Comment parentComment = commentDAO.getComment(parentUuid)
                    .orElseThrow(() -> new PostServiceException(
                            COMMENT_NOT_FOUND,
                            String.format("Could not find parent comment with uuid %s.", parentUuid)
                    ));
            if (!post.getUuid().equals(parentComment.getPostUuid())) {
                throw new PostServiceException(
                        COMMENT_NOT_FOUND,
                        String.format("Parent comment with uuid %s does not belong to post with uuid %s.",
                                parentUuid, postUuid
                        )
                );
            }
        }
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
        commentDAO.createCommentAndUpdateCounter(comment);
        return commentUuid;
    }

    public List<CommentDTO> getCommentsByPost(UUID postUuid, SortType sortType) {
        return getCommentsByPost(postUuid, sortType, null);
    }

    /**
     * @param postUuid
     * @param sortType
     * @param userUuid
     * @return A tree of nested comments associated to specific post.
     */
    public List<CommentDTO> getCommentsByPost(UUID postUuid, SortType sortType, UUID userUuid) {
        List<Comment> comments = commentDAO.getCommentsByPost(postUuid);
        Map<UUID, List<CommentDTO>> commentDTOS = buildApiModelsAsMap(comments, sortType, userUuid);
        return buildCommentTree(commentDTOS, postUuid);
    }

    /**
     * @param authorUuid
     * @param sortType
     * @return A flat list of comments by author.
     */
    public List<CommentDTO> getCommentsByAuthor(UUID authorUuid, SortType sortType) {
        return getCommentsByAuthor(authorUuid, sortType, null);
    }

    /**
     * @param authorUuid
     * @param sortType
     * @param userUuid
     * @return A flat list of comments by author.
     */
    public List<CommentDTO> getCommentsByAuthor(UUID authorUuid, SortType sortType, UUID userUuid) {
        List<Comment> comments = commentDAO.getCommentsByAuthor(authorUuid);
        return buildApiModels(comments, sortType, userUuid);
    }

    public List<CommentDTO> getByLikes(UUID userUuid, SortType sortType) {
        List<Comment> comments = commentDAO.getCommentsByLikes(userUuid);
        return buildApiModels(comments, sortType, userUuid);
    }

    public void vote(UUID userUuid, UUID commentUuid, Direction direction) {
        commentDAO.getComment(commentUuid).orElseThrow(() -> new PostServiceException(
                COMMENT_NOT_FOUND,
                String.format("Could not find comment with uuid %s", commentUuid)
        ));
        Vote vote = new Vote(commentUuid, userUuid, direction);
        commentDAO.voteAndUpdateScore(vote);
    }

    public void deleteVote(UUID userUuid, UUID commentUuid) {
        commentDAO.getVote(userUuid, commentUuid).ifPresent(commentDAO::deleteVoteAndUpdateScore);
    }

    private List<CommentDTO> buildApiModels(
            List<Comment> comments,
            SortType sortType,
            UUID userUuid
    ) {
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }
        Decoration decoration = getDecoration(comments, userUuid);
        return comments.stream()
                .map(c -> new SortableComment(c, sortType))
                .sorted(sortType.getComparator())
                .map(c -> buildApiModel(c, decoration))
                .collect(Collectors.toList());
    }

    /**
     * Should only be used when getting a posts comment field.
     *
     * @param comments
     * @param sortType
     * @param userUuid
     * @return Key: parentUuid, Value: List of commentsDTO who share same parent
     */
    private Map<UUID, List<CommentDTO>> buildApiModelsAsMap(
            List<Comment> comments,
            SortType sortType,
            UUID userUuid
    ) {
        if (comments.isEmpty()) {
            return new HashMap<>();
        }
        Decoration decoration = getDecoration(comments, userUuid);
        Map<UUID, List<CommentDTO>> commentDTOs = comments.stream()
                .map(c -> new SortableComment(c, sortType))
                .sorted(sortType.getComparator())
                .collect(Collectors.groupingBy(Comment::getParentUuid))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        x -> x.getValue().stream()
                                .map(c -> buildApiModel(c, decoration))
                                .collect(Collectors.toList())
                ));
        return commentDTOs;
    }

    private List<CommentDTO> buildCommentTree(Map<UUID, List<CommentDTO>> comments, UUID parentUuid) {
        if (!comments.containsKey(parentUuid))
            return new ArrayList<>();
        for (CommentDTO comment : comments.get(parentUuid))
            comment.setChildren(buildCommentTree(comments, comment.getUuid()));
        return comments.get(parentUuid);
    }

    private Decoration getDecoration(List<Comment> comments, UUID userUuid) {
        List<UUID> commentUuids = comments.stream().map(Comment::getUuid).collect(Collectors.toList());
        List<UUID> authorUuids = comments.stream().map(Comment::getAuthorUuid).collect(Collectors.toList());
        Map<UUID, User> authors = userDAO.get(authorUuids);
        Map<UUID, Vote> myVotes = userUuid != null ?
                commentDAO.getVotesByUser(userUuid, commentUuids).stream()
                        .collect(Collectors.toMap(Vote::getTargetUuid, x -> x))
                : new HashMap<>();
        return new Decoration(authors, myVotes);
    }

    private CommentDTO buildApiModel(Comment comment, Decoration decoration) {
        Vote myVote = decoration.myVotes.get(comment.getUuid());
        User author = decoration.authors.get(comment.getAuthorUuid());
        return comment.toApiModel(
                author != null ? author.getName() : null,
                myVote != null ? myVote.getDirection() : null
        );
    }

    private static class Decoration {
        private final Map<UUID, User> authors;
        private final Map<UUID, Vote> myVotes;

        private Decoration(Map<UUID, User> authors, Map<UUID, Vote> myVotes) {
            this.authors = authors;
            this.myVotes = myVotes;
        }
    }
}