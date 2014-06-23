package com.puc.parte_electronico.model;

import android.content.ContentValues;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jose on 6/23/14.
 */
public class Parameter {
    public static final String PARAMETER_ACCESS_TOKEN = "access_token";

    private int mId;
    private String mKey;
    private String mValue;

    @Nullable
    public static Parameter getParameter(Database database, String key) {
        SQLiteDatabase db = database.getDatabase();
        Parameter parameter = null;

        Cursor cursor = db.query("parameter", null, "key = ?", new String[] { key }, null, null, null, "1");
        if (cursor.moveToFirst()) {
            parameter = new Parameter(cursor);
        }

        cursor.close();
        return parameter;
    }

    public Parameter(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public Parameter(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mKey = cursor.getString(cursor.getColumnIndex("key"));
        mValue = cursor.getString(cursor.getColumnIndex("value"));
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("key", mKey);
        cv.put("value", mValue);
        return cv;
    }

    public void insert(Database db) {
        SQLiteDatabase database = db.getDatabase();
        database.insert("parameter", null, getContentValues());
    }

    public void update(Database db) {
        SQLiteDatabase database = db.getDatabase();
        database.update("parameter", getContentValues(), "id = " + mId, null);
    }
}
