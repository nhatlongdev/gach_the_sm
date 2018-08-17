package com.example.computer.appnapthe;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.example.computer.appnapthe.datastore.BaseDataStore;
import com.example.computer.appnapthe.modelmanager.RequestManager;
import com.example.computer.appnapthe.network.BaseRequest;
import com.example.computer.appnapthe.network.VolleyRequestManager;

/**
 * Created by Trang on 1/8/2016.
 */
public class AppController extends Application {

    // TODO: 7/11/2016 Need comment
    private static AppController instance;
    private String token;

    private int global;

    public static AppController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            instance = this;
            BaseDataStore.init(getApplicationContext());
            Log.d("inForSIm", "CO CHAY VAO DYA");
            VolleyRequestManager.init(getApplicationContext());
            BaseRequest.getInstance();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString() , Toast.LENGTH_SHORT).show();
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getGlobal() {
        return global;
    }

    public void setGlobal(int global) {
        this.global = global;
    }
}
