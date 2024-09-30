package com.example.conversationhelper.db.model;


import com.google.firebase.Timestamp;

public class Comment {
    private String id;
    private String content;
    private String userId;
    private String articleId;
    private Timestamp createTime;

    public Comment(String id, String content, String userId, String articleId, Timestamp createTime) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.articleId = articleId;
        this.createTime = createTime;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
