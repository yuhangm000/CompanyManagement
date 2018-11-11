package com.company.management;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FormList extends AppCompatActivity {
    FloatingActionButton create;
    ListView form_list;
    String target_page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
        init();
        form_list = (ListView) findViewById(R.id.form_list);
        create = (FloatingActionButton) findViewById(R.id.create);
        if (!authenticate(""))
            create.setVisibility(View.INVISIBLE);
        create.setOnClickListener(new ChangeToCreatePage(target_page));
        setForm_list();
    }
    void init(){

//    TODO: 完成activity之间需要传递的数据确认
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        target_page = bundle.getString("target_page");
    }
//    TODO： 完善create按钮的显示判断作用
    Boolean authenticate(String user) {
        if (user == "admin")
            return true;
        return false;
    }
//    TODO: 完成数据填充
    void setForm_list() {

    }

//  TODO: 完成list的适配器
    class ChangeToCreatePage implements View.OnClickListener {
        String target_page;
        public ChangeToCreatePage(String target_page){
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
