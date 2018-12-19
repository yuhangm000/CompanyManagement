package com.company.management;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Iterator;

/**
 * 连接远程服务器，传入请求路径,和JSONObject，返回JSONObject
 * 如果出现异常，手动构建json格式错误
 */
public class Conn {
    public final static String server = "http://47.93.19.169:9876";
    public final static String POST= "POST";
    public final static String GET = "GET";
    public final static String QUERY = "QUERY";
    public final static String DELETE = "DELETE";

    public static HttpURLConnection openConnection(String api, String method) throws IOException {
        //基础配置
        String urlPath =  server + api;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        if (method.equals(POST) || method.equals(QUERY))
            conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        conn.setRequestProperty("accept","application/json");
        return conn;
    }
    public static JSONObject parse(String res) {
        JSONObject objRes = null;
        try {
            objRes = new JSONObject(res);
        }
        catch (JSONException e){
            e.printStackTrace();
            try {
                objRes = new JSONObject("{'code':500, 'msg':'format error'}");
            }
            catch (JSONException e1){
                e1.printStackTrace();
            }
        }
        return objRes;
    }
    public static String requestPOST(HttpURLConnection conn,String params) throws IOException {
        BufferedReader reader = null;
        String result = null;
        byte[] writebytes = params.getBytes();
        conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
        try {
            OutputStream outwritestream = conn.getOutputStream();
            outwritestream.write(params.getBytes());
            outwritestream.flush();
            outwritestream.close();
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
            }
            else{
                result = "{'code':500, 'msg':'未知错误1'}";
            }
            if (reader != null)
                reader.close();
            Log.d("upload", "doJsonPost: conn" + conn.toString());
        }catch (Exception e){
            Log.d("upload", "error:" + e.toString());
            result = "{'code':500, 'msg':'服务器网络错误'}";
        }
        return result;
    }
    public static String requestGET(HttpURLConnection conn) throws IOException {
        BufferedReader reader = null;
        String result = null;
        conn.connect();
        Log.d("GET_connect", conn.toString());
        if (conn.getResponseCode() == 200) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = reader.readLine();
        }
        else{
            result = "{'code':500, 'msg':'未知错误1'}";
        }
        if (reader != null)
            reader.close();
        Log.d("requestGET", result.toString());
        return result;
    }
    public static JSONObject get(String api, JSONObject jsonObject) throws IOException {
        try {
            HttpURLConnection conn;
            if (jsonObject != null) {
                String params = "";
                Iterator<String> keys = jsonObject.keys();
                while (((Iterator) keys).hasNext()) {
                    String key = ((Iterator) keys).next().toString();
                    if (params == "")
                        params = params + key + "=" + jsonObject.get(key);
                    else {
                        params = params + " &" + key + "=" + jsonObject.get(key);
                    }
                }
                conn = openConnection(api + "?" + params, GET);
            }else{
                conn = openConnection(api, GET);
            }
            String result = requestGET(conn);
            JSONObject jObject = parse(result);
            return jObject;
        } catch (IOException e) {
            Log.e("GET_ERROR", e.getMessage());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static JSONObject post(String urlPath, JSONObject JsonObj) throws IOException {
        String result = "";
        String Json = "";
        JSONObject objRes = null;
        if(JsonObj != null)
            Json = JsonObj.toString();
        HttpURLConnection conn = openConnection(urlPath, POST);
        Log.i("post params", Json);
        result = requestPOST(conn, Json);
        Log.i("result", result);
        //将接收的字符串转换成json格式
        objRes = parse(result);
        //返回json对象
        return objRes;
    }

    //重载
    public static JSONObject post(String urlPath, JSONObject JsonObj, String type) {
        String result = "";
        JSONObject objRes = null;
        String Json = "";
        if(JsonObj != null)
            Json = JsonObj.toString();
        if((type != POST)&&(type != GET)&&(type != QUERY)&&(type != DELETE))
            type = POST;
        try {
            //基础配置
            HttpURLConnection conn = openConnection(urlPath, type);
            //发送不能为空
            result = requestPOST(conn, Json);
        } catch (Exception e) {
            result = "{'code':500, 'msg':'未知错误2'}";
            e.printStackTrace();
        }
        //将接收的字符串转换成json格式
        objRes = parse(result);
        //返回json对象
        return objRes;
    }
    public static JSONObject doJsonPost(String urlPath, JSONObject JsonObj) throws IOException {
        JSONObject jsonObject = post(urlPath, JsonObj);
        return jsonObject;
    }
}
