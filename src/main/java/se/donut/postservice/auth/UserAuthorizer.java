package se.donut.postservice.auth;

import io.dropwizard.auth.Authorizer;

public class UserAuthorizer implements Authorizer<AuthenticatedUser> {

    @Override
    public boolean authorize(AuthenticatedUser authenticatedUser, String role) {
        return role.equals(authenticatedUser.getRole());
    }

}
