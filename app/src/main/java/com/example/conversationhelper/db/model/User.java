package com.example.conversationhelper.db.model;

public class User {
    private String id;
    private String role;
    private String name;
    private String password;
    private String email;
    private String avatar;

    public User(String id, String role, String name, String password, String email, String avatar) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.password = password;
        this.email = email;
        this.avatar = avatar;
    }

    public User() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
