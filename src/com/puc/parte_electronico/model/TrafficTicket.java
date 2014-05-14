package com.puc.parte_electronico.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import com.puc.parte_electronico.TestAdapter;
import com.puc.parte_electronico.globals.Settings;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.Date;

/**
 * Created by jose on 5/13/14.
 */
public class TrafficTicket {
    private static long sLastInsertTime;

    private int mId;
    private int mUserId;
    private User mUser;

    private long mDate;
    private Integer mLicenseCode;
    private Double mLatitude;
    private Double mLongitude;

    public TrafficTicket(User user, Integer licenseCode, long date, Double latitude, Double longitude) {
        mUser = user;
        mUserId = mUser.getId();
        mLicenseCode = licenseCode;
        mDate = date;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public TrafficTicket(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mUserId = cursor.getInt(cursor.getColumnIndex("user_id"));
        mDate = cursor.getLong(cursor.getColumnIndex("date"));
        mLicenseCode = cursor.isNull(cursor.getColumnIndex("license_code")) ? null : cursor.getInt(cursor.getColumnIndex("license_code"));
        mLatitude = cursor.isNull(cursor.getColumnIndex("latitude")) ? null : cursor.getDouble(cursor.getColumnIndex("latitude"));
        mLongitude = cursor.isNull(cursor.getColumnIndex("longitude")) ? null : cursor.getDouble(cursor.getColumnIndex("longitude"));
    }

    public static CursorAdapter getAdapter(Context context) {
        Settings settings = Settings.getSettings();
        SQLiteDatabase database = settings.getDatabase().getDatabase();
        Cursor cursor = database.query("ticket", null, null, null, null, null, null);
        return new TestAdapter(context, cursor, 0);
    }

    public static long getTimeOfLastInsert() {
        return sLastInsertTime;
    }

    public int getId() {
        return mId;
    }

    public Date getDate() {
        return new Date(mDate);
    }

    public void insert() {
        Settings settings = Settings.getSettings();
        SQLiteDatabase database = settings.getDatabase().getDatabase();
        database.insertOrThrow("ticket", null, getContentValues());

        sLastInsertTime = System.currentTimeMillis();
    }

    protected ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("user_id", mUser.getId());
        cv.put("date", mDate);
        cv.put("license_code", mLicenseCode);
        cv.put("latitude", mLatitude);
        cv.put("longitude", mLongitude);
        return cv;
    }


}
