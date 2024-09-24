package com.example.conversationhelper.db.model;

import java.io.Serializable;

public class Message implements Serializable {
    private String content;
    private String type;
    private String createTime;

    public Message(String content, String type, String createTime) {
        this.content = content;
        this.type = type;
        this.createTime = createTime;
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
}
