package com.example.conversationhelper.auth;

import com.example.conversationhelper.db.model.User;

public class Authentication {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Authentication.user = user;
    }
}
