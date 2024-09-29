package com.example.conversationhelper.db.repository;

import com.example.conversationhelper.db.model.Like;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.CompletableFuture;

public class LikeRepository {
    private final CollectionReference likeCollection;

    public LikeRepository(FirebaseFirestore db) {
        likeCollection = db.collection("likes");
    }

    public void addLike(String userId, String articleId) {
        String id = likeCollection.document().getId();
        likeCollection.document(id).set(new Like(id, userId, articleId));
    }

    public void deleteLike(String userId, String articleId) {
        likeCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("articleId", articleId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }
                });
    }


    public CompletableFuture<Integer> getCountLikeByArticleId(String articleId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        likeCollection.whereEqualTo("articleId",  articleId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(task.getResult().size());
                    }
                });

        return future;
    }

    public CompletableFuture<Boolean> getIfUserLikedArticle(String userId, String articleId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        likeCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("articleId", articleId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            future.complete(true);
                        } else {
                            future.complete(false);
                        }
                    }
                });

        return future;
    }

}
