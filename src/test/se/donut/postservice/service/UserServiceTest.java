package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.postgresql.UserDAO;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.USERNAME_ALREADY_EXISTS;
import static se.donut.postservice.model.domain.Role.USER;


public class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    public void shouldNotBeAbleToCreateUserWhenNameIsTaken() {
        // Arrange
        String name = "some username";
        String password = "secret";
        User user = mock(User.class);
        doReturn(Optional.of(user)).when(userDAO).get(name);

        // Act
        try {
            userService.createUser(name, password);
            fail();
        } catch (PostServiceException e) {
            // Assert
            assertEquals(USERNAME_ALREADY_EXISTS, e.getExceptionType());
        }
    }

    @Test
    public void shouldBeAbleToCreateUser() {
        // Arrange
        String name = "some username";
        String password = "secret";
        doReturn(Optional.empty()).when(userDAO).get(name);

        // Act
        userService.createUser(name, password);

        // Assert
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO, times(1)).createUser(argumentCaptor.capture());
        User user = argumentCaptor.getValue();
        assertEquals("some username", user.getName());
        assertEquals(Integer.valueOf(0), user.getCarma());
        assertEquals(USER, user.getRole());
    }
}