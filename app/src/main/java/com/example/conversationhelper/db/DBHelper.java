package com.example.conversationhelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "ConversationHelperDB";

    // Таблица пользователей
    public static final String USER_TABLE = "User";
    public static final String KEY_ROLE = "role";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_AVATAR = "avatar";

    // Таблица сообщений
    public static final String MESSAGES_TABLE = "Message";
    public static final String KEY_ID = "id";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CREATE_TIME = "create_time";
    public static final String KEY_CHAT_ID = "chat_id";

    // Таблица чатов
    public static final String CHATS_TABLE = "Chat";
    public static final String KEY_DIFFICULTY  = "difficulty";
    public static final String KEY_USER_ID  = "user_id";
    public static final String KEY_SPECIALIZATION  = "specialization";
    public static final String KEY_LANGUAGE  = "language";
    public static final String KEY_STATUS  = "status";
    public static final String KEY_NUMBER_QUESTIONS = "number_questions";
    public static final String KEY_START_TIME = "start_time";

    // Таблица результатов
    public static final String RESULT_TABLE = "Result";
    public static final String KEY_RIGHT_ANSWER  = "right_answer";
    public static final String KEY_COMPLETION_TIME = "completion_time";

    // Таблица статей
    public static final String ARTICLE_TABLE = "Article";
    public static final String KEY_TITLE  = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LIKE_NUMBER = "like_number";

    // Таблица комментариев
    public static final String COMMENT_TABLE = "Comment";
    public static final String KEY_ARTICLE_ID  = "article_id";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_EMAIL + " TEXT, "
                + KEY_PASSWORD + " TEXT, "
                + KEY_AVATAR + " BLOB DEFAULT NULL, "
                + KEY_ROLE + " TEXT DEFAULT 'user')";

        String CREATE_CHATS_TABLE = "CREATE TABLE " + CHATS_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DIFFICULTY + " TEXT, "
                + KEY_SPECIALIZATION + " TEXT, "
                + KEY_LANGUAGE + " TEXT, "
                + KEY_STATUS + " INTEGER DEFAULT 0, "
                + KEY_NUMBER_QUESTIONS + " INTEGER, "
                + KEY_START_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + KEY_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + USER_TABLE + "(" + KEY_ID + ")"
                + " ON DELETE CASCADE)";

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + MESSAGES_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CONTENT + " TEXT, "
                + KEY_TYPE + " TEXT, "
                + KEY_CHAT_ID + " INTEGER, "
                + KEY_CREATE_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + KEY_CHAT_ID + ") REFERENCES " + CHATS_TABLE + "(" + KEY_ID + ")"
                + " ON DELETE CASCADE)";

        String CREATE_RESULT_TABLE = "CREATE TABLE " + RESULT_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CHAT_ID + " INTEGER, "
                + KEY_RIGHT_ANSWER + " INTEGER, "
                + KEY_COMPLETION_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + KEY_CHAT_ID + ") REFERENCES " + CHATS_TABLE + "(" + KEY_ID + ")"
                + " ON DELETE CASCADE)";

        String CREATE_ARTICLE_TABLE = "CREATE TABLE " + ARTICLE_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_LIKE_NUMBER + " INTEGER DEFAULT 0)";

        String CREATE_COMMENT_TABLE = "CREATE TABLE " + COMMENT_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ARTICLE_ID + " INTEGER, "
                + KEY_USER_ID + " INTEGER, "
                + KEY_CONTENT + " TEXT, "
                + KEY_CREATE_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + KEY_ARTICLE_ID + ") REFERENCES " + ARTICLE_TABLE + "(" + KEY_ID + "), "
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + USER_TABLE + "(" + KEY_ID + ")"
                + " ON DELETE CASCADE)";

        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_CHATS_TABLE);
        sqLiteDatabase.execSQL(CREATE_MESSAGES_TABLE);
        sqLiteDatabase.execSQL(CREATE_RESULT_TABLE);
        sqLiteDatabase.execSQL(CREATE_ARTICLE_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CHATS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RESULT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ARTICLE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + COMMENT_TABLE);
        onCreate(sqLiteDatabase);
    }
}
