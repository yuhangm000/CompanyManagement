package com.company.management;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1. this page show the table list.
 * 2. check out whether has the permission to create with username, pagename.
 */
public class FormList extends AppCompatActivity {
    /**
     * 控件区域
     */
    FloatingActionButton create;
    ListView form_list;
    private PopupMenu popupMenu;
    /**
     * 常量区
     */
    private final String permissionForward = "CREATE_TABLE_";
    /**
     * 变量区域
     */
    private ACL acl;
    int target_page;
    private List<String> item_id = new ArrayList<>();
    private List<String> item_title = new ArrayList<>();
    private List<String> item_creator = new ArrayList<>();
    private List<String> item_create_time = new ArrayList<>();
    private String username;
    private String permissionBackward;
    private Map<Integer, String> pageMap;
    private Map<Integer, List<String>> popMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
        //  初始化完成数据获取，得到相应的数据，并且从后台获得表单数据
        init();
        /**
         * 如果没有权限则不展示创建按钮
         */
        if (target_page == R.string.material_information || !acl.hasPermission(username, permissionForward+permissionBackward) && false)
            create.setVisibility(View.INVISIBLE);
        else
            create.setOnClickListener(new ChangeToCreatePage(target_page));
        /**
         * 设置ListView的点击监听事件
         */
        form_list.setOnItemClickListener(new ItemClickListener());
        /**
         * TODO: @meng 此函数填充表格内容,与后端链接完成后删掉
         */
        setForm_list(null);
    }
    @SuppressLint("WrongConstant")
    void init(){
        acl = (ACL) getApplicationContext();
        /**
         * 初始化，映射参数
         */
        pageMap = new HashMap<>();
        pageMap.put(R.string.material_information, "information");
        pageMap.put(R.string.material_turn_back, "turn_back");
        pageMap.put(R.string.material_out_warehouse, "out_warehouse");
        pageMap.put(R.string.material_in_warehouse, "in_warehouse");
        pageMap.put(R.string.material_purchase_apply, "purchase_apply");

        popMenuItem = new HashMap<>();
        List<String> createOption = new ArrayList<>();
        createOption.add("物料采购申请表");
        popMenuItem.put(R.string.material_purchase_apply, createOption);
        List<String> createOption1 = new ArrayList<>();
        createOption1.add("出库单");
        popMenuItem.put(R.string.material_out_warehouse, createOption1);
        List<String> createOption2 = new ArrayList<>();
        createOption2.add("入库单");
        popMenuItem.put(R.string.material_in_warehouse, createOption2);
        List<String> createOption3 = new ArrayList<>();
        createOption3.add("结余材料申请表");
        createOption3.add("废旧材料登记表");
        createOption3.add("单站工程材料考核表");
        popMenuItem.put(R.string.material_turn_back, createOption3);
        List<String> createOption4 = new ArrayList<>();
        createOption4.add("领料单");
        popMenuItem.put(R.string.material_picking, createOption4);
        Log.i("popItems", popMenuItem.toString());
        /**
         * 初始化页面控件
         */
        form_list = (ListView) findViewById(R.id.form_list);
        create = (FloatingActionButton) findViewById(R.id.form_list_create);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            target_page = bundle.getInt("target_page");
            Log.i("popPages", String.valueOf(target_page));
            UserWR userWR = new UserWR();
            username = userWR.getUserName(getApplicationContext());
            permissionBackward = pageMap.get(target_page);
        } catch (Exception e) {
            Log.e("FORM_LIST_INIT",e.getMessage());
        }
        try {
            getDataFromBackward(target_page);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "服务器休息了，请稍后重试", 1000).show();
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".BottomNavigation");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
    public void getDataFromBackward(int target_page) {
        /**
         * TODO： 联通后端api后需要取消注释
         */
        new GetData(target_page).start();
    }

    void setForm_list(JSONObject jsonObject) {
        try {
            /**
             * TODO: 测试并且完善从后端取数据 @meng
             */
            JSONArray jsonArray = null;
            if (jsonObject != null)
                jsonArray = jsonObject.getJSONArray("param");
            if (jsonArray != null) {
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    item_id.add(jObject.getString("table_id"));
                    if (jsonObject.getString("table_name") != null)
                        item_title.add(jObject.getString("table_name"));
                    else
                        item_title.add(jObject.getString("table_id"));
                    item_creator.add(jObject.getString("creator"));
                    item_create_time.add(jObject.getString("create_time"));
                }
            }
            else {
                String text = "test";
                item_id.add(text);
                item_title.add(text);
                item_creator.add(text);
                item_create_time.add(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FormListListViewContentAdapter formListListViewContentAdapter;
        formListListViewContentAdapter= new FormListListViewContentAdapter(
                item_id,
                item_title,
                item_creator,
                item_create_time
        );
        form_list.setAdapter(formListListViewContentAdapter);
    }
    /**
     * 子线程更新界面
     */
    Handler handler = new Handler() {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Toast.makeText(getApplicationContext(), "服务器开小差了~", 1000).show();
                return;
            }
            JSONObject jsonObject = (JSONObject) msg.obj;
            try {
                if (jsonObject.getInt("status") == 1) {
                    /**
                     * 此处才是真正填充表格的地方 TODO:@meng
                     */
                    setForm_list(jsonObject);
                } else {
                    Toast.makeText(getApplicationContext(), "获取列表错误", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * 类声明区域
     */
//    create 按钮的跳转相应事件
    class ChangeToCreatePage implements View.OnClickListener {
        int target_page;
        public ChangeToCreatePage(int target_page){
            this.target_page = target_page;
        }
        @Override
        public void onClick(View v) {
            popupMenu = new PopupMenu(getApplicationContext(), v);
            Menu menu = popupMenu.getMenu();
            List<String> menuOption = popMenuItem.get(target_page);
            for (int i = 0; menuOption != null && i < menuOption.size(); i++) {
                menu.add(0, i, i, menuOption.get(i));
            }
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("form-title", (String) item.getTitle());
                    intent.putExtras(bundle);
                    String packagename = getPackageName();
                    intent.setClassName(packagename, packagename + ".FormCreate" );
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    return false;
                }
            });

        }
    }
    class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("tableId", item_id.get(position));
            bundle.putString("username", "yanhua");
            bundle.putInt("originalPage", target_page);
            intent.putExtras(bundle);
            String packageName = getPackageName();
            intent.setClassName(packageName, packageName + ".FormDetail");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
    class GetData extends Thread{
        int target_page;
        public GetData(int target_page) {
            this.target_page = target_page;
        }
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            try {
                /**
                 * TODO： 完善router @meng
                 */
                JSONObject json = null;
                String routes = getRoutes(target_page);
                if (routes != null) {
                    UserWR userWR = new UserWR();
                    String userId = userWR.getUserID(getApplicationContext());
                    /**
                     * 此处json应该是如下形式
                     * {
                     * ...
                     * param: {
                     * table_id:
                     * creator:
                     * createt_time:
                     * ...
                     * }
                     */
                    json = Conn.get(routes, userId);
                    if (json != null) {
                        Log.i("GET TABLES", json.toString());
                        Message message = handler.obtainMessage();
                        message.obj = json.getJSONObject("param");
                        handler.sendMessage(message);
                    } else {
                        Message message = handler.obtainMessage();
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                } else {
                    Log.e("FormList","Illegel params.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private String getRoutes(int page) {
            switch (page) {
                case R.string.material_in_warehouse:
                    return Router.MATERIAL_IN_WAREHOUSE_LIST;
                case R.string.material_out_warehouse:
                    return Router.MATERIAL_OUT_WAREHOUSE_LIST;
                case R.string.material_picking:
                    return Router.MATERIAL_PICKING_LIST;
                case R.string.material_turn_back:
                    return Router.MATERIAL_RETURN_LIST;
                case R.string.material_purchase_apply:
                    return Router.MATERIAL_PURCHASE_APPLY_LIST;
                default:
                    return null;
            }
        }
    }
}
