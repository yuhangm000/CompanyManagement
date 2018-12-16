package com.company.management;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    /**
     * 如果返回为null,证明解析错误
     * @param obj
     * @return
     */
    public static JSONObject GetParam(JSONObject obj){
        try {
            String paramStr = obj.getString("param");
            if (paramStr.startsWith("\ufeff")) {
                paramStr = paramStr.substring(1);
            }
            if (paramStr == ""){
                return null;
            }
            JSONObject param = new JSONObject(paramStr);
            return param;
        } catch (JSONException e){
            Log.e("parse json error", e.getMessage());
            return null;
        }
    }

    /**
     * 根据key取得嵌套json, 返回为null的话，证明出错
     * @param obj
     * @param key
     * @return
     */
    public static JSONObject GetJsonObj(JSONObject obj, String key){
        try {
            String paramStr = obj.getString(key);
            if (paramStr.startsWith("\ufeff")) {
                paramStr = paramStr.substring(1);
            }
            if (paramStr == ""){
                return null;
            }
            JSONObject param = new JSONObject(paramStr);
            return param;
        } catch (JSONException e){
            Log.e("parse json error", e.getMessage());
            return null;
        }
    }

    /**
     * 如果param是个list，可以用此方法
     * @param obj
     * @return
     */
    public static JSONArray GetJsonArrayParam(JSONObject obj){
        try{
            JSONArray array = obj.getJSONArray("param");
            return array;
        }catch (JSONException e){
            Log.e("parse json error", e.getMessage());
            return null;
        }
    }

    /**
     * 获取嵌套json的list，可以用此方法
     * @param obj
     * @return
     */
    public static JSONArray GetJsonArray(JSONObject obj, String key){
        try{
            JSONArray array = obj.getJSONArray(key);
            return array;
        }catch (JSONException e){
            Log.e("parse json error", e.getMessage());
            return null;
        }
    }

    public static boolean StatusOk(JSONObject obj){
        try{
            int status = obj.getInt("status");
            if (status == 1){
                return true;
            }
        } catch (JSONException e){
            Log.e("parse json error", e.getMessage());
            return false;
        }
        return false;
    }

    public static String GetMsg(JSONObject obj){
        try{
            String msg = obj.getString("msg");
            return msg;
        }catch (JSONException e){
            Log.e("parse json error", e.getMessage());
            return "";
        }
    }
}
