package se.donut.postservice.auth;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.service.UserService;

import java.util.Optional;

public class UserAuthenticator implements Authenticator<BasicCredentials, AuthenticatedUser> {

    private final UserService userService;

    public UserAuthenticator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<AuthenticatedUser> authenticate(BasicCredentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        UserDTO user = userService.login(username, password);

        if (user != null) {
            return Optional.of(new AuthenticatedUser(
                    user.getUuid(),
                    username,
                    user.getRole().toString()
            ));
        }
        return Optional.empty();
    }
}
