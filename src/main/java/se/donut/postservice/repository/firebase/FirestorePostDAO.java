package se.donut.postservice.repository.firebase;

import com.google.cloud.firestore.Firestore;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.PostAccessor;

public class FirestorePostDAO extends FirestoreAbstractDao implements PostAccessor {

    public FirestorePostDAO(Firestore db) {
        super(db);
    }

    @Override
    public void createPost(Post post) {

    }
}
