package com.company.management;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Map;
import java.util.PriorityQueue;
import com.company.management.R;

public class myinformation extends AppCompatActivity {
    private CircleImageView circleImageView;
    private Bitmap bitmap;
    private Context context;
    private InformationView level,account,gender,role;
    private InformationView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinformation);
        context = getBaseContext();
        circleImageView = (CircleImageView) findViewById(R.id.imageBox);
        level = (InformationView) findViewById(R.id.level);
        account = (InformationView) findViewById(R.id.account);
        gender = (InformationView) findViewById(R.id.gender);
        role = (InformationView) findViewById(R.id.address);
        name = (InformationView) findViewById(R.id.name);
        new GetContent().start();
    }
    public void setContent(JSONObject content){
        try{
            if(bitmap != null){
                circleImageView.setImageBitmap(bitmap);
            }
            name.setText("姓名:" + content.getString("employee_name"));
            account.setText("账号:" + content.getString("employee_id"));
            gender.setText("性别:" + content.getString("sex"));
            role.setText("职位:" + content.getString("position"));
            level.setVisibility(View.INVISIBLE);
        }
        catch (Exception e){
            Log.e("error in setInfor",e.toString());
        }
    }
    class  GetContent extends Thread{
        @Override
        public void run() {
            /**
             * TODO: 需要更改
             */
            String user_id = new UserWR().getUserID(context);
            try {
                JSONObject param = new JSONObject();
                param.put("user_id", Integer.valueOf(user_id));
                JSONObject jsonObject = Conn.get(Router.USER_INFO, param);
                Message msg = handler.obtainMessage();
                if (JsonUtils.StatusOk(jsonObject)) {
                    msg.arg1 = 2;
                    jsonObject = JsonUtils.GetJsonObj(jsonObject, "param");
                    msg.obj = jsonObject;
                } else {
                    msg.arg1 = 3;
                }
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Uri getHeadImage(){
        Uri iv = null;
        return iv;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 3) {
                Toast.makeText(context, "服务器开小差啦~", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject jsonObject = (JSONObject) msg.obj;
            setContent(jsonObject);
        }
    };
    class InformaitonClickLIstern implements View.OnClickListener {
        private String pakageName = "com.company.management";
        private String toActivity;
        private String params;
        InformaitonClickLIstern(String toActivity, String params){
            this.toActivity = toActivity;
            this.params = params;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClassName(pakageName,pakageName+toActivity);
            PackageManager pm = context.getPackageManager();
            Bundle bundle = new Bundle();
            bundle.putString("data",this.params);
            intent.putExtra("data",bundle);
            if(intent.resolveActivity(pm) != null){
                startActivity(intent);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String pakageName = "com.company.management";
        String toActivity = ".BottomNavigation";
        Intent intent = new Intent();
        intent.setClassName(pakageName,pakageName+toActivity);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment",4);
        intent.putExtra("data",bundle);
        PackageManager pm = getPackageManager();
        finishActivity(1);
        if(intent.resolveActivity(pm) != null){
            startActivity(intent);
            finish();
        }
    }
}
