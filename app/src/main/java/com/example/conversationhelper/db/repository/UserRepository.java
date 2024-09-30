package com.example.conversationhelper.db.repository;


import com.example.conversationhelper.db.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.CompletableFuture;

public class UserRepository {
    ChatRepository chatRepository;
    private final CollectionReference usersCollection;
    private final FirebaseFirestore db;

    public UserRepository(FirebaseFirestore db) {
        this.db = db;
        chatRepository = new ChatRepository(db);
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
                        } else future.complete(null);
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

    public void deleteUserById(String id, FirebaseStorage firebaseStorage) {
        usersCollection.document(id).delete();

        db.collection("chats")
                .whereEqualTo("userId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            chatRepository.deleteChatById(document.getId());
                        }
                    }
                });

        StorageReference avatarRef = firebaseStorage.getReference().child("avatars/" + id + ".jpg");
        avatarRef.delete();
    }

    public CompletableFuture<User> getUserById(String id) {
        CompletableFuture<User> future = new CompletableFuture<>();

        usersCollection.document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        future.complete(document.toObject(User.class));
                    }
                });

        return future;
    }
}
