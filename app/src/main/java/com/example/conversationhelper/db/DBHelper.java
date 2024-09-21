package com.example.conversationhelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "ConversationHelperDB";

    // Таблица сообщений
    public static final String MESSAGES_TABLE = "MessagesData";
    public static final String KEY_ID = "id";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_CHAT_ID = "chat_id";

    // Таблица чатов
    public static final String CHATS_TABLE = "ChatsData";
    public static final String KEY_DIFFICULTY  = "difficulty";
    public static final String KEY_SPECIALIZATION  = "specialization";
    public static final String KEY_LANGUAGE  = "language";
    public static final String KEY_STATUS  = "status";
    public static final String KEY_NUMBER_QUESTIONS = "number_questions";
    public static final String KEY_START_TIME = "start_time";

    // Таблица результатов
    public static final String RESULT_TABLE = "ResultData";
    public static final String KEY_RIGHT_ANSWER  = "right_answer";
    public static final String KEY_COMPLETION_TIME = "completion_time";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CHATS_TABLE = "CREATE TABLE " + CHATS_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DIFFICULTY + " TEXT, "
                + KEY_SPECIALIZATION + " TEXT, "
                + KEY_LANGUAGE + " TEXT, "
                + KEY_STATUS + " TEXT DEFAULT 'process', "
                + KEY_NUMBER_QUESTIONS + " INTEGER, "
                + KEY_START_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + MESSAGES_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CONTENT + " TEXT, "
                + KEY_CHAT_ID + " INTEGER, "
                + "FOREIGN KEY(" + KEY_CHAT_ID + ") REFERENCES " + CHATS_TABLE + "(" + KEY_ID + ")"
                + " ON DELETE CASCADE)";

        String CREATE_RESULT_TABLE = "CREATE TABLE " + RESULT_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CHAT_ID + " INTEGER, "
                + KEY_RIGHT_ANSWER + " INTEGER, "
                + KEY_COMPLETION_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + KEY_CHAT_ID + ") REFERENCES " + CHATS_TABLE + "(" + KEY_ID + ")"
                + " ON DELETE CASCADE)";

        sqLiteDatabase.execSQL(CREATE_CHATS_TABLE);
        sqLiteDatabase.execSQL(CREATE_MESSAGES_TABLE);
        sqLiteDatabase.execSQL(CREATE_RESULT_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + MESSAGES_TABLE);
        sqLiteDatabase.execSQL("drop table if exists " + CHATS_TABLE);
        sqLiteDatabase.execSQL("drop table if exists " + RESULT_TABLE);
        onCreate(sqLiteDatabase);
    }
}
