package com.company.management;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class FormList extends AppCompatActivity {
    FloatingActionButton create;
    ListView form_list;
    int target_page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
//        初始化完成数据获取，得到相应的数据，并且从后台获得表单数据
        init();
        form_list = (ListView) findViewById(R.id.form_list);
        create = (FloatingActionButton) findViewById(R.id.form_list_create);
        if (!authenticate(""))
            create.setVisibility(View.INVISIBLE);
        create.setOnClickListener(new ChangeToCreatePage(target_page));
        setForm_list();
    }
    void init(){
//    TODO: 完成从后台获取数据
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
            intent.setClassName(packagename, packagename + '/' + target_page);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}
