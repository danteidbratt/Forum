package se.donut.postservice.repository.firebase;

import com.google.cloud.firestore.Firestore;

abstract class FirestoreAbstractDao {

    final Firestore db;

    FirestoreAbstractDao(Firestore db) {
        this.db = db;
    }
}
