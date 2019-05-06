package se.donut.postservice.service;

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
import static se.donut.postservice.exception.ExceptionType.COMMENT_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

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
        List<CommentDTO> commentTree = commentService.getCommentsByPost(postUuid, SortType.TOP);

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
        List<CommentDTO> commentTree = commentService.getCommentsByPost(postUuid, SortType.NEW);

        // Assert
        assertEquals(numberOfComments, commentTree.size());
        Date previousInstant = new Date(Long.MAX_VALUE);
        for (CommentDTO commentDTO : commentTree) {
            assertTrue(previousInstant.after(commentDTO.getCreatedAt()));
            previousInstant = commentDTO.getCreatedAt();
        }
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
        List<CommentDTO> commentTree = commentService.getCommentsByPost(postUuid, SortType.TOP);

        // Assert
        assertEquals(commentTree.get(0).getUuid(), comment0.getUuid());
        assertEquals(commentTree.get(0).getChildren().get(0).getUuid(), comment00.getUuid());
        assertEquals(commentTree.get(0).getChildren().get(0).getChildren().get(0).getUuid(), comment000.getUuid());
        assertEquals(commentTree.get(0).getChildren().get(1).getUuid(), comment01.getUuid());
        assertEquals(commentTree.get(1).getUuid(), comment1.getUuid());
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
            Date date = Date.from(Instant.ofEpochMilli((long) (Math.random() * 100000000)));
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