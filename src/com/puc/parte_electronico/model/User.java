package com.puc.parte_electronico.model;

import android.database.Cursor;
import com.puc.parte_electronico.globals.CryptoUtilities;
import net.sqlcipher.database.SQLiteDatabase;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jose on 5/14/14.
 */
public class User {
    private int mId;
    private String mUsername;
    private String mPassword;
    private String mHashedPassword;

    public User(String username, String hashedPassword) {
        mUsername = username;
        mHashedPassword = hashedPassword;
    }

    private User(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mUsername = cursor.getString(cursor.getColumnIndex("username"));
        mHashedPassword = cursor.getString(cursor.getColumnIndex("password"));
    }

    public int getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getHashedPassword() {
        return mHashedPassword;
    }

    @Nullable
    public static User getUser(Database database, String username, String password) {
        SQLiteDatabase db = database.getDatabase();
        String hashedPassword = CryptoUtilities.hash(password);
        User user = null;

        String[] selectionArgs = new String[] { username, hashedPassword };
        Cursor cursor = db.query("user", null, "username = ? AND password = ?", selectionArgs, null, null, null, "1");
        boolean found = cursor.moveToFirst();
        if (found) {
            user = new User(cursor);
            user.setPassword(password);
        }

        cursor.close();
        return user;
    }
}
