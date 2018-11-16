package com.company.management;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private ArrayAdapter<String> arrayAdapterSize;
    private ArrayList<String> name_list;
    private ArrayList<String> size_list;
    private List<MaterialSize> materialSize;
    class MaterialSize {
        String material;
        String material_size;
        MaterialSize(String m, String mz) {
            material = m;
            material_size = mz;
        }
    }
    public FormCreateListContentAdapter() {
        material_name = new ArrayList<>();
        material_size = new ArrayList<>();
        material_number = new ArrayList<>();
        // TODO: 此处添加从后台获得材料及其规格的函数
        materialSize = new ArrayList<>();
        materialSize.add(new MaterialSize("yanhua","1"));
        materialSize.add(new MaterialSize("yanhua", "2"));
        materialSize.add(new MaterialSize("meng yuhang","3"));
        materialSize.add(new MaterialSize("meng yuhang", "4"));
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
        return material_name == null ? 0 : material_name.size();
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
        arrayAdapterName = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, name_list);
        arrayAdapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.name.setAdapter(arrayAdapterName);
        viewHolder.name.setOnItemSelectedListener(new SpinnerNameListenser());

        arrayAdapterSize = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, size_list);
        arrayAdapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.size.setAdapter(arrayAdapterName);
        return convertView;
    }
    // 此处获得spinner 的内容
    void initSpinnerContent() {
        name_list = new ArrayList<>();
        for (int i = 0; i < materialSize.size(); i++) {
            name_list.add(materialSize.get(i).material);
        }
        HashSet h = new HashSet(name_list);
        name_list.clear();
        name_list.addAll(h);
    }
    class SpinnerNameListenser implements AdapterView.OnItemSelectedListener {

        @SuppressLint("WrongConstant")
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            size_list = new ArrayList<>();
            for (int i = 0; i < materialSize.size(); i++) {
                if(materialSize.get(i).material.equals(name_list.get(position))) {
                    size_list.add(materialSize.get(i).material_size);
                }
            }
            arrayAdapterSize = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, size_list);
            arrayAdapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            viewHolder.size.setAdapter(arrayAdapterSize);
            Toast.makeText(context, name_list.get(position), 2000).show();
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
