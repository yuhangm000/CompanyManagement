package com.company.management;

import android.content.Intent;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
     * 常量区
     */
    private final String permissionForward = "CREATE_TABLE_";
    /**
     * 变量区域
     */
    private ACL acl;
    int target_page;

    FloatingActionButton create;
    ListView form_list;
    private String username;
    private String permissionBackward;
    private Map<Integer, String> pageMap;

    private List<String> item_id = new ArrayList<>();
    private List<String> item_title = new ArrayList<>();
    private List<String> item_abstract = new ArrayList<>();
    private List<String> item_create_time = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
//        初始化完成数据获取，得到相应的数据，并且从后台获得表单数据
        init();
        /**
         * 如果没有权限则不展示创建按钮
         */
        if (!acl.hasPermission(username, permissionForward+permissionBackward) && false)
            create.setVisibility(View.INVISIBLE);
        else
            create.setOnClickListener(new ChangeToCreatePage(target_page));
        /**
         * 设置ListView的点击监听事件
         */
        form_list.setOnItemClickListener(new ItemClickListener());
        setForm_list();
    }
    void init(){
        acl = (ACL) getApplicationContext();
        /**
         * 初始化，映射参数
         */
        pageMap = new HashMap<>();
        pageMap.put(R.string.material_apply, "apply");
        pageMap.put(R.string.material_turn_back, "turn_back");
        pageMap.put(R.string.get_matrial, "receive");
        pageMap.put(R.string.material_in_warehouse, "in_warehouse");
        /**
         * 初始化页面控件
         */
        form_list = (ListView) findViewById(R.id.form_list);
        create = (FloatingActionButton) findViewById(R.id.form_list_create);
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            target_page = bundle.getInt("target_page");
            UserWR userWR = new UserWR();
            username = userWR.getUserName(getApplicationContext());
            permissionBackward = pageMap.get(target_page);
        } catch (Exception e) {
            Log.e("FORM_LIST_INIT",e.getMessage());
        }
        getDataFromBackward(target_page);

    }
    public void getDataFromBackward(int target_page) {
        /**
         * TODO： 联通后端api后需要取消注释
         */
//        new GetData(target_page).run();
    }
//    TODO: 完成数据填充， 删除无用代码
    void setForm_list() {
        String text = "test";
        item_id.add(text);
        item_title.add(text);
        item_abstract.add(text);
        item_create_time.add(text);
        FormListListViewContentAdapter formListListViewContentAdapter;
        formListListViewContentAdapter= new FormListListViewContentAdapter(item_id,
                item_title,
                item_abstract,
                item_create_time);
        form_list.setAdapter(formListListViewContentAdapter);
    }

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
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("originalPage", this.target_page);
            intent.putExtras(bundle);
            String packagename = getPackageName();
            intent.setClassName(packagename, packagename + ".FormCreate" );
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
    class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            // TODO: 添加item_id 一列
            bundle.putString("tableId", item_id.get(position));
//            bundle.putString("username", username);
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
        @Override
        public void run() {
            try {
                // TODO： 联通后端api获取内容, 使用handler来更新界面
                Conn.get("/getFormList", pageMap.get(this.target_page));
                setForm_list();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }
}
