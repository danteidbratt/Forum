package se.donut.postservice.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.UserAccessor;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static se.donut.postservice.exception.ExceptionType.USERNAME_ALREADY_EXISTS;
import static se.donut.postservice.model.domain.Role.USER;


public class UserServiceTest {

    private UserAccessor userAccessor;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        userAccessor = mock(UserAccessor.class);
        userService = new UserService(userAccessor);
    }

    @Test
    public void shouldNotBeAbleToCreateUserWhenNameIsTaken() {
        // Arrange
        String name = "some username";
        String password = "secret";
        User user = mock(User.class);
        doReturn(Optional.of(user)).when(userAccessor).getUser(name);

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
        doReturn(Optional.empty()).when(userAccessor).getUser(name);

        // Act
        userService.createUser(name, password);

        // Assert
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userAccessor, times(1)).createUser(argumentCaptor.capture(), eq(password));
        User user = argumentCaptor.getValue();
        assertEquals("some username", user.getName());
        assertEquals(Integer.valueOf(0), user.getCarma());
        assertEquals(USER, user.getRole());
    }
}