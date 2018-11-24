package com.company.management;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class FormListListViewContentAdapter extends BaseAdapter {
    // 列表项的内容
    private List<String> item_title;
    private List<String> item_abstract;
    private List<String> item_create_time;
//    上下文信息
    private Context context = null;

    ViewHolder viewHolder = null;
    public FormListListViewContentAdapter(List<String> item_title, List<String> item_abstract, List<String> item_create_time) {
        this.item_title = item_title;
        this.item_abstract = item_abstract;
        this.item_create_time = item_create_time;
//        this.context = context;
    }
    @Override
    public int getCount() {
        return item_title == null ? 0 : item_title.size();
    }

    @Override
    public Object getItem(int position) {
        return item_title.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (context == null) {
            context = parent.getContext();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.form_list_list_view_content,null);
            viewHolder = new ViewHolder();
            viewHolder.table_name = (TextView) convertView.findViewById(R.id.form_list_list_view_table_name);
            viewHolder.table_abstract = (TextView) convertView.findViewById(R.id.form_list_list_view_abstract);
            viewHolder.table_create_time = (TextView) convertView.findViewById(R.id.form_list_list_view_create_time);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.table_name.setText(item_title.get(position));
        viewHolder.table_abstract.setText(item_abstract.get(position));
        viewHolder.table_create_time.setText(item_create_time.get(position));
        viewHolder.table_name.setOnClickListener(new ListViewClickListener());
        return convertView;
    }
    static class ViewHolder {
        TextView table_name, table_abstract, table_create_time;
    }
    // TODO: 去除点击事件
    private class ListViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            String packagename = context.getPackageName();
            intent.setClassName(packagename, packagename+".FormDetail");
            PackageManager packageManager = context.getPackageManager();
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent);
            }
        }
    }
}
