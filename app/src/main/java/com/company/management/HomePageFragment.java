package com.company.management;

import android.content.pm.PackageManager;
import android.media.Image;
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
 * this page support user with four choice,
 * 1. click each one of four choices will change into a list view corresponding to it.
 */
public class HomePageFragment extends Fragment {
    ImageView material_info,
            in_warehouse,
            material_turn_back,
            out_warehouse,
            material_purchase,
            material_picking;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        material_purchase.setOnClickListener(new ChangeToFormList(R.string.material_purchase_apply));
        in_warehouse.setOnClickListener(new ChangeToFormList(R.string.material_in_warehouse));
        material_turn_back.setOnClickListener(new ChangeToFormList(R.string.material_turn_back));
        out_warehouse.setOnClickListener(new ChangeToFormList(R.string.material_out_warehouse));
        material_info.setOnClickListener(new ChangeToFormList(R.string.material_information));
        material_picking.setOnClickListener(new ChangeToFormList(R.string.material_picking));
    }

    public void init(View view) {
        material_purchase = (ImageView) view.findViewById(R.id.material_purchase);
        in_warehouse = (ImageView) view.findViewById(R.id.material_in_warehouse);
        material_turn_back = (ImageView) view.findViewById(R.id.material_turn_back);
        out_warehouse = (ImageView) view.findViewById(R.id.meterial_out_warehouse);
        material_info = (ImageView) view.findViewById(R.id.material_info);
        material_picking = (ImageView) view.findViewById(R.id.material_picking);
    }
    class ChangeToFormList implements View.OnClickListener {
        int  target;
        public ChangeToFormList(int target) {
            this.target = target;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("target_page", target);
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