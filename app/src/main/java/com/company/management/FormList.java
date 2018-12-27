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
import android.widget.EditText;
import android.widget.ImageButton;
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
    private EditText queryProjectName;
    private EditText queryCreator;
    private ImageButton query;
    /**
     * 常量区
     */
    private final String permissionForward = "CREATE_TABLE_";
    private final String MATERIAL_PURCHASE_FORM = "物料采购申请表";
    private final String OUT_WAREHOUSE_FORM = "出库单";
    private final String IN_WAREHOUSE_FORM = "入库单";
    private final String LEFT_MATERIAL_FORM = "结余材料申请表";
    private final String USELESS_OR_OLD_MATERIAL_FORM = "废旧材料登记表";
    private final String SINGLETON_PROJECT_CHECK_FORM = "单站工程材料考核表";
    private final String MATERIAL_PICKING_FORM = "领料单";
    /**
     * 变量区域
     */
    private ACL acl;
    int target_page;
    private List<ListItem> forms;
    private List<ListItem> formsInspector;
    private String username;
    private String permissionBackward;
    private Map<Integer, String> pageMap;
    private Map<Integer, List<String>> popMenuItem;
    private Map<String, String> createPermission;
    private FormListListViewContentAdapter formListListViewContentAdapter;
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
//        queryProjectName.setOnClickListener(new ProjectNameSelected());
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formsInspector =  filterForms();
                Message msg = handler.obtainMessage();
                msg.arg1 = 4;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 私有方法区域
     */
    @SuppressLint("WrongConstant")
    private void init(){
        acl = (ACL) getApplicationContext();
        forms = new ArrayList<>();
        /**
         * 初始化，映射参数
         */
        pageMap = new HashMap<>();
        pageMap.put(R.string.material_information, "information");
        pageMap.put(R.string.material_turn_back, "turn_back");
        pageMap.put(R.string.material_out_warehouse, "out_warehouse");
        pageMap.put(R.string.material_in_warehouse, "in_warehouse");
        pageMap.put(R.string.material_purchase_apply, "purchase_apply");
        /**
         * 初始化新建按钮
         */
        popMenuItem = new HashMap<>();
        List<String> createOption = new ArrayList<>();
        createOption.add(MATERIAL_PURCHASE_FORM);
        popMenuItem.put(R.string.material_purchase_apply, createOption);
        List<String> createOption1 = new ArrayList<>();
        createOption1.add(OUT_WAREHOUSE_FORM);
        popMenuItem.put(R.string.material_out_warehouse, createOption1);
        List<String> createOption2 = new ArrayList<>();
        createOption2.add(IN_WAREHOUSE_FORM);
        popMenuItem.put(R.string.material_in_warehouse, createOption2);
        List<String> createOption3 = new ArrayList<>();
        createOption3.add(LEFT_MATERIAL_FORM);
        createOption3.add(USELESS_OR_OLD_MATERIAL_FORM);
        createOption3.add(SINGLETON_PROJECT_CHECK_FORM);
        popMenuItem.put(R.string.material_turn_back, createOption3);
        List<String> createOption4 = new ArrayList<>();
        createOption4.add(MATERIAL_PICKING_FORM);
        popMenuItem.put(R.string.material_picking, createOption4);
        Log.i("popItems", popMenuItem.toString());
        /**
         * 初始化创建权限映射
         */
        createPermission = new HashMap<>();
        createPermission.put(MATERIAL_PICKING_FORM, "material-get-form-create");
        createPermission.put(MATERIAL_PURCHASE_FORM, "material-purchase-form-create");
        createPermission.put(LEFT_MATERIAL_FORM, "material-return-form-create");
        createPermission.put(SINGLETON_PROJECT_CHECK_FORM, "project-check-form-create");
        createPermission.put(USELESS_OR_OLD_MATERIAL_FORM, "material-useless-form-create");
        createPermission.put(IN_WAREHOUSE_FORM, "material-in-warehouse-form-create");
        createPermission.put(OUT_WAREHOUSE_FORM, "material-out-warehouse-form-create");
        /**
         * 初始化页面控件
         */
        form_list = (ListView) findViewById(R.id.form_list);
        create = (FloatingActionButton) findViewById(R.id.form_list_create);
        queryProjectName = (EditText) findViewById(R.id.queryProjectName);
        queryCreator = (EditText) findViewById(R.id.queryCreator);
        query = (ImageButton) findViewById(R.id.verifyQuery);
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
    private void getDataFromBackward(int target_page) {
        /**
         * TODO： 联通后端api后需要取消注释
         */
        new GetData(target_page).start();
    }

    private void setForm_list(JSONArray jsonArray) {
        try {
            if (jsonArray != null) {
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    ListItem listItem = new ListItem();
                    listItem.id = jObject.getString("table_id");
                    if (jObject.has("table_name"))
                        listItem.title = jObject.getString("table_name");
                    else
                        listItem.title = jObject.getString("table_id");
                    listItem.creator = jObject.getString("writer");
                    listItem.createTime = jObject.getString("create_time");
                    forms.add(listItem);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        formsInspector = filterForms();
        formListListViewContentAdapter= new FormListListViewContentAdapter(formsInspector);
        form_list.setAdapter(formListListViewContentAdapter);
    }
    private List<ListItem> filterForms() {
        final String projectName = queryProjectName.getText().toString().trim().toLowerCase();
        final String creator = queryCreator.getText().toString().trim().toLowerCase();
        List<ListItem> items = new ArrayList<>();
        for (int i = 0; i < forms.size(); i++) {
            final ListItem item = forms.get(i);
            String item_title = item.title.toLowerCase().trim();
            String item_creator = item.creator.toLowerCase().trim();
            if (item_title.equals(projectName) && item_creator.equals(creator)) {
                items.add(item);
            }
            else if (item_title.equals(projectName)) {
                items.add(item);
            }
            else if (item_creator.equals(creator)){
                items.add(item);
            }
        }
        Log.i("querySize", String.valueOf(items.size()));
        if (items.size() == 0 &&
                projectName.equals("") &&
                creator.equals("")) {
            return forms;
        }
        return items;
    }
    /**
     * 子线程更新界面
     */
    Handler handler = new Handler() {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 3) {
                Toast.makeText(getApplicationContext(), "服务器开小差了~", 1000).show();
                return;
            } else if (msg.arg1 == 2) {
                Toast.makeText(getApplicationContext(), (String)msg.obj, 1000).show();
                return;
            } else if (msg.arg1 == 4) {
                Log.i("formInspector", String.valueOf(formsInspector.size()));
                formListListViewContentAdapter.notifyDataSetChanged(formsInspector);
                return;
            }
            JSONArray array = (JSONArray) msg.obj;
            setForm_list(array);
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
                    if (!acl.hasPermission(username, createPermission.get((String) item.getTitle()))) {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 2;
                        msg.obj = "Sorry, You do not have access to do this operation";
                        handler.sendMessage(msg);
                        return false;
                    }
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
            bundle.putInt("tableId", Integer.valueOf(formsInspector.get(position).id));
            bundle.putInt("originalPage", target_page);
            String packageName = getPackageName();
            if (target_page == R.string.material_turn_back) {
                if (!username.equals(formsInspector.get(position).creator)) {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 2;
                    msg.obj = "Sorry, you do not have access to do this operation.";
                    handler.sendMessage(msg);
                    return;
                }
                bundle.putString("form-title", "还料表");
                intent.setClassName(packageName, packageName + ".FormCreate");
            } else {
                intent.setClassName(packageName, packageName + ".FormDetail");
            }
            intent.putExtras(bundle);
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
                JSONObject json = null;
                String routes = getRoutes(target_page);
                if (routes != null) {
                    UserWR userWR = new UserWR();
                    String userId = userWR.getUserID(getApplicationContext());
                    json = Conn.get(routes, null);
                    if (json != null) {
                        Log.i("GET TABLES", json.toString());
                        Message message = handler.obtainMessage();
                        if (!JsonUtils.StatusOk(json)){
                            message.arg1 = 3;
                        } else {
                            message.obj = JsonUtils.GetJsonArrayParam(json);
                        }
                        handler.sendMessage(message);
                    } else {
                        Message message = handler.obtainMessage();
                        message.arg1 = 3;
                        handler.sendMessage(message);
                    }
                } else {
                    Log.e("FormList","Illegel params.");
                }
            } catch (IOException e) {
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
                    return Router.MATERIAL_PICKING_LIST;
                case R.string.material_purchase_apply:
                    return Router.MATERIAL_PURCHASE_APPLY_LIST;
                default:
                    return null;
            }
        }
    }
    class ProjectNameSelected implements View.OnClickListener{

        @Override
        public void onClick(View v) {

        }
    }
    public class ListItem {
        String id;
        String creator;
        String createTime;
        String title;
    }
}
