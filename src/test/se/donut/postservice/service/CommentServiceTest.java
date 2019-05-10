package se.donut.postservice.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.postgresql.CommentDAO;
import se.donut.postservice.repository.postgresql.PostDAO;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.resource.request.SortType;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.*;
import static se.donut.postservice.resource.request.SortType.*;

public class CommentServiceTest {

    private CommentDAO commentDAO;
    private PostDAO postDAO;
    private UserDAO userDAO;
    private CommentService commentService;

    @Before
    public void setup() {
        commentDAO = mock(CommentDAO.class);
        postDAO = mock(PostDAO.class);
        userDAO = mock(UserDAO.class);
        commentService = new CommentService(commentDAO, postDAO, userDAO);
    }

    @Test
    public void shouldBeAbleToSortCommentByTop() {
        // Arrange
        UUID postUuid = UUID.randomUUID();
        int numberOfComments = 10;
        List<Comment> comments = generateListOfRandomRootComments(numberOfComments, postUuid);
        when(commentDAO.getCommentsByPostUuid(postUuid)).thenReturn(comments);

        // Act
        List<CommentDTO> commentTree = commentService.getCommentsByPost(postUuid, TOP);

        // Assert
        assertEquals(numberOfComments, commentTree.size());
        int previousScore = Integer.MAX_VALUE;
        for (CommentDTO commentDTO : commentTree) {
            assertTrue(commentDTO.getScore() <= previousScore);
            previousScore = commentDTO.getScore();
        }
    }

    @Test
    public void shouldBeAbleToSortCommentByNew() {
        // Arrange
        UUID postUuid = UUID.randomUUID();
        int numberOfComments = 10;
        List<Comment> comments = generateListOfRandomRootComments(numberOfComments, postUuid);
        when(commentDAO.getCommentsByPostUuid(postUuid)).thenReturn(comments);

        // Act
        List<CommentDTO> commentTree = commentService.getCommentsByPost(postUuid, NEW);

        // Assert
        assertEquals(numberOfComments, commentTree.size());
        Date previousInstant = new Date(Long.MAX_VALUE);
        for (CommentDTO commentDTO : commentTree) {
            assertTrue(previousInstant.after(commentDTO.getCreatedAt()));
            previousInstant = commentDTO.getCreatedAt();
        }
    }

    @Test
    public void shouldBeAbleToSortCommentsByHot() {
        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        DateTime now = DateTime.now();
        Comment comment1 = generateComment(postUuid, postUuid, 100, now.minusMinutes(6).toDate());
        Comment comment2 = generateComment(postUuid, postUuid, 50, now.minusMinutes(3).toDate());
        Comment comment3 = generateComment(postUuid, postUuid, 10, now.minusMinutes(0).toDate());
        List<Comment> comments = Arrays.asList(comment1, comment2, comment3);
        when(commentDAO.getCommentsByPostUuid(postUuid)).thenReturn(comments);

        List<CommentDTO> commentDTOS = commentService.getCommentsByPost(postUuid, HOT, userUuid);

        assertEquals(comment2.getScore(), commentDTOS.get(0).getScore());
        assertEquals(comment3.getScore(), commentDTOS.get(1).getScore());
        assertEquals(comment1.getScore(), commentDTOS.get(2).getScore());
    }

    @Test
    public void shouldBeAbleToNestCommentsRecursively() {
        // Arrange
        UUID postUuid = UUID.randomUUID();
        Date now = new Date();
        Comment comment0 = generateComment(postUuid, postUuid, 2, now);
        Comment comment00 = generateComment(postUuid, comment0.getUuid(), 2, now);
        Comment comment000 = generateComment(postUuid, comment00.getUuid(), 2, now);
        Comment comment01 = generateComment(postUuid, comment0.getUuid(), 1, now);
        Comment comment1 = generateComment(postUuid, postUuid, 1, now);
        List<Comment> comments = Arrays.asList(comment0, comment00, comment000, comment01, comment1);
        when(commentDAO.getCommentsByPostUuid(postUuid)).thenReturn(comments);

        // Act
        List<CommentDTO> commentTree = commentService.getCommentsByPost(postUuid, TOP);

        // Assert
        assertEquals(commentTree.get(0).getUuid(), comment0.getUuid());
        assertEquals(commentTree.get(0).getChildren().get(0).getUuid(), comment00.getUuid());
        assertEquals(commentTree.get(0).getChildren().get(0).getChildren().get(0).getUuid(), comment000.getUuid());
        assertEquals(commentTree.get(0).getChildren().get(1).getUuid(), comment01.getUuid());
        assertEquals(commentTree.get(1).getUuid(), comment1.getUuid());
    }

    @Test
    public void shouldNotBeAbleToCreateCommentWithEmptyContent() {
        // Arrange
        UUID postUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        String content = "";

        // Act
        try {
            commentService.createComment(postUuid, postUuid, userUuid, content);
            fail();
        } catch (PostServiceException e) {
            assertEquals(INVALID_CONTENT, e.getExceptionType());
        }

        // Assert
        verify(commentDAO, never()).createComment(any());
    }

    @Test
    public void shouldNotBeAbleToCreateCommentOnNonExistingComment() {
        // Arrange
        UUID parentUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        UUID authorUuid = UUID.randomUUID();
        Post post = mock(Post.class);
        when(post.getUuid()).thenReturn(postUuid);
        when(postDAO.get(postUuid)).thenReturn(Optional.of(post));
        when(commentDAO.getComment(parentUuid)).thenReturn(Optional.empty());

        // Act
        try {
            commentService.createComment(
                    postUuid,
                    parentUuid,
                    authorUuid,
                    "some content"
            );
            fail();
        } catch (PostServiceException e) {
            Assert.assertEquals(COMMENT_NOT_FOUND, e.getExceptionType());
        }

        // Assert
        verify(commentDAO, never()).createComment(any());
    }

    @Test
    public void shouldNotBeAbleToCreateCommentOnNonExistingPost() {
        UUID parentUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        UUID authorUuid = UUID.randomUUID();
        when(postDAO.get(postUuid)).thenReturn(Optional.empty());

        // Act
        try {
            commentService.createComment(
                    postUuid,
                    parentUuid,
                    authorUuid,
                    "some content"
            );
            fail();
        } catch (PostServiceException e) {
            Assert.assertEquals(POST_NOT_FOUND, e.getExceptionType());
        }

        // Assert
        verify(commentDAO, never()).createComment(any());
    }

    private List<Comment> generateListOfRandomRootComments(int numberOfComments, UUID postUuid) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < numberOfComments; i++) {
            int score = (int) (Math.random() * 100);
            Date date = Date.from(Instant.ofEpochSecond((long) (Math.random() * 100000000)));
            comments.add(generateComment(postUuid, postUuid, score, date));
        }
        return comments;
    }

    private Comment generateComment(UUID postUuid, UUID parentUuid, int score, Date date) {
        return new Comment(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "content",
                score,
                parentUuid,
                postUuid,
                date
        );
    }

}