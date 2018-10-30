package com.company.management;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.content.Context;
public class UserInfoSaveAndRead {
    String filename = "user-info";
    public void saveUserInfo(String username, Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(username.getBytes("utf-8"));
            outputStream.close();
        } catch (Exception e) {
            Log.e("error", "saveUserInfo: " + e.getMessage());
        }
    }
    public String getUserInfo(Context context) {
        byte [] buffer = new byte[1024];
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(filename);
            inputStream.read(buffer);
            inputStream.close();
        } catch (Exception e) {
            Log.e("error", "getUserInfo:" + e.getMessage());
        }
        return buffer.toString();
    }
}