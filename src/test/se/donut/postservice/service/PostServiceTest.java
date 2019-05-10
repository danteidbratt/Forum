package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.PostDAO;
import se.donut.postservice.repository.postgresql.UserDAO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;

public class PostServiceTest {

    private UserDAO userDAO;
    private PostDAO postDAO;
    private ForumDAO forumAccessor;
    private PostService postService;

    @Before
    public void setup() {
        userDAO = mock(UserDAO.class);
        postDAO = mock(PostDAO.class);
        forumAccessor = mock(ForumDAO.class);
        postService = new PostService(userDAO, postDAO, forumAccessor);
    }

    @Test
    public void shouldNotBeAbleToCreatePostOnNonExistingForum() {
        // Arrange
        UUID forumUuid = UUID.randomUUID();
        doReturn(Optional.empty()).when(forumAccessor).getForum(forumUuid);

        // Act
        try {
            postService.createPost(
                    forumUuid,
                    UUID.randomUUID(),
                    "some title",
                    "some content"
            );
            fail();
        } catch (PostServiceException e) {
            assertEquals(FORUM_NOT_FOUND, e.getExceptionType());
        }

        // Assert
        verify(postDAO, never()).create(any());
    }

}