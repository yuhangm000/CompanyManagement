package com.company.management;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.List;

public class FormDetail extends AppCompatActivity {
    private ACL acl;
    private Context context;
    private String username;
    private int tableId;
    private ListView listView;
    private TextView tableCreator;
    private TextView tableCreateTime;
    private TextView tableStatus;
    private TextView tableHeadOperation;
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
        context = getBaseContext();
        acl = (ACL) getApplicationContext();
        /**
         * 获取页面控件
         */
        listView = (ListView) findViewById(R.id.material_list);
        tableCreator = (TextView) findViewById(R.id.form_detail_creator);
        tableCreateTime = (TextView) findViewById(R.id.form_detail_time);
        tableStatus  = (TextView) findViewById(R.id.form_detail_status);
        agree = (Button) findViewById(R.id.pass);
        refuse = (Button) findViewById(R.id.reject);
        tableHeadOperation = (TextView) findViewById(R.id.table_head_operation);

        creator_title = getString(R.string.form_detail_creator);
        create_time_title = getString(R.string.form_detail_time);
        status_title = getString(R.string.form_detail_status);
        /**
         * 获取前一个页面传过来的参数
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");
        tableId = bundle.getInt("tableId");
        int originalPage = bundle.getInt("originalPage");
        if (originalPage == R.string.material_purchase_apply) {
            tableHeadOperation.setText("申请数量");
        } else if (originalPage == R.string.material_in_warehouse){
            tableHeadOperation.setText("入库数量");
        } else if (originalPage == R.string.material_turn_back) {
            tableHeadOperation.setText("归还数量");
        } else if (originalPage == R.string.material_out_warehouse) {
            tableHeadOperation.setText("领取数量");
        }
        if (!showCheckoutButton(username, tableId)) {
            agree.setVisibility(View.INVISIBLE);
            refuse.setVisibility(View.INVISIBLE);
        } else {
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuse.setVisibility(View.INVISIBLE);
                    agree.setVisibility(View.INVISIBLE);
                    new TableStatus("pass").run();
                }
            });
            refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuse.setVisibility(View.INVISIBLE);
                    agree.setVisibility(View.INVISIBLE);
                    new TableStatus("refuse").run();
                }
            });
        }
    }
    public boolean showCheckoutButton(String username, int page) {
        if (page == R.string.material_purchase_apply) {
            if (acl.hasPermission(username, "apply_table_check") && !status_info.equals("pending")) {
                return true;
            } else {
                return false;
            }
        } else {
           return false;
        }
    }
    public void getDataFromBackward() {
//        new GetFormDetail(String.valueOf(tableId)).run();
        mData.add("1");
        mSize.add("20*20mm");
        mNumber.add(0);
    }
    public void setTableBasicInfo() {
        // TODO: 填充完成数据之后，需要删除以下三行代码
        create_time_info = "2018.11.11 00:00:00";
        creator_info = "yanhua";
        status_info = "pass";
        tableCreator.setText(creator_title + divider + creator_info);
        tableCreateTime.setText(create_time_title + divider + create_time_info);
        tableStatus.setText(status_title + divider + status_info);
    }

    /**
     * 类声明区域
     */
    class GetFormDetail extends Thread {
        String form_id;

        public GetFormDetail(String form_id) {
            this.form_id = form_id;
        }

        @Override
        public void run() {
            try {
                // TODO: 联通后台api，获取数据格式，然后进行填充（handler）
                Conn.get("/formDtail", this.form_id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }
    class TableStatus extends Thread {
        String status;
        public TableStatus(String status) {
            this.status = status;
        }
        public void setTableStatus() throws IOException, JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", this.status);
            Conn.post("/setTableStatus", jsonObject);
            /**
             * 发送完成之后，向handler发送一个消息，更新表格界面
             */
        }

        @Override
        public void run() {
            try {
                this.setTableStatus();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
