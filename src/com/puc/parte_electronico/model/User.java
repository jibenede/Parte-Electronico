package com.puc.parte_electronico.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.puc.parte_electronico.globals.CryptoUtilities;
import net.sqlcipher.database.SQLiteDatabase;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jose on 5/14/14.
 */
@JsonIgnoreProperties({"id", "created_at", "updated_at"})
public class User {
    private int mId;
    @JsonProperty("username")
    private String mUsername;
    private String mPassword;
    @JsonProperty("password_digest")
    private String mHashedPassword;

    @JsonProperty("first_name")
    private String mFirstName;
    @JsonProperty("last_name")
    private String mLastName;
    @JsonProperty("precint")
    private String mPrecinct;
    @JsonProperty("courthouse_number")
    private int mCourthouseNumber;
    @JsonProperty("courthouse_city")
    private String mCourthouseCity;
    @JsonProperty("plaque")
    private String mPlaque;
    @JsonProperty("rank")
    private String mRank;

    public User() {}

    public User(String username, String hashedPassword) {
        mUsername = username;
        mHashedPassword = hashedPassword;
    }

    private User(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mUsername = cursor.getString(cursor.getColumnIndex("username"));
        mHashedPassword = cursor.getString(cursor.getColumnIndex("password"));

        mFirstName = cursor.getString(cursor.getColumnIndex("first_name"));
        mLastName = cursor.getString(cursor.getColumnIndex("last_name"));
        mPrecinct = cursor.getString(cursor.getColumnIndex("precinct"));
        mCourthouseNumber = cursor.getInt(cursor.getColumnIndex("courthouse_number"));
        mCourthouseCity = cursor.getString(cursor.getColumnIndex("courthouse_city"));
        mPlaque = cursor.getString(cursor.getColumnIndex("plaque"));
        mRank = cursor.getString(cursor.getColumnIndex("rank"));
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("username", mUsername);
        cv.put("password", mHashedPassword);
        cv.put("first_name", mFirstName);
        cv.put("last_name", mLastName);
        cv.put("precinct", mPrecinct);
        cv.put("courthouse_number", mCourthouseNumber);
        cv.put("courthouse_city", mCourthouseCity);
        cv.put("plaque", mPlaque);
        cv.put("rank", mRank);
        return cv;
    }

    public int getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
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

    public void setHashedPassword(String hashedPassword) {
        mHashedPassword = hashedPassword;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getPrecinct() {
        return mPrecinct;
    }

    public void setPrecinct(String precinct) {
        mPrecinct = precinct;
    }

    public int getCourthouseNumber() {
        return mCourthouseNumber;
    }

    public void setCourthouseNumber(int courthouseNumber) {
        mCourthouseNumber = courthouseNumber;
    }

    public String getCourthouseCity() {
        return mCourthouseCity;
    }

    public void setCourthouseCity(String courthouseCity) {
        mCourthouseCity = courthouseCity;
    }

    public String getPlaque() {
        return mPlaque;
    }

    public void setPlaque(String plaque) {
        mPlaque = plaque;
    }

    public String getRank() {
        return mRank;
    }

    public void setRank(String rank) {
        mRank = rank;
    }

    public void insert(Database database) {
        SQLiteDatabase db = database.getDatabase();
        db.insert("user", null, getContentValues());
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

    @Nullable
    public static User getUser(Database database, int id) {
        SQLiteDatabase db = database.getDatabase();
        User user = null;

        Cursor cursor = db.query("user", null, "_id = " + id, null, null, null, null, "1");
        boolean found = cursor.moveToFirst();
        if (found) {
            user = new User(cursor);
        }

        cursor.close();
        return user;
    }

    public static void deleteUsers(Database database) {
        SQLiteDatabase db = database.getDatabase();
        db.delete("user", null, null);
    }
}
