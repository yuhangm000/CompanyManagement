package com.company.management;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.List;

public class FormDetail extends AppCompatActivity {
    private ACL acl;
    private Context context;
    private String username;
    private int tableId;
    private ListView listView;
    private TextView creator;
    private TextView createTime;
    private TextView status;

    private List<String> mData = new ArrayList<>(), mSize = new ArrayList<>();
    private List<Integer> mNumber = new ArrayList<>();

    String creator_title = null;
    String create_time_title = null;
    String status_title = null;
    String divider = " : ";
    String creator_info, create_time_info, status_info;
    private Button agree, refuse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_detail);
        init();
        // TODO：需要完成数据获取
        getDataFromBackward();
        setTableBasicInfo();
        /**
         * 设置相应的listview适配器
         */
        ListContentAdapter lca = new ListContentAdapter(mData, mSize, mNumber);
        listView.setAdapter(lca);
    }
    public void init() {
        context = getApplicationContext();
        listView = (ListView) findViewById(R.id.material_list);
        creator = (TextView) findViewById(R.id.form_detail_creator);
        createTime = (TextView) findViewById(R.id.form_detail_time);
        status  = (TextView) findViewById(R.id.form_detail_status);
        creator_title = getString(R.string.form_detail_creator);
        create_time_title = getString(R.string.form_detail_time);
        status_title = getString(R.string.form_detail_status);
        agree = (Button) findViewById(R.id.pass);
        refuse = (Button) findViewById(R.id.reject);
        acl = (ACL) getApplicationContext();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");
        tableId = bundle.getInt("originalPage");
        if (!showCheckoutButton(username, tableId)) {
            agree.setVisibility(View.INVISIBLE);
            refuse.setVisibility(View.INVISIBLE);
        } else {
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuse.setVisibility(View.INVISIBLE);
                    agree.setVisibility(View.INVISIBLE);
                    // TODO: 添加向后台发送消息的操作

                }
            });
            refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuse.setVisibility(View.INVISIBLE);
                    agree.setVisibility(View.INVISIBLE);
                    // TODO: 添加向后台发送消息的操作
                }
            });
        }
    }
    public boolean showCheckoutButton(String username, int page) {
        if (page == R.string.material_apply) {
            if (acl.hasPermission(username, "apply_table_check")) {
                return true;
            } else {
                return false;
            }
        } else {
           return false;
        }
    }
    public void getDataFromBackward() {
        mData.add("1");
        mSize.add("20*20mm");
        mNumber.add(0);
    }
    public void setTableBasicInfo() {
        // TODO: 替换成相应的*_info
        creator.setText(creator_title + divider + "yanhua");
        createTime.setText(create_time_title + divider + "2018.11.11 00:00:00");
        status.setText(status_title + divider + "完成");
    }
}
