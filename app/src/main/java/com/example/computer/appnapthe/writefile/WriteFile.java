package com.example.computer.appnapthe.writefile;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WriteFile {

    /*thread write file*/
    public static void progressWriteFile(final Activity activity, final String nameFile, final String content){
        Log.d("inForSIm", "Start thread ghi file");
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    saveData(activity, nameFile, content);
                }
            });
            thread.start();
        }catch (Exception e){

        }
    }


    /*c1 luu mac dinh*/
    public static void saveData(Activity activity, String nameFile, String content){
        if(isExternalStorageReadable()){
            File file;
            FileOutputStream outputStream;
            try {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nameFile);
                Log.d("MainActivity", Environment.getExternalStorageDirectory().getAbsolutePath());
                outputStream = new FileOutputStream(file);
                outputStream.write(content.getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("inForSim","Can not write");
            Toast.makeText(activity, "Can not write", Toast.LENGTH_SHORT).show();
        }
    }

    /*c2 luu vao thu muc public*/
    public static void saveDataInPublic(){
        if(isExternalStorageReadable()){
            String content = "Blog chia se kien thuc lap trinh";
            File file;
            FileOutputStream outputStream;
            try {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "thangcoder.txt");
                Log.d("MainActivity", Environment.getExternalStorageDirectory().getAbsolutePath());
                outputStream = new FileOutputStream(file);
                outputStream.write(content.getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("inForSim","Can not write");
        }
    }

    /*do file*/
    public static String readData(String nameFileCache){
        String txt = "";
        BufferedReader input = null;
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory(), nameFileCache);

            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }
            Log.d("MainActivity", buffer.toString());
            txt = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txt;
    }

    /*xin quyen*/
    private static void checkAndRequestPermissions(Activity activity) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    /*check xem bo nho thiet bi con du de ghi ko*/
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
