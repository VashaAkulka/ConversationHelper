package com.example.conversationhelper.db.model;

import java.io.Serializable;

public class Message implements Serializable {
    private String id;
    private String content;
    private String type;
    private String createTime;
    private Chat chat;

    public Message(String id, String content, String type, String createTime, Chat chat) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.createTime = createTime;
        this.chat = chat;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
