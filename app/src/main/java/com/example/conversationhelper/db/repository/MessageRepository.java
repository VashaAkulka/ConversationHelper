package com.example.conversationhelper.db.repository;

import com.example.conversationhelper.db.model.Message;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageRepository {
    private final CollectionReference messageCollection;

    public MessageRepository(FirebaseFirestore db) {
        this.messageCollection = db.collection("messages");
    }

    public Message addMessage(String content, String chatId, String type) {
        String messageId = messageCollection.document().getId();
        Timestamp createTime = Timestamp.now();

        Message message = new Message(messageId, content, type, createTime, chatId);
        messageCollection.document(messageId).set(message);

        return message;
    }

    public CompletableFuture<List<Message>> getMessageByChatId(String chatId) {
        CompletableFuture<List<Message>> future = new CompletableFuture<>();
        List<Message> messageList = new ArrayList<>();

        messageCollection.orderBy("createTime").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            if (message.getChatId().equals(chatId)) {
                                messageList.add(message);
                            }
                        }
                        future.complete(messageList);
                    }
                });

        return future;
    }

    public void deleteMessageByChatId(String id) {
        messageCollection
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
