package com.puc.parte_electronico.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.puc.parte_electronico.globals.Settings;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 5/20/14.
 */
public class Picture implements Parcelable {
    public static final int PICTURE_TYPE_BACKGROUND = 0;
    public static final int PICTURE_TYPE_EVIDENCE = 1;

    private int mId;
    private int mTicketId;
    @JsonIgnore
    private String mPath;
    private int mType;

    @JsonIgnore
    private Bitmap mBitmap;

    public static List<Picture> getPicturesOfTicket(Database db, int ticketId) {
        SQLiteDatabase database = db.getDatabase();
        Cursor cursor = database.rawQuery("select * from picture where ticket_id = " + ticketId, null);
        List<Picture> pictures = new ArrayList<Picture>();
        while(cursor.moveToNext()) {
            Picture picture = new Picture(cursor);
            pictures.add(picture);
        }
        cursor.close();
        return pictures;
    }

    public Picture(TrafficTicket ticket, String path, int type) {
        mPath = path;
        mType = type;
    }

    public Picture(Parcel in) {
        mId = in.readInt();
        mTicketId = in.readInt();
        mPath = in.readString();
        mType = in.readInt();
    }

    public Picture(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mTicketId = cursor.getInt(cursor.getColumnIndex("ticket_id"));
        mPath = cursor.getString(cursor.getColumnIndex("path"));
        cursor.getString(cursor.getColumnIndex("type"));
    }

    public String getFileName() {
        return mPath.substring(mPath.lastIndexOf("/") + 1);
    }

    @JsonIgnore
    public String getPath() {
        return mPath;
    }

    public int getType() {
        return mType;
    }

    @JsonIgnore
    public Bitmap getBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeFile(mPath);
        }
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void insert(int parentTicketId) {
        mTicketId = parentTicketId;
        Settings settings = Settings.getSettings();
        SQLiteDatabase database = settings.getDatabase().getDatabase();

        database.insertOrThrow("picture", null, getContentValues());
    }

    protected ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("ticket_id", mTicketId);
        cv.put("path", mPath);
        cv.put("type", mType);
        return cv;
    }

    // Parcelable interface

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
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
        dest.writeString(mPath);
        dest.writeInt(mType);
    }
}
