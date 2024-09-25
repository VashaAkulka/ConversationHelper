package com.example.conversationhelper.db.model;

import java.io.Serializable;

public class Chat implements Serializable {
    private String id;
    private String difficulty;
    private String specialization;
    private String language;
    private int status;
    private int numberQuestions;
    private String startTime;
    private String userId;

    public Chat(String id, String difficulty, String specialization, String language, int status, int numberQuestions, String startTime, String userId) {
        this.id = id;
        this.difficulty = difficulty;
        this.specialization = specialization;
        this.language = language;
        this.status = status;
        this.numberQuestions = numberQuestions;
        this.startTime = startTime;
        this.userId = userId;
    }

    public Chat() {
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNumberQuestions() {
        return numberQuestions;
    }

    public void setNumberQuestions(int numberQuestions) {
        this.numberQuestions = numberQuestions;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
}
