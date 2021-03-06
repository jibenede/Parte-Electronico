package com.puc.parte_electronico.model;

import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.io.File;

/**
 * Created by jose on 4/30/14.
 */
public class Database {
    private static final String DATABASE_CONFIG = "DatabaseConfiguration.config";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ParteElectronico.sql";

    private SQLiteDatabase mDatabase;

    public Database(Context context, String password) throws WrongEncryptionPasswordException {
        try {
            SQLiteDatabase.loadLibs(context);
            File databaseFile = context.getDatabasePath(DATABASE_NAME);
            boolean databaseCreated = databaseFile.exists();

            if (!databaseCreated) {
                databaseFile.getParentFile().mkdirs();
                mDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFile, password, null);
                mDatabase.setVersion(DATABASE_VERSION);
                onCreate();
            } else {
                mDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFile, password, null);
            }

            int databaseVersion = mDatabase.getVersion();
            if (databaseVersion < DATABASE_VERSION) {
                onUpdate(databaseVersion, DATABASE_VERSION);
                mDatabase.setVersion(DATABASE_VERSION);
            }

            onInit();
        } catch (SQLiteException e) {
            throw new WrongEncryptionPasswordException();
        }

    }

    /**
     * Executed once when the database is created for the very first time. Should be used for creating the tables
     * and seeding data if necessary.
     */
    private void onCreate() {
        mDatabase.beginTransaction();

        mDatabase.execSQL("create table ticket (_id INTEGER PRIMARY KEY, " +
                "user_id REFERENCES user(_id) ON DELETE RESTRICT, license_code, date NOT NULL, latitude, longitude, " +
                "rut, first_name, last_name, address, vehicle, license_plate, email, location, description, " +
                "upload_state, zip_path, uuid, type);");
        mDatabase.execSQL("create table violation (_id INTEGER PRIMARY KEY, " +
                "ticket_id REFERENCES ticket(_id) ON DELETE CASCADE, type, cost);");
        mDatabase.execSQL("create table picture (_id INTEGER PRIMARY KEY, " +
                "ticket_id REFERENCES ticket(_id) ON DELETE CASCADE, path, type);");
        mDatabase.execSQL("create table user (_id INTEGER PRIMARY KEY, username, password, first_name, last_name, " +
                "precinct, courthouse_number, courthouse_city, plaque, rank, access_token);");
        mDatabase.execSQL("create table parameter(_id INTEGER PRIMARY KEY, key, value);");

        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    /**
     * Executed when there is a change in the database schema. For it to be properly supported, each time one such
     * change is performed, the constant {@link Database#DATABASE_VERSION} should be increased.
     *
     * @param oldVersion The current version of the database.
     * @param newVersion The new version the database is being upgraded to.
     */
    private void onUpdate(int oldVersion, int newVersion) {

    }

    /**
     * Executed each time the database is opened, after onCreate and onUpdate if necessary.
     */
    private void onInit() {
        mDatabase.execSQL("PRAGMA foreign_keys = ON;");

    }

    SQLiteDatabase getDatabase() {
        return mDatabase;
    }


    public void close() {
        mDatabase.close();
    }

    public static class WrongEncryptionPasswordException extends Exception {

    }

}
