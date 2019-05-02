package se.donut.postservice.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.CommentAccessor;
import se.donut.postservice.repository.PostAccessor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.COMMENT_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

public class CommentServiceTest {

    private CommentAccessor commentAccessor;
    private PostAccessor postAccessor;
    private CommentService commentService;

    @Before
    public void setup() {
        commentAccessor = mock(CommentAccessor.class);
        postAccessor = mock(PostAccessor.class);
        commentService = new CommentService(commentAccessor, postAccessor);
    }

    @Test
    public void shouldNotBeAbleToCreateCommentOnNonExistingComment() {
        // Arrange
        UUID parentUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        UUID authorUuid = UUID.randomUUID();
        when(postAccessor.getPost(postUuid)).thenReturn(Optional.of(mock(Post.class)));
        when(commentAccessor.getComment(parentUuid)).thenReturn(Optional.empty());

        // Act
        try {
            commentService.createComment(
                    postUuid,
                    parentUuid,
                    authorUuid,
                    "some username",
                    "some content"
            );
            fail();
        } catch (PostServiceException e) {
            Assert.assertEquals(COMMENT_NOT_FOUND, e.getExceptionType());
        }

        // Assert
        verify(commentAccessor, never()).createComment(any());
    }

    @Test
    public void shouldNotBeAbleToCreateCommentOnNonExistingPost() {
        UUID parentUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        UUID authorUuid = UUID.randomUUID();
        when(postAccessor.getPost(postUuid)).thenReturn(Optional.empty());

        // Act
        try {
            commentService.createComment(
                    postUuid,
                    parentUuid,
                    authorUuid,
                    "some username",
                    "some content"
            );
            fail();
        } catch (PostServiceException e) {
            Assert.assertEquals(POST_NOT_FOUND, e.getExceptionType());
        }

        // Assert
        verify(commentAccessor, never()).createComment(any());
    }

}