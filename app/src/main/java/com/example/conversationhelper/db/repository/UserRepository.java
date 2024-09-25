package com.example.conversationhelper.db.repository;


import com.example.conversationhelper.db.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.CompletableFuture;

public class UserRepository {
    private final CollectionReference usersCollection;

    public UserRepository(FirebaseFirestore db) {
        this.usersCollection = db.collection("users");
    }

    public User addUser(String name, String email, String password) {
        String userId = usersCollection.document().getId();
        User user = new User(userId, "user", name, password, email, null);
        usersCollection.document(userId).set(user);

        return user;
    }

    public CompletableFuture<User> getUserByName(String name) {
        CompletableFuture<User> future = new CompletableFuture<>();

        usersCollection.whereEqualTo("name", name).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                future.complete(user);
                            }
                        } else future.complete(null);;
                    }
                });

        return future;
    }

    public CompletableFuture<Boolean> updateUser(User user) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        usersCollection.whereEqualTo("name", user.getName()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) future.complete(true);
                        else future.complete(false);

                        usersCollection.document(user.getId())
                                .set(user);
                    }
                });

        return future;
    }

    public void deleteUserById(String id) {
        usersCollection.document(id).delete();
    }
}
