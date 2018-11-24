package com.company.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageRemindListViewAdapter extends BaseAdapter {
    private List<String> message_title;
    private List<String> message_abstract;
    private List<String> message_time;
    private Context context = null;
    ViewHolder viewHolder = null;

    public MessageRemindListViewAdapter(List<String> message_title, List<String> message_abstract, List<String> message_time) {
        this.message_abstract = message_abstract;
        this.message_title = message_title;
        this.message_time = message_time;
    }
    @Override
    public int getCount() {
        return message_abstract == null ? 0 : message_abstract.size();
    }

    @Override
    public Object getItem(int position) {
        return message_title.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.message_remind_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.message_title = (TextView) convertView.findViewById(R.id.message_remind_list_message_title);
            viewHolder.message_abstract = (TextView) convertView.findViewById(R.id.message_remind_list_message_abstract);
            viewHolder.getMessage_time = (TextView) convertView.findViewById(R.id.message_remind_list_message_time);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.message_title.setText(message_title.get(position));
        viewHolder.message_abstract.setText(message_abstract.get(position));
        viewHolder.getMessage_time.setText(message_time.get(position));
        return convertView;
    }
    class ViewHolder {
        TextView message_title;
        TextView message_abstract;
        TextView getMessage_time;
    }
}
