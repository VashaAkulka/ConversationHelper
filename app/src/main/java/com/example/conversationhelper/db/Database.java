package com.example.conversationhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Message;
import com.example.conversationhelper.db.model.User;
import com.example.conversationhelper.time.TimeStampConvertor;

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

    public Chat addChat(String difficulty, String specialization, String language, int numberQuestions, int userId) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_DIFFICULTY, difficulty);
        values.put(DBHelper.KEY_SPECIALIZATION, specialization);
        values.put(DBHelper.KEY_LANGUAGE, language);
        values.put(DBHelper.KEY_NUMBER_QUESTIONS, numberQuestions);
        values.put(DBHelper.KEY_USER_ID, userId);

        String createTime = TimeStampConvertor.getCurrentTimestamp();
        int id = (int)database.insert(DBHelper.CHATS_TABLE, null, values);
        return new Chat(id, difficulty, specialization, language, 0, numberQuestions, createTime);
    }

    public Message addMessage(String content, int chatId, String type) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_CONTENT, content);
        values.put(DBHelper.KEY_CHAT_ID, chatId);
        values.put(DBHelper.KEY_TYPE, type);

        database.insert(DBHelper.MESSAGES_TABLE, null, values);
        String createTime = TimeStampConvertor.getCurrentTimestamp();
        return new Message(content, type, createTime);
    }

    public List<Chat> getAllChatsByUserId(int userId) {
        List<Chat> chatList = new ArrayList<>();

        String selection = DBHelper.KEY_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = database.query(DBHelper.CHATS_TABLE, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.KEY_ID));
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_DIFFICULTY));
                String specialization = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_SPECIALIZATION));
                String language = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_LANGUAGE));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.KEY_STATUS));
                int numberQuestions = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.KEY_NUMBER_QUESTIONS));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_START_TIME));

                Chat chat = new Chat(id, difficulty, specialization, language, status, numberQuestions, startTime);

                chatList.add(chat);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return chatList;
    }

    public List<Message> getMessageContentByChatId(int chatId) {
        List<Message> messages = new ArrayList<>();

        String selection = DBHelper.KEY_CHAT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(chatId) };

        Cursor cursor = database.query(DBHelper.MESSAGES_TABLE, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_CONTENT));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_TYPE));
                String createTime = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_CREATE_TIME));

                Message message = new Message(content, type, createTime);
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return messages;
    }

    public void deleteChatById(int id) {
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        database.delete(DBHelper.CHATS_TABLE, whereClause, whereArgs);
    }

    public User addUser(String name, String email, String password) {
        ContentValues values = new ContentValues();

        values.put(DBHelper.KEY_NAME, name);
        values.put(DBHelper.KEY_EMAIL, email);
        values.put(DBHelper.KEY_PASSWORD, password);

        int id = (int)database.insert(DBHelper.USER_TABLE, null, values);
        return new User(id, "user", name, password, email, "no");
    }

    public User getUserByName(String name) {

        String selection = DBHelper.KEY_NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = database.query(DBHelper.USER_TABLE, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_AVATAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_ROLE))
                );
                cursor.close();
                return user;
            }
            cursor.close();
        }

        return null;
    }

}
