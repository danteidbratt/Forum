package se.donut.postservice.repository.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.repository.ForumAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreForumDAO extends FirestoreAbstractDao implements ForumAccessor {

    public FirestoreForumDAO(Firestore db) {
        super(db);
    }

    @Override
    public void createForum(Forum forum) {
        try {
            DocumentReference docRef = db.collection("users").document("alovelace");
            Map<String, Object> data = new HashMap<>();
            data.put("first", "Ada");
            data.put("last", "Lovelace");
            data.put("born", 1815);
            ApiFuture<WriteResult> result = docRef.set(data);
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
