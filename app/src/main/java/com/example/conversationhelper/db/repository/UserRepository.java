package com.example.conversationhelper.db.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.conversationhelper.db.DBHelper;
import com.example.conversationhelper.db.model.User;

public class UserRepository extends BaseRepository {
    private static UserRepository instance;

    private UserRepository(Context context) {
        super(context.getApplicationContext());
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
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
