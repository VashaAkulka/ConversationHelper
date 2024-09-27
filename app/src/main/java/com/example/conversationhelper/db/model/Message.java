package com.example.conversationhelper.db.model;

import com.google.firebase.Timestamp;

public class Message {
    private String id;
    private String content;
    private String type;
    private Timestamp createTime;
    private String chatId;

    public Message(String id, String content, String type, Timestamp createTime, String chatId) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.createTime = createTime;
        this.chatId = chatId;
    }

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
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
}
