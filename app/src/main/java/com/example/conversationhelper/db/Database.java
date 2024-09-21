package com.example.conversationhelper.db;

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
}
