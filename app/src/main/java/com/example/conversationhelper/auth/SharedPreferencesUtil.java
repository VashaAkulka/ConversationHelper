package com.example.conversationhelper.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.conversationhelper.db.model.User;
import com.google.gson.Gson;

public class SharedPreferencesUtil {
    private static final String PREFS_NAME = "my_prefs";
    private static final String KEY_USER = "auth_user";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveUser(User user) {
        String json = gson.toJson(user);
        sharedPreferences.edit().putString(KEY_USER, json).apply();
    }

    public User loadUser() {
        String json = sharedPreferences.getString(KEY_USER, null);
        return gson.fromJson(json, User.class);
    }

    public void deleteUser() {
        sharedPreferences.edit().remove(KEY_USER).apply();
    }
}