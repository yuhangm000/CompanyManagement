package com.company.management;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class ListContentAdapter extends BaseAdapter {
//    上下文信息
    private Context context;
//    item的数据
    private List<String> mData; // 材料名称List
    private List<String> mSize; // 材料规格List
    private List<Integer> mNumber; // 材料数量List

    ViewHolder viewHolder = null;
//    自定义构造函数
    public ListContentAdapter(List<String> mData, List<String> mSize, List<Integer> mNumber){
        this.mData = mData;
        this.mSize = mSize;
        this.mNumber = mNumber;
    }
    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
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
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content,null);
            viewHolder = new ViewHolder();
            viewHolder.mName = (TextView) convertView.findViewById(R.id.list_content_material_name);
            viewHolder.mSize = (TextView) convertView.findViewById(R.id.list_content_size);
            viewHolder.mNumber = (EditText) convertView.findViewById(R.id.list_content_number);
            viewHolder.mNumber.addTextChangedListener(new NumberTextWather());
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
//        设置数据
        viewHolder.mName.setText(mData.get(position));
        viewHolder.mSize.setText(mSize.get(position));
        viewHolder.mNumber.setText(mNumber.get(position));
        return convertView;
    }
    static class ViewHolder {
        TextView mName;
        TextView mSize;
        EditText mNumber;
    }
    class NumberTextWather implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            viewHolder.mNumber.setText(s);
        }
    }
}
