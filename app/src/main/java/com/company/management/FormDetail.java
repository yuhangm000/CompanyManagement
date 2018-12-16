package com.company.management;

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

import org.json.JSONArray;
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
        setTableBasicInfo(); // 完成连通后以下两行删除
        setTableMaterilaList();
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
        username = bundle.getString("username");
        tableId = bundle.getInt("tableId");
        originalPage = bundle.getInt("originalPage");
        if (originalPage == R.string.material_purchase_apply) {
            tableHeadOperation.setText("申请数量");
        } else if (originalPage == R.string.material_in_warehouse){
            tableHeadOperation.setText("入库数量");
        } else if (originalPage == R.string.material_turn_back) {
            tableHeadOperation.setText("归还数量");
        } else if (originalPage == R.string.material_picking) {
            tableHeadOperation.setText("领取数量");
        }
        /**
         * 判断是否显示审查按钮
         */
        if (!showCheckoutButton(username, tableId)) {
            agree.setVisibility(View.INVISIBLE);
            refuse.setVisibility(View.INVISIBLE);
//            if (showReturnButton(username, status_info, creator_info)) {
//                go_return.setVisibility(View.VISIBLE);
//            }
            if (showReturnButton("root", "success", "root")) {
                go_return.setVisibility(View.VISIBLE);
            }
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
//        new GetFormDetail(String.valueOf(tableId)).start();
        mData.add("1");
        mSize.add("20*20mm");
        mNumber.add(0);
    }
    public void setTableBasicInfo() {
        // TODO: 填充完成数据之后，需要删除以下三行代码 @meng
        create_time_info = "2018.11.11 00:00:00";
        creator_info = "yanhua";
        status_info = "pass";
        tableCreator.setText(creator_title + divider + creator_info);
        tableCreateTime.setText(create_time_title + divider + create_time_info);
        tableStatus.setText(status_title + divider + status_info);
    }
    public void setTableMaterilaList() {
        lca= new ListContentAdapter(mData, mSize, mNumber);
        listView.setAdapter(lca);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject table_msg = (JSONObject) msg.obj;
            try {
                /**
                 * 获取表的基本信息
                 */
                creator_info = table_msg.getString("creator");
                create_time_info = table_msg.getString("time");
                status_info = table_msg.getString("status");
                /**
                 * 获取表的内容
                 */
                JSONArray jsonArray = table_msg.getJSONArray("material_list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mData.add(jsonObject.getString("material_name"));
                    mSize.add(jsonObject.getString("material_size"));
                    mNumber.add(Integer.valueOf(jsonObject.getString("material_number")));
                }
                setTableBasicInfo();
                setTableMaterilaList();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
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
                // TODO: 联通后台api，获取数据格式，然后进行填充（handler） @meng
                /**
                 * result 中需要有   表格的基本信息和材料的所有信息
                 * result {
                 *     creator_info: 创建者
                 *     time: 创建时间
                 *     status: 表格状态(pengding|success|reject)
                 *     material_list: [
                 *     {
                 *     material_name:
                 *     material_size:
                 *     material_number:
                 *     }
                 *     ]
                 */
                JSONObject params = new JSONObject();
                params.put("table_id", this.form_id);
                JSONObject result = Conn.get(Router.MATERIAL_IN_WAREHOUSE_DETAIL, params);
                Message msg = handler.obtainMessage();
                msg.obj = result;
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
