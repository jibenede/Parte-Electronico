package com.puc.parte_electronico.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jose on 5/20/14.
 */
public class Picture implements Parcelable {
    public static final int PICTURE_TYPE_BACKGROUND = 0;
    public static final int PICTURE_TYPE_EVIDENCE = 1;

    private int mId;
    private int mTicketId;
    private TrafficTicket mTicket;
    private String mPath;
    private int mType;

    private Bitmap mBitmap;

    public Picture(TrafficTicket ticket, String path, int type) {
        mTicket = ticket;
        mPath = path;
        mType = type;
    }

    public Picture(Parcel in) {
        mTicket = in.readParcelable(TrafficTicket.class.getClassLoader());
        mPath = in.readString();
        mType = in.readInt();
    }

    public String getPath() {
        return mPath;
    }

    public int getType() {
        return mType;
    }

    public Bitmap getBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeFile(mPath);
        }
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
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
        dest.writeParcelable(mTicket, 0);
        dest.writeString(mPath);
        dest.writeInt(mType);
    }
}
