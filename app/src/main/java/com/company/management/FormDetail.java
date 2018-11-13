package com.company.management;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class FormDetail extends AppCompatActivity {
    private Context context;
    private ListView listView;
    private TextView creator;
    private TextView createTime;
    private TextView status;

    private List<String> mData, mSize;
    private List<Integer> mNumber;

    String creator_title = null;
    String create_time_title = null;
    String status_title = null;
    String divider = " : ";

    String creator_info, create_time_info, status_info;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_form_detail);
        context = getApplicationContext();
        listView = (ListView) findViewById(R.id.material_list);
        creator = (TextView) findViewById(R.id.form_detail_creator);
        createTime = (TextView) findViewById(R.id.form_detail_time);
        status  = (TextView) findViewById(R.id.form_detail_status);
        creator_title = getString(R.string.creator);
        create_time_title = getString(R.string.time);
        status_title = getString(R.string.status);
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
