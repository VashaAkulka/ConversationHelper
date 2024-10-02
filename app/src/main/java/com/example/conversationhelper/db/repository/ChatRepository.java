package com.example.conversationhelper.db.repository;


import com.example.conversationhelper.db.model.Chat;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        Chat chat = new Chat(chatId, difficulty, specialization, language, false, numberQuestions, createTime, userId);
        chatCollection.document(chatId).set(chat);

        return chat;
    }

    public CompletableFuture<List<Chat>> getAllChatsByUserId(String userId) {
        CompletableFuture<List<Chat>> future = new CompletableFuture<>();
        List<Chat> chatList = new ArrayList<>();

        chatCollection.get()
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

    public void updateChatStatusById(String id) {
        chatCollection.document(id).update("status", true);
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
                .whereEqualTo("status", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Object numberQuestions = document.get("numberQuestions");
                            if (numberQuestions instanceof Number) {
                                count += ((Number) numberQuestions).intValue();
                            }
                        }
                        future.complete(count);
                    }
                });
        return future;
    }
}
