package com.puc.parte_electronico.model;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * Created by jose on 4/30/14.
 */
public class Database {
    private static final String DATABASE_NAME = "ParteElectronico.sql";
    private static Database sSingleton;

    private SQLiteDatabase mDatabase;

    private static Database getDatabase(Context context, String password) {
        if (sSingleton == null) {
            sSingleton = new Database(context, password);
        }
        return sSingleton;
    }

    private Database(Context context, String password) {
        SQLiteDatabase.loadLibs(context);
        File databaseFile = context.getDatabasePath(DATABASE_NAME);
        databaseFile.mkdirs();
        mDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFile, password, null);
        mDatabase.execSQL("create table t1(a, b)");
        mDatabase.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                "two for the show"});

        SQLiteOpenHelper helper;
    }



}
