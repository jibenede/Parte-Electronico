package com.puc.parte_electronico.globals;

import com.puc.parte_electronico.model.Database;
import com.puc.parte_electronico.model.User;

/**
 * Created by jose on 5/14/14.
 */
public class Settings {
    private static Settings sSingleton;

    private Database mDatabase;
    private User mCurrentUser;
    private String mAccessToken;

    public static Settings getSettings() {
        if (sSingleton == null) {
            sSingleton = new Settings();
        }

        return sSingleton;
    }

    public Database getDatabase() {
        return mDatabase;
    }

    public void setDatabase(Database database) {
        mDatabase = database;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(User user) {
        mCurrentUser = user;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String token) {
        mAccessToken = token;
    }



}
