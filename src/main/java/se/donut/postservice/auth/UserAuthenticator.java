package se.donut.postservice.auth;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import lombok.extern.slf4j.Slf4j;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.model.domain.VaultEntry;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.repository.postgresql.VaultDAO;
import se.donut.postservice.util.AppSecurity;

import java.util.Optional;

@Slf4j
public class UserAuthenticator implements Authenticator<BasicCredentials, AuthenticatedUser> {

    private final UserDAO userDAO;
    private final VaultDAO vaultDAO;

    public UserAuthenticator(UserDAO userDAO, VaultDAO vaultDAO) {
        this.userDAO = userDAO;
        this.vaultDAO = vaultDAO;
    }

    @Override
    public Optional<AuthenticatedUser> authenticate(BasicCredentials credentials) {
        User user = userDAO.get(credentials.getUsername()).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        VaultEntry vaultEntry = vaultDAO.getByUserUuid(user.getUuid());
        String passwordHash = AppSecurity.encryptPassword(credentials.getPassword(), vaultEntry.getSalt());
        if (vaultEntry.getPasswordHash().equals(passwordHash)) {
            log.info(String.format("User '%s' was successfully authenticated", credentials.getUsername()));
            return Optional.of(new AuthenticatedUser(
                    user.getUuid(),
                    user.getName(),
                    user.getRole().toString()
            ));
        }
        log.info(String.format("User '%s' failed to authenticate", credentials.getUsername()));
        return Optional.empty();
    }

}
