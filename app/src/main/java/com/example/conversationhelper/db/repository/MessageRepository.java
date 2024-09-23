package com.example.conversationhelper.db.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.conversationhelper.db.DBHelper;
import com.example.conversationhelper.db.model.Message;
import com.example.conversationhelper.time.TimeStampConvertor;

import java.util.ArrayList;
import java.util.List;

public class MessageRepository extends BaseRepository {
    private static MessageRepository instance;

    private MessageRepository(Context context) {
        super(context.getApplicationContext());
    }

    public static synchronized MessageRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MessageRepository(context);
        }
        return instance;
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

    public List<Message> getMessageByChatId(int chatId) {
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
}
