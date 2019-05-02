package se.donut.postservice.service;

import se.donut.postservice.repository.VaultAccessor;

import java.util.Optional;
import java.util.UUID;

public class AuthService {

    private final VaultAccessor vaultAccessor;

    public AuthService(VaultAccessor vaultAccessor) {
        this.vaultAccessor = vaultAccessor;
    }

    public Optional<UUID> attemptLogin(String username, String password) {
        return vaultAccessor.authenticate(username, password);
    }

}
