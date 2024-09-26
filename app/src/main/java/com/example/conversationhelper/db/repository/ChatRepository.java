package com.example.conversationhelper.db.repository;


import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.time.TimeStampConvertor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatRepository {
    private final CollectionReference chatCollection;
    private final FirebaseFirestore db;


    public ChatRepository(FirebaseFirestore db) {
        this.db = db;
        this.chatCollection = db.collection("chats");
    }

    public Chat addChat(String difficulty, String specialization, String language, int numberQuestions, String userId) {
        String chatId = chatCollection.document().getId();
        String createTime = TimeStampConvertor.getCurrentTimestamp();

        Chat chat = new Chat(chatId, difficulty, specialization, language, 0, numberQuestions, createTime, userId);
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

        db.collection("messages")
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
}
