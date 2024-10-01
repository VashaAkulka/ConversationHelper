package com.example.conversationhelper.db.model;

import com.google.firebase.Timestamp;

public class Result {
    private String id;
    private String chatId;
    private String userId;
    private Timestamp endTime;
    private int rightAnswer;

    public Result(String id, String chatId, String userId, Timestamp endTime, int rightAnswer) {
        this.id = id;
        this.chatId = chatId;
        this.userId = userId;
        this.endTime = endTime;
        this.rightAnswer = rightAnswer;
    }

    public Result() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(int rightAnswer) {
        this.rightAnswer = rightAnswer;
    }
}
