package com.company.management;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FormDetail extends AppCompatActivity {
    private final String [] statusMap = {"pending", "refuse", "success"};
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
    private Button agree;
    private Button refuse;
    private FloatingActionButton go_return;
    private ListContentAdapter lca;
    int originalPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_detail);
        init(); // 进行页面控件初始化
        // TODO：需要完成数据获取 @meng
        getDataFromBackward();
        /**
         * 设置相应的listview适配器
         */
        if (originalPage == R.string.material_picking && showReturnButton(username, status_info, creator_info)) {
            go_return.setVisibility(View.VISIBLE);
            go_return.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tableId", tableId);
                    bundle.putString("form-title", "还料表");
                    intent.putExtras(bundle);
                    intent.setClassName(getPackageName(), getPackageName() + ".FormCreate");
                    if(intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        }
        go_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("tableId", String.valueOf(tableId));
                bundle.putString("form-title", "还料表");
                intent.putExtras(bundle);
                intent.setClassName(getPackageName(), getPackageName()+".FormCreate");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
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
        tableHeadOperation = (TextView) findViewById(R.id.table_head_operation);
        agree = (Button) findViewById(R.id.pass);
        refuse = (Button) findViewById(R.id.reject);
        agree.setVisibility(View.INVISIBLE);
        refuse.setVisibility(View.INVISIBLE);
        go_return = (FloatingActionButton) findViewById(R.id.form_detail_turn_back);
        go_return.setVisibility(View.INVISIBLE);
        creator_title = getString(R.string.form_detail_creator);
        create_time_title = getString(R.string.form_detail_time);
        status_title = getString(R.string.form_detail_status);
        /**
         * 获取前一个页面传过来的参数
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.i("bundle in detail", bundle.toString());
        username = new UserWR().getUserName(getApplicationContext());
        tableId = bundle.getInt("tableId");
        originalPage = bundle.getInt("originalPage");
        Log.i("tableId", String.valueOf(tableId));
        Log.i("originalPage", String.valueOf(originalPage));
        if (originalPage == R.string.material_purchase_apply) {
            tableHeadOperation.setText("申请数量");
        } else if (originalPage == R.string.material_in_warehouse){
            tableHeadOperation.setText("入库数量");
        } else if (originalPage == R.string.material_turn_back) {
            tableHeadOperation.setText("归还数量");
        } else if (originalPage == R.string.material_picking) {
            tableHeadOperation.setText("领取数量");
        }
    }

    /**
     * 判断是否显示归还按钮
     * @param username
     * @param status
     * @param creator
     * @return
     */
    public boolean showReturnButton(String username, String status, String creator) {
        if (username.equals(creator) && status.equals("success") && originalPage == R.string.material_picking) {
            return true;
        } else {
            return false;
        }
    }

    public boolean showCheckoutButton(String username, int page) {
        if (page == R.string.material_purchase_apply) {
            if (acl.hasPermission(username, "material-purchase-form-check") && status_info.equals(statusMap[0])) {
                return true;
            } else {
                return false;
            }
        } else {
           return false;
        }
    }
    public void getDataFromBackward() {
        new GetFormDetail(String.valueOf(tableId)).start();
    }
    public void setTableBasicInfo() {
        tableCreator.setText(creator_title + divider + creator_info);
        tableCreateTime.setText(create_time_title + divider + create_time_info);
        tableStatus.setText(status_title + divider + status_info);
    }
    public void setTableMaterilaList() {
        lca= new ListContentAdapter(mData, mSize, mNumber);
        listView.setAdapter(lca);
    }
    Handler handler = new Handler() {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 3) {
                Toast.makeText(getApplicationContext(),"服务器里空空如也~", 1000).show();
                return;
            }
            JSONObject table_msg = (JSONObject) msg.obj;
            try {
                /**
                 * 获取表的基本信息
                 */
                creator_info = table_msg.getString("writer");
                create_time_info = table_msg.getString("create_time");
                try {
                    status_info = statusMap[table_msg.getInt("status")];
                } catch (Exception e) {
                    status_info = table_msg.getString("status");
                }
                /**
                 * 获取表的内容
                 */
                JSONArray jsonArray = JsonUtils.GetJsonArray(table_msg,"material");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mData.add(jsonObject.getString("name"));
                    mSize.add(jsonObject.getString("unit"));
                    try{
                        mNumber.add(Integer.valueOf(jsonObject.getString("num")));
                    } catch (Exception e) {
                        try {
                            mNumber.add(Integer.valueOf(jsonObject.getString("receive_num")));
                        } catch (Exception ee) {
                            mNumber.add(Integer.valueOf(jsonObject.getString("number")));
                        }

                    }
                }
                setTableBasicInfo();
                setTableMaterilaList();
                /**
                 * 判断是否显示审查按钮
                 */
                if (!showCheckoutButton(username, originalPage)) {
                    if (showReturnButton(username, status_info, creator_info)) {
                        go_return.setVisibility(View.VISIBLE);
                    }
                } else {
                    agree.setVisibility(View.VISIBLE);
                    refuse.setVisibility(View.VISIBLE);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    String getRoutes (int page) {
        switch (page) {
            case R.string.material_in_warehouse:
                return Router.MATERIAL_IN_WAREHOUSE_DETAIL;
            case R.string.material_out_warehouse:
                return Router.MATERIAL_OUT_WAREHOUSE_DETAIL;
            case R.string.material_picking:
                return Router.MATERIAL_PICKING_DETAIL;
            case R.string.material_purchase_apply:
                return Router.MATERIAL_PURCHASE_APPLY_DETAIL;
            case R.string.material_turn_back:
                return Router.MATERIAL_RETURN_DETAIL;
            default:
                return null;
        }
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
                JSONObject params = new JSONObject();
                params.put("table_id", this.form_id);
                String routes = getRoutes(originalPage);
                JSONObject result = Conn.get(routes, params);
                Message msg =handler.obtainMessage();
                if (JsonUtils.StatusOk(result)){
                    msg.obj = JsonUtils.GetParam(result);
                    Log.i("form detail", result.toString());
                    Log.i("form material detail", msg.obj.toString());
                }
                else {
                    Log.e("get detail failed", result.toString());
                    msg.arg1 = 3;
                }
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
