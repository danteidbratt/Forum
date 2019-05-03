package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.repository.ForumAccessor;
import se.donut.postservice.repository.PostAccessor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;

public class PostServiceTest {

    private PostAccessor postAccessor;
    private ForumAccessor forumAccessor;
    private CommentService commentService;
    private PostService postService;

    @Before
    public void setup() {
        postAccessor = mock(PostAccessor.class);
        forumAccessor = mock(ForumAccessor.class);
        commentService = mock(CommentService.class);
        postService = new PostService(postAccessor, forumAccessor, commentService);
    }

    @Test
    public void shouldNotBeAbleToCreatePostOnNonExistingForum() {
        // Arrange
        UUID forumUuid = UUID.randomUUID();
        doReturn(Optional.empty()).when(forumAccessor).get(forumUuid);

        // Act
        try {
            postService.createPost(
                    forumUuid,
                    UUID.randomUUID(),
                    "some name",
                    "some title",
                    "www.somelink.com",
                    "some contet"
            );
            fail();
        } catch (PostServiceException e) {
            assertEquals(FORUM_NOT_FOUND, e.getExceptionType());
        }

        // Assert
        verify(postAccessor, never()).create(any());
    }

}