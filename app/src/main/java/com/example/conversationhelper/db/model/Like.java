package com.example.conversationhelper.db.model;

public class Like {
    private String id;
    private String userId;
    private String articleId;

    public Like(String id, String userId, String articleId) {
        this.id = id;
        this.userId = userId;
        this.articleId = articleId;
    }

    public Like() {
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
}
