package com.example.conversationhelper.db.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.conversationhelper.db.DBHelper;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.time.TimeStampConvertor;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository extends BaseRepository {
    private static ChatRepository instance;

    private ChatRepository(Context context) {
        super(context.getApplicationContext());
    }

    public static synchronized ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context);
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

    public void deleteChatById(int id) {
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        database.delete(DBHelper.CHATS_TABLE, whereClause, whereArgs);
    }
}
