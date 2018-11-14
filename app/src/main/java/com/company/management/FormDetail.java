package com.company.management;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FormDetail extends AppCompatActivity {
    private Context context;
    private ListView listView;
    private TextView creator;
    private TextView createTime;
    private TextView status;

    private List<String> mData = new ArrayList<>(), mSize = new ArrayList<>();
    private List<Integer> mNumber = new ArrayList<>();

    String creator_title = null;
    String create_time_title = null;
    String status_title = null;
    String divider = " : ";

    String creator_info, create_time_info, status_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_detail);
        context = getApplicationContext();
        listView = (ListView) findViewById(R.id.material_list);
        creator = (TextView) findViewById(R.id.form_detail_creator);
        createTime = (TextView) findViewById(R.id.form_detail_time);
        status  = (TextView) findViewById(R.id.form_detail_status);
        creator_title = getString(R.string.form_detail_creator);
        create_time_title = getString(R.string.form_detail_time);
        status_title = getString(R.string.form_detail_status);
        // TODO: 替换成相应的*_info
        creator.setText(creator_title + divider + "yanhua");
        createTime.setText(create_time_title + divider + "2018.11.11 00:00:00");
        status.setText(status_title + divider + "完成");

        // TODO: 需要完成获取
        mData.add("1");
        mSize.add("20*20mm");
        mNumber.add(0);

        ListContentAdapter lca = new ListContentAdapter(mData, mSize, mNumber);
        listView.setAdapter(lca);
    }
}
