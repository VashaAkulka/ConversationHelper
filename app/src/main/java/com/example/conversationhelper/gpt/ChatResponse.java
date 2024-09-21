package com.example.conversationhelper.gpt;

import java.util.List;

public class ChatResponse {
    List<Choice> choices;

    static class Choice {
        RequestMessage message;
    }
}
