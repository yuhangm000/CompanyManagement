package com.company.management;

import android.content.Context;
import android.content.SharedPreferences;

public class UserWR {
    private final String USERFILE = "USER_LOGIN_STATE";
    private final String username = "username";
    private final String login = "isLogin";
    public UserWR() {

    }
    public void saveUserLogin(String username, boolean login, Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(this.username, username);
        editor.putBoolean(this.login, login);
        editor.commit();
    }
    public boolean isLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        boolean isLogin = sp.getBoolean(this.login, false);
        return isLogin;
    }
    public String getUserName (Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        String name = sp.getString(this.username, null);
        return name;
    }
    public void removeUserLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
