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
        return new User(id, "user", name, password, email, null);
    }

    public User getUserByName(String name) {

        String selection = DBHelper.KEY_NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = database.query(DBHelper.USER_TABLE, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.KEY_EMAIL)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.KEY_AVATAR))
                );
                cursor.close();
                return user;
            }
            cursor.close();
        }

        return null;
    }

    public boolean updateUser(User user) {
        if (getUserByName(user.getName()) == null) {
            ContentValues values = new ContentValues();

            values.put(DBHelper.KEY_NAME, user.getName());
            values.put(DBHelper.KEY_EMAIL, user.getEmail());
            values.put(DBHelper.KEY_PASSWORD, user.getPassword());
            values.put(DBHelper.KEY_AVATAR, user.getAvatar());
            values.put(DBHelper.KEY_ROLE, user.getRole());

            database.update(DBHelper.USER_TABLE, values, "id = ?", new String[]{String.valueOf(user.getId())});
            return true;
        }
        return false;
    }

    public void deleteUserById(int id) {
        database.delete(DBHelper.USER_TABLE, "id = ?", new String[]{String.valueOf(id)});
    }
}
