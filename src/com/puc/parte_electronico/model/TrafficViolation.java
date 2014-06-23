package com.puc.parte_electronico.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import com.puc.parte_electronico.globals.Settings;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 5/13/14.
 */
public class TrafficViolation implements Parcelable {
    private int mId;
    private int mTicketId;
    private String mType;
    private int mCost;

    public static List<TrafficViolation> getViolationsOfTicket(Database db, int ticketId) {
        SQLiteDatabase database = db.getDatabase();
        Cursor cursor = database.rawQuery("select * from violation where ticket_id = " + ticketId, null);
        List<TrafficViolation> violations = new ArrayList<TrafficViolation>();
        while(cursor.moveToNext()) {
            TrafficViolation violation = new TrafficViolation(cursor);
            violations.add(violation);
        }
        cursor.close();
        return violations;
    }

    public TrafficViolation() {
    }

    public TrafficViolation(Parcel in) {
        mId = in.readInt();
        mTicketId = in.readInt();
        mType = in.readString();
        mCost = in.readInt();
    }

    public TrafficViolation(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mTicketId = cursor.getInt(cursor.getColumnIndex("ticket_id"));
        mType = cursor.getString(cursor.getColumnIndex("type"));
        mCost = cursor.getInt(cursor.getColumnIndex("cost"));
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getValue() {
        return mCost;
    }

    public void setValue(int value) {
        mCost = value;
    }

    public boolean isValid() {
        return mType != null && mType.length() > 0;
    }

    public void insert(int parentTicketId) {
        mTicketId = parentTicketId;
        Settings settings = Settings.getSettings();
        SQLiteDatabase database = settings.getDatabase().getDatabase();

        database.insertOrThrow("violation", null, getContentValues());
    }

    protected ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("ticket_id", mTicketId);
        cv.put("type", mType);
        return cv;
    }

    // Parcelable interface

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TrafficViolation createFromParcel(Parcel in) {
            return new TrafficViolation(in);
        }

        public TrafficViolation[] newArray(int size) {
            return new TrafficViolation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mTicketId);
        dest.writeString(mType);
        dest.writeInt(mCost);
    }
}
