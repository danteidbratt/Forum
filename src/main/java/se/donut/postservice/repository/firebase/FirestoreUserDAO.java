package se.donut.postservice.repository.firebase;

import com.google.cloud.firestore.Firestore;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.UserAccessor;

import java.util.Optional;
import java.util.UUID;

public class FirestoreUserDAO extends FirestoreAbstractDao implements UserAccessor {

    public FirestoreUserDAO(Firestore db) {
        super(db);
    }

    @Override
    public Optional<User> getUser(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUser(String name) {
        return Optional.empty();
    }

    @Override
    public void createUser(User user) {

    }
}
