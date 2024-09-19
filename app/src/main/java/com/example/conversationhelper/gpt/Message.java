package com.example.conversationhelper.gpt;

public class Message {
    String role;
    String content;

    Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
