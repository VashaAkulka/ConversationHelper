package com.example.conversationhelper.gpt;

import java.util.List;

public class ChatRequest {
    String model;
    List<RequestMessage> messages;

    ChatRequest(String model, List<RequestMessage> messages) {
        this.model = model;
        this.messages = messages;
    }
}
