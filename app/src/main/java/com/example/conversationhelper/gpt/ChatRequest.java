package com.example.conversationhelper.gpt;

import java.util.List;

public class ChatRequest {
    String model;
    List<Message> messages;

    ChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }
}
