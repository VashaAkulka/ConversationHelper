package com.example.conversationhelper.db.repository;

import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Result;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ResultRepository {
    private final CollectionReference resultCollection;
    private final FirebaseFirestore db;

    public ResultRepository(FirebaseFirestore db) {
        this.db = db;
        this.resultCollection = db.collection("results");
    }

    public void addResult(String userId, String chatId, int rightAnswer, boolean success) {
        String id = resultCollection.document().getId();
        Timestamp endTime = Timestamp.now();
        resultCollection.document(id).set(new Result(id, chatId, userId, success, endTime, rightAnswer));
    }

    public void deleteResultByChatId(String id) {
        resultCollection
                .whereEqualTo("chatId", id)
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

    public CompletableFuture<List<Float>> getTimeByUserId(String id) {
        CompletableFuture<List<Float>> future = new CompletableFuture<>();

        resultCollection
                .whereEqualTo("userId", id)
                .get()
                .addOnCompleteListener(resultTask -> {
                    if (resultTask.isSuccessful()) {
                        List<Result> results = new ArrayList<>();
                        for (QueryDocumentSnapshot resultDocument : resultTask.getResult()) {
                            Result result = resultDocument.toObject(Result.class);
                            results.add(result);
                        }
                        results.sort(Comparator.comparing(Result::getEndTime));

                        List<Float> list = new ArrayList<>(Collections.nCopies(results.size(), 0f));
                        List<CompletableFuture<Void>> futures = new ArrayList<>();

                        for (int index = 0; index < results.size(); index++) {
                            Result result = results.get(index);
                            CompletableFuture<Void> chatFuture = new CompletableFuture<>();
                            futures.add(chatFuture);

                            final int currentIndex = index;

                            db.collection("chats")
                                    .whereEqualTo("id", result.getChatId())
                                    .get()
                                    .addOnCompleteListener(chatTask -> {
                                        if (chatTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot chatDocument : chatTask.getResult()) {
                                                Chat chat = chatDocument.toObject(Chat.class);
                                                long sec = result.getEndTime().getSeconds() - chat.getStartTime().getSeconds();

                                                list.set(currentIndex, sec / 3600.0f);
                                            }
                                        }
                                        chatFuture.complete(null);
                                    });
                        }

                        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                        allFutures.thenRun(() -> future.complete(list));
                    }
                });

        return future;
    }


    public CompletableFuture<Boolean> getSuccessByChatId(String id) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        resultCollection
                .whereEqualTo("chatId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Boolean success = document.getBoolean("success");
                            future.complete(success);
                        }
                    }
                });
        return future;
    }
}
