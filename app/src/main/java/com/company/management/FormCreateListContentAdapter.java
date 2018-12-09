package com.company.management;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

// TODO: 数据刷新问题
// TODO: 修改适配器
public class FormCreateListContentAdapter extends BaseAdapter {
    private List<TableItem> items;
    private Context context = null;
    private ViewHolder viewHolder;
    private ArrayList<String> name_list; // 候选的所有材料名字
    private ArrayList<String> size_list; // 候选的所有规格
    private List<MaterialSize> materialSize; // 所有材料的名字和规格信息

    public List<String> getCorrespondingSize(String name) {
        List<String> answer = new ArrayList<>();
        for (int i = 0; i < materialSize.size(); i++) {
            if (materialSize.get(i).material.equals(name)) {
                answer.add(materialSize.get(i).material_size);
            }
        }
        return answer;
    }
    private Handler  handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public FormCreateListContentAdapter(Context context, List<TableItem> items) {
        this.context = context;
        this.items = items;
        materialSize = new ArrayList<>();
        name_list = new ArrayList<>();
        size_list = new ArrayList<>();
        initMaterilaSizeList();
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    public void addItem(List<TableItem> items) {
//        items.add(new TableItem());
//        List<TableItem> new_items = items;
////        new_items.add(new TableItem());
//        items.clear();
//        items.addAll(new_items);
        this.items = items;
        notifyDataSetChanged();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (context == null) {
            context = parent.getContext();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.form_create_list_content, null);
            viewHolder =  new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.form_create_list_content_show_material);
            viewHolder.size = (TextView) convertView.findViewById(R.id.form_create_list_content_show_size);
            viewHolder.number = (TextView) convertView.findViewById(R.id.form_create_list_content_show_number);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.number.setText(items.get(position).number);
        viewHolder.name.setText(items.get(position).material);
        viewHolder.size.setText(items.get(position).size);
        // 初始化材料名的adapter
        viewHolder.name.setOnClickListener(new OnClickListener(position));
        // 初始化材料规格的adapter
        viewHolder.size.setOnClickListener(new View.OnClickListener() {
            String selected = null;
            @Override
            public void onClick(View v) {
                String material_name = viewHolder.name.getText().toString();
                size_list = (ArrayList<String>) getCorrespondingSize(material_name);
            }
        });
        // 设置编辑框监听器
        return convertView;
    }
    public void initMaterilaSizeList() {
        materialSize.clear();
        materialSize.add(new MaterialSize("yanhua", "1"));
        materialSize.add(new MaterialSize("yanhua", "2"));
        materialSize.add(new MaterialSize("yanhua", "3"));
        materialSize.add(new MaterialSize("meng", "4"));
        materialSize.add(4, new MaterialSize("meng", "5"));
        name_list.add("yanhua");
        name_list.add("meng");

    }
    // 设置可选内容
    public void setMaterialSize(List<MaterialSize> ms) {
        this.materialSize = ms;
    }
    class ViewHolder{
        TextView name;
        TextView size;
        TextView number;
    }
    public class MaterialSize {
        String material;
        String material_size;
        MaterialSize(String m, String mz) {
            material = m;
            material_size = mz;
        }
    }
    class OnClickListener implements View.OnClickListener {
        String selected = null;
        int item_position;
        public OnClickListener(int item_position) {
            this.item_position = item_position;
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            try {
                final ListView lv = new ListView(context);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, name_list);
                lv.setAdapter(arrayAdapter);
                final PopupWindow popupWindow = new PopupWindow(lv, 400, 500);
                popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(1, 147,151,147)));
                lv.setBackgroundColor(context.getColor(R.color.lightGray));
                final TextView text = (TextView) v.findViewById(R.id.form_create_list_content_show_material);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected = name_list.get(position);
                        text.setText(selected);

                        popupWindow.dismiss();
                    }
                });
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(v, 1, 50, 50);
            } catch (Exception e) {
                Log.e("error in create onclick", e.getMessage());
            }
        }
    }
}
