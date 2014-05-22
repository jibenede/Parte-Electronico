package com.puc.parte_electronico.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jose on 5/13/14.
 */
public class TrafficViolation implements Parcelable {
    private String mType;
    private int mCost;


    public TrafficViolation() {
    }

    public TrafficViolation(Parcel in) {
        mType = in.readString();
        mCost = in.readInt();
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
        dest.writeString(mType);
        dest.writeInt(mCost);
    }
}
