package se.donut.postservice.repository.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.repository.CommentAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FirestoreCommentDAO extends FirestoreAbstractDao implements CommentAccessor {

    public FirestoreCommentDAO(Firestore db) {
        super(db);
    }

    @Override
    public List<Comment> getComments(UUID forumUuid, UUID postUuid, List<UUID> parentPath) {
        List<Comment> comments = new ArrayList<>();
        try {
            DocumentReference ref = db.collection("forums")
                    .document(forumUuid.toString())
                    .collection("posts")
                    .document(postUuid.toString());

            for (UUID uuid : parentPath) {
                ref = ref.collection("comments").document(uuid.toString());
            }

            ApiFuture<QuerySnapshot> query = db.collection("comments").get();
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            for (QueryDocumentSnapshot document : documents) {

                Comment comment = new Comment(
                        parseToUuid(document.getString("uuid")),
                        parseToUuid(document.getString("author_uuid")),
                        document.getString("author_uuid"),
                        document.getString("content"),
                        parseToInt(document.getString("score")),
                        document.getTimestamp("created_at").toSqlTimestamp().toInstant(),
                        document.getBoolean("is_deleted"),
                        parseToUuid(document.getString("parent_uuid")),
                        parseToUuid(document.getString("post_uuid"))
                );
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return comments;
    }

    @Override
    public void createComment(Comment comment) {

    }

    private UUID parseToUuid(String s) {
        return UUID.fromString(s);
    }

    private int parseToInt(String s) {
        return Integer.parseInt(s);
    }

}
