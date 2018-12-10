package com.company.management;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
                for (int i = 0; i < size; i++) {
                    TableItem item = (TableItem) formCreateListContentAdapter.getItem(i);
                    View view = material_list.getChildAt(i);
                    view.findViewById(R.id.form_create_list_content_show_material).setEnabled(false);
                    view.findViewById(R.id.form_create_list_content_show_size).setEnabled(false);
                    view.findViewById(R.id.form_create_list_content_show_number).setEnabled(false);
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
}
    /**
     * 异步函数区域
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            formCreateListContentAdapter.notifyDataSetChanged();
        }
    };
}
