package com.company.management;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * todo 紧急！前端确定当拿取数据出错时候的代码
 */
public class FormCreate extends AppCompatActivity {
    /**
     * 控件声明区
     */
    private TextView creator;
    private TextView status;
    private TextView form_create_title;
    private ListView material_list;
    private Button cancel, complete, add_item;
    /**
     * 数据常量区域
     */
    private final String creator_forward = "创建者：";
    private final String status_forward = "状态：";
    /**
     * 数据变量区域
     */
    private List<TableItem> items;
    private String form_title = null;
    private int tableId = -1;
    private FormCreateListContentAdapter formCreateListContentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentInfo();
        setContentView(R.layout.activity_form_create);
        init();
        material_list.setAdapter(formCreateListContentAdapter);
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.add(new TableItem());
                formCreateListContentAdapter.notifyDataSetChanged();
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * TODO: 添加获取listview所有内容的代码, 并且向后端发送请求
                 */
                int size = formCreateListContentAdapter.getCount();

                JSONArray jsonArray = new JSONArray();
                try {
                    for (int i = 0; i < size; i++) {
                        JSONObject jsonObject = new JSONObject();
                        TableItem item = (TableItem) formCreateListContentAdapter.getItem(i);
                        jsonObject.put("id", Integer.valueOf(formCreateListContentAdapter.getId(item.getMaterial(), item.getSize())));
                        jsonObject.put("material_id", Integer.valueOf(formCreateListContentAdapter.getId(item.getMaterial(), item.getSize())));
                        jsonObject.put("num", Integer.valueOf(item.getNumber()));
                        jsonArray.put(jsonObject);
                        View view = material_list.getChildAt(i);
                        view.findViewById(R.id.form_create_list_content_show_material).setEnabled(false);
                        view.findViewById(R.id.form_create_list_content_show_size).setEnabled(false);
                        view.findViewById(R.id.form_create_list_content_show_number).setEnabled(false);
                        view.findViewById(R.id.form_create_list_content_delete).setEnabled(false);
                    }
                    new SendMaterialRequest(jsonArray).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                complete.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                add_item.setVisibility(View.INVISIBLE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();
                formCreateListContentAdapter.notifyDataSetChanged();
            }
        });
    }
    void getIntentInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        form_title = bundle.getString("form-title");
        if (form_title.equals("还料表")) {
            tableId = bundle.getInt("tableId");
        }
    }
    void init() {
        /**
         * 获取控件句柄
         */
        creator = (TextView) findViewById(R.id.form_create_creator);
        status = (TextView) findViewById(R.id.form_create_status);
        form_create_title = (TextView) findViewById(R.id.form_create_title);
        material_list = (ListView) findViewById(R.id.form_create_material_list);
        cancel = (Button) findViewById(R.id.form_create_cancel);
        complete = (Button) findViewById(R.id.form_create_complete);
        add_item = (Button) findViewById(R.id.add_item);
        items = new ArrayList<>();
        items.add(new TableItem());
        formCreateListContentAdapter = new FormCreateListContentAdapter(getBaseContext(),items);
        form_create_title.setText("新建"+form_title);
        status.setText(status_forward + "申请中");
        creator.setText(creator_forward + (new UserWR().getUserName(getApplicationContext())));
        new GetMaterialList().start();
        if (tableId != -1) {
            new GetPickingMaterial().start();
            add_item.setVisibility(View.INVISIBLE);
        }
}
    String getRoutes(String form_title) {
        Log.i("routes", form_title);
        switch (form_title) {
            case "入库单":
                return Router.MATERIAL_IN_WAREHOUSE_CREATE;
            case "出库单":
                return Router.MATERIAL_OUT_WAREHOUSE_CREATE;
            case "物料采购申请表":
                return Router.MATERIAL_PURCHASE_APPLY_CREATE;
            case "领料单":
                return  Router.MATERIAL_PICKING_CREATE;
            case "还料表":
                return Router.MATERIAL_RETURN_CREATE;
            default:
                return "NaN";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 异步函数区域
     * Message 参数说明
     * arg1 == 1 未开通服务
     * arg1 == 2 为设置初始项内容
     * arg1 == 3 服务器端错误
     * arg1 == else 提示其余信息
     * obj存放需要操作的对象内容
     */
    Handler handler = new Handler() {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Toast.makeText(getApplicationContext(), "暂未开通该申请功能", 1000).show();
                return;
            } else if (msg.arg1 == 3) {
                Toast.makeText(getApplicationContext(), "服务器开小差了~", 1000).show();
                return;
            } else if (msg.arg1 == 2) {
                formCreateListContentAdapter.setItems((ArrayList<TableItem>)msg.obj);
            } else if(msg.arg1 == 4) {
                Toast.makeText(getApplicationContext(), (String)msg.obj, 1000).show();
                onBackPressed();
//                Intent intent = new Intent();
//                intent.setClassName(getPackageName(), getPackageName() + ".FormList");
            } else {
                Toast.makeText(getApplicationContext(), (String) msg.obj,  1000).show();
                return;
            }
        }
    };
    class SendMaterialRequest extends Thread{
        private  JSONArray jsonArray;
        public SendMaterialRequest(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
        }
        @Override
        public void run() {
            super.run();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("applier", Integer.valueOf(new UserWR().getUserID(getApplicationContext())));
                jsonObject.put("receiver", Integer.valueOf(new UserWR().getUserID(getApplicationContext())));
                jsonObject.put("writer", Integer.valueOf(new UserWR().getUserID(getApplicationContext())));
                jsonObject.put("material", jsonArray);
                String routes = getRoutes(form_title);
                Message msg = handler.obtainMessage();
                if (routes.equals("NaN")) {
                    msg.arg1 = 1;
                } else {
                    JSONObject jsonObject1 = Conn.post(routes, jsonObject);
                    if (JsonUtils.StatusOk(jsonObject1)) {
                        msg.arg1 = 0;
                    } else {
                        msg.arg1 = -1;
                    }
                    msg.obj = jsonObject1.getString("msg");
                }
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class GetMaterialList extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                JSONObject object = Conn.get(Router.MATERIAL_LIST, null);
                if (!JsonUtils.StatusOk(object)) {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 3;
                    handler.sendMessage(msg);
                    return;
                }
                JSONArray jsonArray = JsonUtils.GetJsonArrayParam(object);
                List<FormCreateListContentAdapter.MaterialSize> materialSize = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.i("material item", jsonObject.toString());
                    FormCreateListContentAdapter.MaterialSize materialSizeOne = new FormCreateListContentAdapter.MaterialSize(jsonObject.getString("id"),
                            jsonObject.getString("name"),
                            jsonObject.getString("unit"));
                    materialSize.add(materialSizeOne);
                }
                formCreateListContentAdapter.setMaterialSize(materialSize);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class GetPickingMaterial extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Message msg = handler.obtainMessage();
                JSONObject pickingContent = Conn.get(Router.MATERIAL_PICKING_DETAIL, new JSONObject().put("table_id", tableId));
                if (JsonUtils.StatusOk(pickingContent)) {
                    pickingContent= JsonUtils.GetJsonObj(pickingContent, "param");
                    if (!pickingContent.getString("status").equals("pass")) {
                        msg.arg1 = 4;
                        msg.obj = "Sorry, this form has not been accepted.";
                        handler.sendMessage(msg);
                        return;
                    }
                    JSONArray materialArray = JsonUtils.GetJsonArray(pickingContent, "material");
                    List<TableItem> items = new ArrayList<>();
                    for (int i = 0; i < materialArray.length(); i++) {
                        TableItem item = new TableItem();
                        JSONObject m = materialArray.getJSONObject(i);
                        item.setMaterial(m.getString("name"));
                        item.setSize(m.getString("unit"));
                        items.add(item);
                    }
                    msg.arg1 = 2;
                    msg.obj = items;
                } else {
                    msg.arg1 = 3;
                }
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
