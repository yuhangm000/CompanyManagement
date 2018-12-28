package com.company.management;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    public void setItems(List<TableItem> items) {
        this.items = items;
        notifyDataSetChanged();
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
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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
            viewHolder.delete = (ImageButton) convertView.findViewById(R.id.form_create_list_content_delete);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.number.setText(items.get(position).number);
        viewHolder.name.setText(items.get(position).material);
        viewHolder.size.setText(items.get(position).size);
        // 初始化材料名的adapter
        viewHolder.name.setOnClickListener(new OnClickListenerForName(position));
        // 初始化材料规格的adapter
        viewHolder.size.setOnClickListener(new OnClickListenerForSize(position));
        // 设置材料数量的adapter
        viewHolder.number.setOnClickListener(new OnClickListenerForNum(position));
        viewHolder.delete.setOnClickListener(new OnClickListernerForDelete(position));
        return convertView;
    }
    // 设置可选内容
    public void setMaterialSize(List<MaterialSize> ms) {
        this.materialSize = ms;
        for (int i = 0; i < materialSize.size(); i++) {
            name_list.add(materialSize.get(i).material);
        }
        HashSet temp = new HashSet(name_list);
        name_list.clear();;
        name_list.addAll(temp);
    }
    public static class MaterialSize {
        String id;
        String material;
        String material_size;
        MaterialSize(String id, String m, String mz) {
            this.id = id;
            material = m;
            material_size = mz;
        }
    }
    public String getId(String name, String size) {
        for (int i = 0; i < materialSize.size(); i++) {
            if (materialSize.get(i).material.equals(name) && materialSize.get(i).material_size.equals(size)){
                return materialSize.get(i).id;
            }
        }
        return "-1";
    }
    public String getId(int position) {
        return getId(name_list.get(position), size_list.get(position));
    }
    class OnClickListenerForName implements View.OnClickListener {
        String selected = null;
        int item_position;
        public OnClickListenerForName(int item_position) {
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
                        items.get(item_position).setMaterial(selected);
                    }
                });
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(v, 1, 50, 50);
            } catch (Exception e) {
                Log.e("error in create onclick", e.getMessage());
            }
        }
    }
    class OnClickListenerForSize implements View.OnClickListener {
        String selected = null;
        int item_position;
        public OnClickListenerForSize(int item_position) {
            this.item_position = item_position;
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            String material_name = items.get(item_position).getMaterial();
            if (material_name == null || material_name.equals("点击选择")) {
                Toast.makeText(context, "请先选择材料名称", 1000).show();
                return;
            }
            size_list = (ArrayList<String>) getCorrespondingSize(material_name);
            final ListView lv = new ListView(context);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, size_list);
            lv.setAdapter(arrayAdapter);
            final PopupWindow popupWindow = new PopupWindow(lv, 400, 500);
            popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(1, 147,151,147)));
            lv.setBackgroundColor(context.getColor(R.color.lightGray));
            final TextView text = (TextView) v.findViewById(R.id.form_create_list_content_show_size);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selected = size_list.get(position);
                    text.setText(selected);
                    popupWindow.dismiss();
                    items.get(item_position).setSize(selected);
                }
            });
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(v, 1, 50, 50);
        }
    }
    class OnClickListenerForNum implements View.OnClickListener{
        int item_position;
        public OnClickListenerForNum(int item_position) {
            this.item_position = item_position;
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            String name = items.get(item_position).getMaterial();
            String size = items.get(item_position).getSize();
            if (name == null || name.equals("点击选择")) {
                Toast.makeText(context, "请先选择材料名称", 1000).show();
                return;
            }
            if (size == null  || size.equals("点击选择")) {
                Toast.makeText(context, "请先选择材料规格", 1000).show();
                return;
            }
            final EditText et = new EditText(context);
            final PopupWindow popupWindow = new PopupWindow(et, 700, 200);
            popupWindow.setFocusable(true);
            et.setBackgroundColor(context.getColor(R.color.lightGray));
            final TextView text = (TextView) v.findViewById(R.id.form_create_list_content_show_number);
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    text.setText(s.toString());
                    items.get(item_position).setNumber(text.getText().toString());
                }
            });
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(v, 1, 50, 50);
        }
    }
    class OnClickListernerForDelete implements View.OnClickListener{

        int item_position;
        public OnClickListernerForDelete(int item_position) {
            this.item_position = item_position;
        }
        @Override
        public void onClick(View v) {
            items.remove(item_position);
            notifyDataSetChanged();
        }
    }
    class ViewHolder{
        TextView name;
        TextView size;
        TextView number;
        ImageButton delete;
    }
}
