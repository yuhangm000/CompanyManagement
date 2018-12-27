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
    private List<FormList.ListItem> forms;
//    上下文信息
    private Context context = null;

    ViewHolder viewHolder = null;
    public FormListListViewContentAdapter(List<FormList.ListItem> forms) {
        this.forms = forms;
    }
    @Override
    public int getCount() {
        return forms == null ? 0 : forms.size();
    }

    @Override
    public Object getItem(int position) {
        return forms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(List<FormList.ListItem> newForms) {
        this.forms = newForms;
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (context == null) {
            context = parent.getContext();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.form_list_list_view_content,null);
            viewHolder = new ViewHolder();
            viewHolder.table_id = (TextView) convertView.findViewById(R.id.form_list_list_view_table_id);
            viewHolder.table_name = (TextView) convertView.findViewById(R.id.form_list_list_view_table_name);
            viewHolder.table_creator = (TextView) convertView.findViewById(R.id.form_list_list_view_creator);
            viewHolder.table_create_time = (TextView) convertView.findViewById(R.id.form_list_list_view_create_time);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        if(getCount() != 0)
        {
            viewHolder.table_id.setText(forms.get(position).id);
            viewHolder.table_name.setText(forms.get(position).title);
            viewHolder.table_creator.setText(forms.get(position).creator);
            viewHolder.table_create_time.setText(forms.get(position).createTime);
        }
        return convertView;
    }
    static class ViewHolder {
        TextView table_id,table_name, table_creator, table_create_time;
    }
}
