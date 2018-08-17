package com.example.computer.appnapthe.network;

import android.app.Activity;

/**
 * Created by Computer on 13/11/2017.
 */

public class MyThreadConnectSocket implements Runnable {

    private Activity activity;

    private static MyThreadConnectSocket _instance = null;
    public static MyThreadConnectSocket getInstance(Activity activity){

        if(_instance == null) {
            _instance = new MyThreadConnectSocket();
        } else if(activity!=null){
            _instance.setActivity(activity);
        }
        return _instance;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {

    }
}
