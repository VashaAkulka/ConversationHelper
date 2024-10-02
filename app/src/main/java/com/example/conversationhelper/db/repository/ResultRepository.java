package com.example.conversationhelper.db.repository;

import com.example.conversationhelper.db.model.Result;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.CompletableFuture;

public class ResultRepository {
    private final CollectionReference resultCollection;

    public ResultRepository(FirebaseFirestore db) {
        resultCollection = db.collection("results");
    }

    public void addResult(String userId, String chatId, int rightAnswer) {
        String id = resultCollection.document().getId();
        Timestamp endTime = Timestamp.now();
        resultCollection.document(id).set(new Result(id, chatId, userId, endTime, rightAnswer));
    }

    public void deleteResultByChatId(String id) {
        resultCollection
                .whereEqualTo("chadId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }
                });
    }

    public CompletableFuture<Integer> getCountRightAnswerByUserId(String id) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        resultCollection
                .whereEqualTo("userId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Object rightAnswerValue = document.get("rightAnswer");
                            if (rightAnswerValue instanceof Number) {
                                count += ((Number) rightAnswerValue).intValue();
                            }
                        }
                        future.complete(count);
                    }
                });
        return future;
    }
}
