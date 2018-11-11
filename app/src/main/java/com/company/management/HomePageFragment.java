package com.company.management;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.*;
/**
 * Created by 24448 on 2018/6/12.
 */
// TODO: 完成设置title
public class HomePageFragment extends Fragment {
    ImageView material_apply, in_warehouse, material_turn_back, get_material;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        material_apply = (ImageView) view.findViewById(R.id.material_apply);
        in_warehouse = (ImageView) view.findViewById(R.id.material_in_warehouse);
        material_turn_back = (ImageView) view.findViewById(R.id.material_turn_back);
        get_material = (ImageView) view.findViewById(R.id.meterial_get);
        material_apply.setOnClickListener(new ChangeToFormList(R.string.material_apply));
        in_warehouse.setOnClickListener(new ChangeToFormList(R.string.material_in_warehouse));
        material_turn_back.setOnClickListener(new ChangeToFormList(R.string.material_turn_back));
        get_material.setOnClickListener(new ChangeToFormList(R.string.get_matrial));
    }
//    TODO：需要进行测试
    class ChangeToFormList implements View.OnClickListener {
        int  target;
        String username;
        public ChangeToFormList(int target) {
            this.target = target;
            UserInfoSaveAndRead userinfo = new UserInfoSaveAndRead();
            username = userinfo.getUserInfo(getActivity().getApplicationContext());
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("page", target);
            bundle.putString("user", username);
            intent.putExtras(bundle);
            intent.setClassName(getActivity().getPackageName(),
                    getActivity().getPackageName() + ".FormList");
            PackageManager packageManager = getActivity().getPackageManager();
            try{
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                }
            }
            catch (Exception e){
                Log.e("tag",e.getMessage().toString());
            }
        }
    }
    // 简单消息提示框
    private void showDialog(String resultMsg) {
        new AlertDialog.Builder(getContext())
                .setTitle("结果")
                .setMessage(resultMsg)
                .setPositiveButton("确定", null)
                .show();
    }
}