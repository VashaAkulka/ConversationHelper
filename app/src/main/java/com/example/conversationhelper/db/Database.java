package com.example.conversationhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {
    private static Database instance;
    private SQLiteDatabase database;

    private Database(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public static Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context.getApplicationContext());
        }
        return instance;
    }

    public int addChat(String difficulty, String specialization, String language, int numberQuestions) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_DIFFICULTY, difficulty);
        values.put(DBHelper.KEY_SPECIALIZATION, specialization);
        values.put(DBHelper.KEY_LANGUAGE, language);
        values.put(DBHelper.KEY_NUMBER_QUESTIONS, numberQuestions);

        return (int)database.insert(DBHelper.CHATS_TABLE, null, values);
    }

    public void addMessage(String content, int chatId) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_CONTENT, content);
        values.put(DBHelper.KEY_CHAT_ID, chatId);

        database.insert(DBHelper.MESSAGES_TABLE, null, values);
    }
}
