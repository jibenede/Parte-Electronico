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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jose on 5/13/14.
 */
public class TrafficTicket implements Parcelable {
    public static final String TICKET_KEY = "TICKET_KEY";

    private static long sLastInsertTime;

    private int mId;
    private int mUserId;

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

    private String mDescription;
    private String mLocation;

    private ArrayList<TrafficViolation> mViolations;
    private ArrayList<Picture> mPictures;

    public TrafficTicket(User user) {
        mUserId = user.getId();
        mDate = new Date().getTime();

        mViolations = new ArrayList<TrafficViolation>();
        mPictures = new ArrayList<Picture>();
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

        mDescription = in.readString();
        mLocation = in.readString();

        mViolations = in.readArrayList(TrafficViolation.class.getClassLoader());
        mPictures = in.readArrayList(Picture.class.getClassLoader());
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

        mDescription = cursor.getString(cursor.getColumnIndex("description"));
        mLocation = cursor.getString(cursor.getColumnIndex("location"));
    }

    public boolean isValid() {
        // TODO: implement
        return true;
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

    public List<Picture> getPictures() {
        return mPictures;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void addPicture(Picture picture) {
        mPictures.add(picture);
    }

    public List<TrafficViolation> getViolations() {
        return mViolations;
    }

    public void addTrafficViolation(TrafficViolation violation) {
        mViolations.add(violation);
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

    public String getPrinterStringSummary() {
        // TODO: finish formatting string
        User user = User.getUser(Settings.getSettings().getDatabase(), mUserId);
        StringBuffer buffer = new StringBuffer();

        buffer.append("Datos del infractor:\n");
        buffer.append("Nombre: " + mFirstName + "\n");
        buffer.append("Appellido: " + mLastName + "\n");
        buffer.append("Cédula de Identidad: " + mRut + "\n");
        buffer.append("Domicilio: " + mAddress + "\n");
        buffer.append("\n");

        // Dummy data for time of citation
        // TODO: remove when finished.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        Date timeOfCitation = calendar.getTime();

        buffer.append(String.format("Citado a comparecer al %1$d juzgado de policía local de %2$s el día " +
                "%3$td de %3$tB a las %3$tH:%3$tM bajo apercibimiento de proceder en su rebeldía.\n",
                user.getCourthouseNumber(), user.getCourthouseCity(), timeOfCitation));
        buffer.append("\n");

        buffer.append("Infracciones:\n");
        for (TrafficViolation violation : mViolations) {
            buffer.append(violation.getType() + "\n");
        }
        buffer.append("\n");

        buffer.append("Observaciones generales:\n");
        buffer.append(mDescription + "\n\n");

        buffer.append(String.format("Cometidas en: %1$s a las %2$tH:%2$tM\n\n", mLocation, new Date(mDate)));

        buffer.append("Vehículo: " + mVehicle + "\n");
        buffer.append("Patente: " + mLicensePlate + "\n\n");

        buffer.append(String.format("Testigo: %s %s %s\n", user.getRank(), user.getFirstName(), user.getLastName()));
        buffer.append("Placa: " + user.getPlaque() + "\n\n");

        buffer.append(String.format("Fecha: %1$td de %1$tB del %1$tY\n", new Date(mDate)));

        return buffer.toString();
    }

    protected ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("user_id", mUserId);
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
        cv.put("description", mDescription);
        cv.put("location", mLocation);
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

        out.writeString(mDescription);
        out.writeString(mLocation);

        out.writeList(mViolations);
        out.writeList(mPictures);
    }

}
