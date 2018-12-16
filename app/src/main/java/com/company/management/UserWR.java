package com.company.management;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserWR {
    private final String USERFILE = "USER_LOGIN_STATE";
    private final String username = "username";
    private final String login = "isLogin";
    private final String userID = "userID";
    private final String gender = "gender";
    private final String role = "role";
    public UserWR() {
    }
    public void saveUserLogin(JSONObject user, boolean login, Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Log.i("user infor", user.toString());
        try {
            editor.putString(this.username, user.getString("employee_name"));
            editor.putString(this.gender, user.getString("sex"));
            editor.putInt(this.userID, user.getInt("id"));
            editor.putString(this.role, user.getString("position"));
            editor.putBoolean(this.login, login);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public String getUserID (Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        int id = sp.getInt(this.userID, -1);
        return String.valueOf(id);
    }
    public String getRole(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        String role = sp.getString(this.role, "root");
        String username = sp.getString(this.username, null);
        Log.i("username", username);
        Log.i("role", role);
        return role;
    }
    public String getGender (Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        String gender = sp.getString("gender", null);
        return gender;
    }
    public Map<String, String> getUser (Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        Map<String, String> user = new HashMap<>();
        user.put("username", getUserName(context));
        user.put("userID", getUserID(context));
        user.put("gender", getGender(context));
        user.put("role", getRole(context));
        return user;
    }
    public void removeUserLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
