package com.example.conversationhelper.db.model;

public class Chat {
    private int id;
    private String difficulty;
    private String specialization;
    private String language;
    private String status;
    private int numberQuestions;
    private String startTime;

    public Chat(int id, String difficulty, String specialization, String language, String status, int numberQuestions, String startTime) {
        this.id = id;
        this.difficulty = difficulty;
        this.specialization = specialization;
        this.language = language;
        this.status = status;
        this.numberQuestions = numberQuestions;
        this.startTime = startTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
