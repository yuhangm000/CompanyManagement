package com.company.management;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObservable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FormCreateListContentAdapter extends BaseAdapter {
    private List<Spinner> material_name;
    private List<Spinner> material_size;
    private List<String> material_number;
    private Context context = null;
    private ViewHolder viewHolder;
    private ArrayAdapter<String> arrayAdapterName;
    private ArrayList<String> name_list;
    private ArrayList<String> size_list;
    private List<MaterialSize> materialSize;
    private int list_size;
    private int old_selection;
    class MaterialSize {
        String material;
        String material_size;
        MaterialSize(String m, String mz) {
            material = m;
            material_size = mz;
        }
    }
    private Handler  handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayAdapter<String> arrayAdapterSize = (ArrayAdapter<String>) msg.obj;
            arrayAdapterSize.clear();
            size_list.clear();
            for (int i = 0; i < materialSize.size(); i++) {
                if(materialSize.get(i).material.equals(name_list.get(old_selection))) {
//                    arrayAdapterSize.remove(materialSize.get(i).material_size);
                    size_list.add(materialSize.get(i).material_size);
                    arrayAdapterSize.add(materialSize.get(i).material_size);
                }
            }
            Log.i("in handle", String.valueOf(arrayAdapterSize.getCount()));
            arrayAdapterSize.notifyDataSetChanged();
//            arrayAdapterSize = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, size);
//            arrayAdapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            viewHolder.size.setAdapter(arrayAdapterSize);
        }
    };
    public FormCreateListContentAdapter() {
        material_name = new ArrayList<>();
        material_size = new ArrayList<>();
        material_number = new ArrayList<>();
        old_selection = 0;
        // TODO: 此处添加从后台获得材料及其规格的函数
        materialSize = new ArrayList<>();
        materialSize.add(new MaterialSize("yanhua","1"));
        materialSize.add(new MaterialSize("yanhua", "2"));
        materialSize.add(new MaterialSize("meng yuhang","3"));
        materialSize.add(new MaterialSize("meng yuhang", "4"));
        list_size = materialSize.size();
    }
    public FormCreateListContentAdapter(List<Spinner> name, List<Spinner> size, List<String> number) {
        this.material_name = name;
        this.material_size = size;
        this.material_number = number;
    }
    public void setMaterial_name(List<Spinner> material_name) {
        this.material_name = material_name;
    }

    public void setMaterial_size(List<Spinner> material_size) {
        this.material_size = material_size;
    }

    public void setMaterial_number(List<String> material_number) {
        this.material_number = material_number;
    }

    public void setMaterial_item(List<Spinner> material_name, List<Spinner> material_size, List<String> material_number) {
        this.material_name = material_name;
        this.material_size = material_size;
        this.material_number = material_number;
    }
    @Override
    public int getCount() {
        return list_size;
    }

    @Override
    public Object getItem(int position) {
        return material_name.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.form_create_list_content, null);
            viewHolder =  new ViewHolder();
            viewHolder.name = (Spinner) convertView.findViewById(R.id.form_create_list_content_material);
            viewHolder.size = (Spinner) convertView.findViewById(R.id.form_create_list_content_size);
            viewHolder.number = (EditText) convertView.findViewById(R.id.form_create_list_content_number);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.number.setText("0");
        initSpinnerContent();
        // 初始化材料名的adapter
        arrayAdapterName = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, name_list);
        arrayAdapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.name.setAdapter(arrayAdapterName);
        viewHolder.name.setSelection(old_selection);
        // 初始化材料规格的adapter
        final ArrayAdapter<String> arrayAdapterSize = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, size_list);
        arrayAdapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.size.setAdapter(arrayAdapterSize);
        // 设置监听器
        viewHolder.name.setOnItemSelectedListener(new SpinnerNameListener(arrayAdapterSize));
        // 设置编辑框监听器

        return convertView;
    }
    // 此处获得spinner 的内容
    void initSpinnerContent() {
        name_list = new ArrayList<>();
        size_list = new ArrayList<>();
        for (int i = 0; i < materialSize.size(); i++) {
            name_list.add(materialSize.get(i).material);
        }
        HashSet h = new HashSet(name_list);
        name_list.clear();
        name_list.addAll(h);
        size_list.clear();
        for (int i = 0; i < materialSize.size(); i++) {
            if (name_list.get(old_selection).equals(materialSize.get(i).material)) {
                size_list.add(materialSize.get(i).material_size);
            }
        }
    }
    class SpinnerNameListener implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<String> arrayAdapter;
        public SpinnerNameListener(ArrayAdapter<String> arrayAdapter) {
            this.arrayAdapter = arrayAdapter;
        }
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            size_list.clear();
            Message msg = handler.obtainMessage();
            old_selection = position;
            msg.obj = arrayAdapter;
            handler.sendMessage(msg);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class ViewHolder{
        Spinner name;
        Spinner size;
        EditText number;
    }
}
