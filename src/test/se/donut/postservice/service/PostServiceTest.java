package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.PostDAO;
import se.donut.postservice.repository.postgresql.UserDAO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.INVALID_TITLE;

public class PostServiceTest {

    private UserDAO userDAO;
    private PostDAO postDAO;
    private ForumDAO forumDao;
    private PostService postService;

    @Before
    public void setup() {
        userDAO = mock(UserDAO.class);
        postDAO = mock(PostDAO.class);
        forumDao = mock(ForumDAO.class);
        postService = new PostService(userDAO, postDAO, forumDao);
    }

    @Test
    public void shouldNotBeAbleToCreatePostWithEmptyTitle() {
        String title = "";
        String content = "some content";
        UUID forumUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        try {
            postService.createPost(forumUuid, userUuid, title, content);
            fail();
        } catch (PostServiceException e) {
            assertEquals(INVALID_TITLE, e.getExceptionType());
        }
        verify(postDAO, never()).create(any());
    }

    @Test
    public void shouldNotBeAbleToCreatePostOnNonExistingForum() {
        // Arrange
        UUID forumUuid = UUID.randomUUID();
        doReturn(Optional.empty()).when(forumDao).getForum(forumUuid);

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

    @Test
    public void shouldBeAbleToCreatePost() {
        // Arrange
        String title = "some title";
        String content = "some content";
        UUID forumUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        when(forumDao.getForum(eq(forumUuid))).thenReturn(Optional.of(mock(Forum.class)));

        // Act
        postService.createPost(forumUuid, userUuid, title, content);

        // Assert
        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postDAO, times(1)).create(argumentCaptor.capture());
        Post post = argumentCaptor.getValue();

        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
        assertEquals(forumUuid, post.getForumUuid());
        assertEquals(userUuid, post.getAuthorUuid());
    }

}