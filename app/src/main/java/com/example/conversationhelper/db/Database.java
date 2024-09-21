package com.example.conversationhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.conversationhelper.db.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database instance;
    private final SQLiteDatabase database;

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

    public List<Chat> getAllChats() {
        List<Chat> chatList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + DBHelper.CHATS_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.KEY_ID));
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_DIFFICULTY));
                String specialization = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_SPECIALIZATION));
                String language = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_LANGUAGE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_STATUS));
                int numberQuestions = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.KEY_NUMBER_QUESTIONS));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_START_TIME));

                Chat chat = new Chat(id, difficulty, specialization, language, status, numberQuestions, startTime);

                chatList.add(chat);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return chatList;
    }

    public List<String> getMessageContentByChatId(int chatId) {
        List<String> messages = new ArrayList<>();

        String selection = DBHelper.KEY_CHAT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(chatId) };

        Cursor cursor = database.query(DBHelper.MESSAGES_TABLE, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                messages.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_CONTENT)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return messages;
    }
}
