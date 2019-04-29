package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.postgresql.PostgresUserDAO;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.NAME_ALREADY_TAKEN;
import static se.donut.postservice.model.domain.UserRole.MEMBER;


public class UserServiceTest {

    private PostgresUserDAO userDAO;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        userDAO = mock(PostgresUserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    public void shouldNotBeAbleToCreatePosterWhenNameIsTaken() {
        // Arrange
        String name = "some name";
        User user = mock(User.class);
        doReturn(Optional.of(user)).when(userDAO).getUser(name);

        // Act
        try {
            userService.createPoster(name);
            fail();
        } catch (PostServiceException e) {
            // Assert
            assertEquals(NAME_ALREADY_TAKEN, e.getExceptionType());
        }

    }

    @Test
    public void shouldBeAbleToCreatePoster() {
        // Arrange
        String name = "some name";
        doReturn(Optional.empty()).when(userDAO).getUser(name);

        // Act
        userService.createPoster(name);

        // Assert
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO, times(1)).createUser(argumentCaptor.capture());
        User user = argumentCaptor.getValue();
        assertEquals("some name", user.getName());
        assertEquals(Integer.valueOf(0), user.getCarma());
        assertEquals(false, user.getIsDeleted());
        assertEquals(MEMBER, user.getRole());
    }
}