package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import se.donut.postservice.exception.ExceptionType;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.postgresql.UserDAO;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.*;
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
    public void shouldNotBeAbleToCreateUserWithWhitespaceInUsername() {
        assertExceptionTypeForUserCreation("some username", "password", INVALID_USERNAME);
    }

    @Test
    public void shouldNotBeAbleToCreateUserWithWhitespaceInPassword() {
        assertExceptionTypeForUserCreation("username", "some password", INVALID_PASSWORD);
    }

    @Test
    public void shouldNotBeAbleToCreateUserWhenNameIsTaken() {
        // Arrange
        String name = "username";
        String password = "secret";
        User user = mock(User.class);
        doReturn(Optional.of(user)).when(userDAO).get(name);

        // Act
        try {
            userService.createUser(name, password);
            fail();
        } catch (PostServiceException e) {
            // Assert
            assertEquals(USERNAME_ALREADY_TAKEN, e.getExceptionType());
        }
    }

    @Test
    public void shouldBeAbleToCreateUser() {
        // Arrange
        String username = "username";
        String password = "secret";
        doReturn(Optional.empty()).when(userDAO).get(username);

        // Act
        userService.createUser(username, password);

        // Assert
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO, times(1)).createUserWithPassword(argumentCaptor.capture(), eq(password));
        User user = argumentCaptor.getValue();
        assertEquals(username, user.getName());
        assertEquals(USER, user.getRole());
    }

    private void assertExceptionTypeForUserCreation(String username, String password, ExceptionType expectedExceptionType) {
        try {
            userService.createUser(username, password);
            fail();
        } catch (PostServiceException e) {
            assertEquals(expectedExceptionType, e.getExceptionType());
        }
        verify(userDAO, never()).createUserWithPassword(any(),  any());
    }
}