package com.company.management;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.os.Handler;
import android.os.Message;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Random;
import com.company.management.R;
/**
 * Created by 24448 on 2018/6/12.
 */

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