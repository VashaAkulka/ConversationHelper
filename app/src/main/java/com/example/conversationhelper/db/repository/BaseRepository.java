package com.example.conversationhelper.db.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.conversationhelper.db.DBHelper;

public abstract class BaseRepository {
    protected final SQLiteDatabase database;

    protected BaseRepository(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        this.database = dbHelper.getWritableDatabase();
    }
}
