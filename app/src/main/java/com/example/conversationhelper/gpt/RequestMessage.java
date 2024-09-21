package com.example.conversationhelper.gpt;

public class RequestMessage {
    String role;
    String content;

    RequestMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
