package com.company.management;

import android.annotation.SuppressLint;
import android.app.Fragment;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormDetail extends AppCompatActivity {
    private final String [] BASICINFO = {
            "writer", "create_time", "status", "verify",
            "verifier", "verify_time", "back", "back_time",
            "backer", "check", "check_time", "checker",
            "waste_back_table_num","waste_back","waste_backer","waste_back_time"
    };
    private final Map<String, String> BASIC_INFO_MAP = new HashMap<>();
    private final String [] statusMap = {"pending", "refuse", "success"};
    private final String [] operationMap = {"否", "是"};
    private ACL acl;
    private Context context;
    private String username;
    private int tableId;
    private ListView listView;
    private LinearLayout basic_info;
    private TextView tableHeadOperation;
    private List<String> mData = new ArrayList<>(), mSize = new ArrayList<>(), mUnit = new ArrayList<>();
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
    }
    public void init() {
        context = getBaseContext();
        acl = (ACL) getApplicationContext();
        /**
         * 获取页面控件
         */
        listView = (ListView) findViewById(R.id.material_list);
        basic_info = (LinearLayout) findViewById(R.id.basic_information);
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
        BASIC_INFO_MAP.put(BASICINFO[0], "创建者");
        BASIC_INFO_MAP.put(BASICINFO[1], "创建时间");
        BASIC_INFO_MAP.put(BASICINFO[2], "当前状态");
        BASIC_INFO_MAP.put(BASICINFO[3], "是否确认");
        BASIC_INFO_MAP.put(BASICINFO[4], "确认人员");
        BASIC_INFO_MAP.put(BASICINFO[5], "确认时间");
        BASIC_INFO_MAP.put(BASICINFO[6], "是否归还");
        BASIC_INFO_MAP.put(BASICINFO[7], "归还时间");
        BASIC_INFO_MAP.put(BASICINFO[8], "归还者");
        BASIC_INFO_MAP.put(BASICINFO[9], "是否检查");
        BASIC_INFO_MAP.put(BASICINFO[10], "检查时间");
        BASIC_INFO_MAP.put(BASICINFO[11], "检查者");
        BASIC_INFO_MAP.put(BASICINFO[12], "废旧材料归还单号");
        BASIC_INFO_MAP.put(BASICINFO[13], "废料归还状况");
        BASIC_INFO_MAP.put(BASICINFO[14], "废料归还人");
        BASIC_INFO_MAP.put(BASICINFO[15], "废料归还时间");
        /**
         * 获取前一个页面传过来的参数
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = new UserWR().getUserName(getApplicationContext());
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
    }

    /**
     * 判断是否显示归还按钮
     * @param username
     * @param status
     * @param creator
     * @return
     */
    public boolean showReturnButton(String username, String status, String creator) {
        if (username.equals(creator) && (status.equals("success") || status.equals("pass")) && originalPage == R.string.material_picking) {
            return true;
        } else {
            return false;
        }
    }

    public boolean showCheckoutButton(String username, int page) {
        if (page == R.string.material_purchase_apply) {
            if (acl.hasPermission(username, "material-purchase-form-check", getBaseContext()) && status_info.equals(statusMap[0])) {
                return true;
            } else {
                return false;
            }
        } else if(page == R.string.material_picking){
            try {
                if (acl.hasPermission(username, "material-get-form-check", getBaseContext()) && status_info.equals(statusMap[0])) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e("errorInPicking", e.toString());
                Log.e("errorInPickingAcl", String.valueOf(acl.getRoles().isEmpty()));
                Log.e("errorInPickingStatusInf", String.valueOf(status_info==null));
                Log.e("errorInPickingStatusMap", String.valueOf(statusMap==null));
                return false;
            }
        } else {
           return false;
        }
    }
    public void getDataFromBackward() {
        new GetFormDetail(String.valueOf(tableId)).start();
    }
    public void setTableBasicInfo(JSONObject table_msg) {
        /**
         * 获取表的基本信息
         */
        try {
            int count = 0;
            for (int i = 0; i < BASICINFO.length; i++) {
                String info = null;
                info = table_msg.getString(BASICINFO[i]);
                if (info.equals("null")) {
                    continue;
                }
                if (BASICINFO[i].equals("status")) {
                    status_info = info;
                }
                final TextView textView = new TextView(context);
                textView.setText(BASIC_INFO_MAP.get(BASICINFO[i]) + ":" + info);
                basic_info.addView(textView);
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setTableMaterilaList(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                mData.add(jsonObject.getString("name"));
                mSize.add(jsonObject.getString("size"));
                mUnit.add(jsonObject.getString("unit"));
                try{
                    mNumber.add(Integer.valueOf(jsonObject.getString("num")));
                } catch (Exception e) {
                    try {
                        mNumber.add(Integer.valueOf(jsonObject.getString("receive_num")));
                    } catch (Exception ee) {
                        mNumber.add(Integer.valueOf(jsonObject.getString("number")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        lca= new ListContentAdapter(mData, mSize, mNumber, mUnit);
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
            /**
             * 获取表的内容
             */
            JSONArray jsonArray = JsonUtils.GetJsonArray(table_msg,"material");
            setTableBasicInfo(table_msg);
            setTableMaterilaList(jsonArray);
            /**
             * 判断是否显示审查按钮
             */
            if (!showCheckoutButton(username, originalPage)) {
                if (showReturnButton(username, status_info, creator_info)) {
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
            } else {
                agree.setVisibility(View.VISIBLE);
                refuse.setVisibility(View.VISIBLE);
                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refuse.setVisibility(View.INVISIBLE);
                        agree.setVisibility(View.INVISIBLE);
                        status_info = "pass";
//                            tableStatus.setText(status_title + divider + status_info);
                        new TableStatus("pass").start();
                    }
                });
                refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refuse.setVisibility(View.INVISIBLE);
                        agree.setVisibility(View.INVISIBLE);
                        status_info = "refuse";
//                            tableStatus.setText(status_title + divider + status_info);
                        new TableStatus("refuse").start();
                    }
                });
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
            jsonObject.put("table_id", tableId);
            if (originalPage == R.string.material_picking) {
                JSONObject jsonObject1 = Conn.post(Router.MATERIAL_PICKING_CHECK, jsonObject);
                Log.i("Verify table status", jsonObject1.toString());
            } else if (originalPage == R.string.material_purchase_apply) {
                Conn.post(Router.MATERIAL_PURCHASE_CHECK, jsonObject);
            }
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
