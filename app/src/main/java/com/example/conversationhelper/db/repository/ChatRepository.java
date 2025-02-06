package com.example.conversationhelper.db.repository;



import com.example.conversationhelper.db.model.Chat;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatRepository {
    private final CollectionReference chatCollection;
    private final MessageRepository messageRepository;
    private final ResultRepository resultRepository;


    public ChatRepository(FirebaseFirestore db) {
        this.messageRepository = new MessageRepository(db);
        this.resultRepository = new ResultRepository(db);
        this.chatCollection = db.collection("chats");
    }

    public Chat addChat(String difficulty, String specialization, String language, int numberQuestions, String userId) {
        String chatId = chatCollection.document().getId();
        Timestamp createTime = Timestamp.now();

        Chat chat = new Chat(chatId, difficulty, specialization, language, numberQuestions, createTime, userId);
        chatCollection.document(chatId).set(chat);

        return chat;
    }

    public CompletableFuture<List<Chat>> getAllChatsByUserId(String userId) {
        CompletableFuture<List<Chat>> future = new CompletableFuture<>();
        List<Chat> chatList = new ArrayList<>();

        chatCollection.orderBy("startTime").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Chat chat = document.toObject(Chat.class);
                            if (chat.getUserId().equals(userId)) {
                                chatList.add(chat);
                            }
                        }
                        future.complete(chatList);
                    }
                });

        return future;
    }


    public void deleteChatById(String id) {
        chatCollection.document(id).delete();
        messageRepository.deleteMessageByChatId(id);
        resultRepository.deleteResultByChatId(id);
    }

    public void deleteChatByUserId(String id) {
        chatCollection
                .whereEqualTo("userId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            deleteChatById(document.getId());
                        }
                    }
                });
    }

    public CompletableFuture<Integer> getCountQuestionByUserId(String id) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        chatCollection
                .whereEqualTo("userId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Chat> chats = new ArrayList<>();
                        List<CompletableFuture<Void>> futures = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Chat chat = document.toObject(Chat.class);
                            CompletableFuture<Void> chatFuture = resultRepository.getSuccessByChatId(chat.getId())
                                    .thenAccept(aBoolean -> {
                                        if (aBoolean != null) chats.add(chat);
                                    });
                            futures.add(chatFuture);
                        }

                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                            int sum = chats.stream()
                                    .mapToInt(Chat::getNumberQuestions)
                                    .sum();
                            future.complete(sum);
                        });
                    }
                });

        return future;
    }

    public CompletableFuture<Integer> getCountCompleteChat(String userId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        chatCollection
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CompletableFuture<Void>> futures = new ArrayList<>();
                        AtomicInteger complete = new AtomicInteger(0);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Chat chat = document.toObject(Chat.class);
                            CompletableFuture<Void> chatFuture = resultRepository.getSuccessByChatId(chat.getId())
                                    .thenAccept(aBoolean -> {
                                        if (aBoolean != null) complete.incrementAndGet();
                                    });
                            futures.add(chatFuture);
                        }

                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> future.complete(complete.get()));
                    }
                });

        return future;
    }
}
