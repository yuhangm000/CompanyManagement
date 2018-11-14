package com.company.management;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FormList extends AppCompatActivity {
    FloatingActionButton create;
    ListView form_list;
    int target_page;

    List<String> item_title = new ArrayList<>(), item_abstract = new ArrayList<>(), item_create_time = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
//        初始化完成数据获取，得到相应的数据，并且从后台获得表单数据
        init();
//        TODO: 完成展示功能
        if (!authenticate(""))
            create.setVisibility(View.INVISIBLE);
        create.setOnClickListener(new ChangeToCreatePage(target_page));
        setForm_list();
    }
    void init(){
        form_list = (ListView) findViewById(R.id.form_list);
        create = (FloatingActionButton) findViewById(R.id.form_list_create);
//    TODO: 完成从后台获取数据
        String text = "test";
        item_title.add(text);
        item_abstract.add(text);
        item_create_time.add(text);
        FormListListViewContentAdapter formListListViewContentAdapter = new FormListListViewContentAdapter(item_title, item_abstract, item_create_time);
        form_list.setAdapter(formListListViewContentAdapter);
//    TODO: 完成activity之间需要传递的数据确认
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            target_page = bundle.getInt("target_page");
        } catch (Exception e) {
            Log.e("FORM_LIST_INIT",e.getMessage());
        }
    }
//    TODO： 完善create按钮的显示判断作用
    Boolean authenticate(String user) {
//        TODO: 完成之后这个地方需要去除
        return true;
//        if (user == "admin")
//            return true;
//        return false;
    }
//    TODO: 完成数据填充
    void setForm_list() {

    }

//    Description: 完成跳转的界面
//  TODO: 完成list的适配器
    class ChangeToCreatePage implements View.OnClickListener {
        int target_page;
        public ChangeToCreatePage(int target_page){
            this.target_page = target_page;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
    String packagename = getPackageName();
            intent.setClassName(packagename, packagename + ".FormCreate" );
            if (intent.resolveActivity(getPackageManager()) != null) {
        startActivity(intent);
    }
}
    }
}
