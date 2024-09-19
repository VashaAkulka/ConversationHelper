package com.example.conversationhelper.gpt;

public interface ChatGptCallback {
    void onSuccess(String result);
    void onError(Exception e);
}
