package com.example.computer.appnapthe.datastore;

import android.content.Context;

import com.example.computer.appnapthe.datastore.db.DbConnection;

/**
 * Created by Trang on 5/20/2016.
 */
public class BaseDataStore {

    protected static BaseDataStore instance;
    protected MySharedPreferences sharedPreferences;
    protected DbConnection dbConnection;

    private static final String KEY_PREF_IS_INSTALLED = "PREF_IS_INSTALLED";

    /**
     *
     * Call when start application
     *
     */
    public static void init(Context context){
        instance = new BaseDataStore();

        instance.sharedPreferences = new MySharedPreferences(context);
        instance.dbConnection = new DbConnection(context);
    }

    public static BaseDataStore getInstance(){
        if (instance != null) {
            return instance;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }

    /**
     * Check app isIntalled?
     *
     */

    public static void setInstalled(boolean isInstalled){
        getInstance().sharedPreferences.putBooleanValue(KEY_PREF_IS_INSTALLED, isInstalled);
    }

    public static boolean isInstalled(){
        return getInstance().sharedPreferences.getBooleanValue(KEY_PREF_IS_INSTALLED);
    }

}
