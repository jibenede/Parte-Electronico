package com.puc.parte_electronico.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CursorAdapter;
import com.puc.parte_electronico.adapters.TicketListAdapter;
import com.puc.parte_electronico.globals.Settings;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.Date;

/**
 * Created by jose on 5/13/14.
 */
public class TrafficTicket implements Parcelable {
    private static long sLastInsertTime;

    private int mId;
    private int mUserId;
    private User mUser;

    private long mDate;
    private Integer mLicenseCode;
    private Double mLatitude;
    private Double mLongitude;

    private Integer mRut;
    private String mFirstName;
    private String mLastName;
    private String mAddress;
    private String mVehicle;
    private String mLicensePlate;

    public TrafficTicket(User user) {
        mUser = user;
        mUserId = mUser.getId();
        mDate = new Date().getTime();
    }

    public TrafficTicket(Parcel in) {
        boolean flag;

        mId = in.readInt();
        mUserId = in.readInt();
        mDate = in.readLong();

        flag = in.readInt() == 1;
        if (flag) {
            mLicenseCode = in.readInt();
        } else {
            mLicenseCode = null;
        }

        flag = in.readInt() == 1;
        if (flag) {
            mLatitude = in.readDouble();
            mLongitude = in.readDouble();
        } else {
            mLatitude = null;
            mLongitude = null;
        }

        flag = in.readInt() == 1;
        if (flag) {
            mRut = in.readInt();
        } else {
            mRut = null;
        }

        mFirstName = in.readString();
        mLastName = in.readString();
        mAddress = in.readString();
        mVehicle = in.readString();
        mLicensePlate = in.readString();
    }

    public TrafficTicket(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mUserId = cursor.getInt(cursor.getColumnIndex("user_id"));
        mDate = cursor.getLong(cursor.getColumnIndex("date"));
        mLicenseCode = cursor.isNull(cursor.getColumnIndex("license_code")) ? null : cursor.getInt(cursor.getColumnIndex("license_code"));
        mLatitude = cursor.isNull(cursor.getColumnIndex("latitude")) ? null : cursor.getDouble(cursor.getColumnIndex("latitude"));
        mLongitude = cursor.isNull(cursor.getColumnIndex("longitude")) ? null : cursor.getDouble(cursor.getColumnIndex("longitude"));

        mRut = cursor.isNull(cursor.getColumnIndex("rut")) ? null : cursor.getInt(cursor.getColumnIndex("rut"));
        mFirstName = cursor.getString(cursor.getColumnIndex("first_name"));
        mLastName = cursor.getString(cursor.getColumnIndex("last_name"));
        mAddress = cursor.getString(cursor.getColumnIndex("address"));
        mVehicle = cursor.getString(cursor.getColumnIndex("vehicle"));
        mLicensePlate = cursor.getString(cursor.getColumnIndex("license_plate"));
    }

    public Integer getLicenseCode() {
        return mLicenseCode;
    }

    public void setLicenseCode(int licenseCode) {
        mLicenseCode = licenseCode;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public Integer getRut() {
        return mRut;
    }

    public void setRut(Integer rut) {
        mRut = rut;
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

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getVehicle() {
        return mVehicle;
    }

    public void setVehicle(String vehicle) {
        mVehicle = vehicle;
    }

    public String getLicensePlate() {
        return mLicensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        mLicensePlate = licensePlate;
    }

    public static CursorAdapter getAdapter(Context context) {
        Settings settings = Settings.getSettings();
        SQLiteDatabase database = settings.getDatabase().getDatabase();
        Cursor cursor = database.query("ticket", null, null, null, null, null, null);
        return new TicketListAdapter(context, cursor, 0);
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
        cv.put("rut", mRut);
        cv.put("first_name", mFirstName);
        cv.put("last_name", mLastName);
        cv.put("address", mAddress);
        cv.put("vehicle", mVehicle);
        cv.put("license_plate", mLicensePlate);
        return cv;
    }

    // Parcelable interface

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TrafficTicket createFromParcel(Parcel in) {
            return new TrafficTicket(in);
        }

        public TrafficTicket[] newArray(int size) {
            return new TrafficTicket[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mId);
        out.writeInt(mUserId);
        out.writeLong(mDate);
        if (mLicenseCode != null) {
            out.writeInt(1);
            out.writeInt(mLicenseCode);
        } else {
            out.writeInt(0);
        }

        if (mLatitude != null && mLongitude != null) {
            out.writeInt(1);
            out.writeDouble(mLatitude);
            out.writeDouble(mLongitude);
        } else {
            out.writeInt(0);
        }

        if (mRut != null) {
            out.writeInt(1);
            out.writeInt(mRut);
        } else {
            out.writeInt(0);
        }
        out.writeString(mFirstName);
        out.writeString(mLastName);
        out.writeString(mAddress);
        out.writeString(mVehicle);
        out.writeString(mLicensePlate);

    }

}
