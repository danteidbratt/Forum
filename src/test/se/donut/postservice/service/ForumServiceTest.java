package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Subscription;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.UserDAO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.*;

public class ForumServiceTest {

    private UserDAO userDAO;
    private ForumDAO forumDAO;
    private ForumService forumService;

    @Before
    public void setup() {
        userDAO = mock(UserDAO.class);
        forumDAO = mock(ForumDAO.class);
        forumService = new ForumService(userDAO, forumDAO);
    }

    @Test
    public void shouldNotBeAbleToCreateForumWithDuplicateName() {
        // Arrange
        String forumName = "worldNews";
        when(forumDAO.getForumByName(forumName)).thenReturn(Optional.of(mock(Forum.class)));

        // Act
        try {
            forumService.createForum(UUID.randomUUID(), "some name", forumName, "some description");
            fail();
        } catch (PostServiceException e) {
            assertEquals(FORUM_NAME_ALREADY_TAKEN, e.getExceptionType());
        }

        // Assert
        verify(forumDAO, never()).createForum(any());
    }

    @Test
    public void shouldNotBeAbleToCreateForumWithNonAlphanumericCharacterInName() {
        // Arrange
        String forumName = "world news 2";
        when(forumDAO.getForumByName(forumName)).thenReturn(Optional.of(mock(Forum.class)));

        // Act
        try {
            forumService.createForum(UUID.randomUUID(), "some name", forumName, "some description");
            fail();
        } catch (PostServiceException e) {
            assertEquals(INVALID_FORUM_NAME, e.getExceptionType());
        }

        // Assert
        verify(forumDAO, never()).createForum(any());
    }

    @Test
    public void authorShouldBeSubscribedToForumAfterCreation() {
        // Arrange
        UUID userUuid = UUID.randomUUID();

        // Act
        forumService.createForum(userUuid, "some name", "worldNews1", "some description");

        // Assert
        ArgumentCaptor<Forum> forumArgumentCaptor = ArgumentCaptor.forClass(Forum.class);
        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);

        verify(forumDAO, times(1)).createForum(forumArgumentCaptor.capture());
        verify(forumDAO, times(1)).subscribe(subscriptionArgumentCaptor.capture());

        Forum forum = forumArgumentCaptor.getValue();
        Subscription subscription = subscriptionArgumentCaptor.getValue();

        assertEquals(userUuid, forum.getAuthorUuid());
        assertEquals(forum.getUuid(), subscription.getForumUuid());
        assertEquals(userUuid, subscription.getUserUuid());
    }

    @Test
    public void shouldNotBeAbleToSubscribeToNonExistingForum() {
        // Arrange
        UUID forumUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        when(forumDAO.getForum(forumUuid)).thenReturn(Optional.empty());

        // Act
        try {
            forumService.subscribe(userUuid, forumUuid);
            fail();
        } catch (PostServiceException e) {
            assertEquals(FORUM_NOT_FOUND, e.getExceptionType());
        }

        // Assert
        verify(forumDAO, never()).createForum(any());
    }
}
