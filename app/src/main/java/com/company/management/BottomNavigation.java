package com.company.management;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;

import com.company.management.R;

/**
 * this page just show the basic function & no need to checkout user's access permission.
 */
public class BottomNavigation extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager vpager;
    private ArrayList<Fragment> aList;
    private MultiPages mAdapter;
    private BottomNavigationView navigation;
    private MyApp myApp;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    vpager.setCurrentItem(0);
                    break;
                case R.id.navigation_remind:
                    vpager.setCurrentItem(1);
                    break;
                case R.id.navigation_mine:
                    vpager.setCurrentItem(2);
                    break;
            }
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        myApp = (MyApp) getApplication();
        CheckIn();

        navigation = (BottomNavigationView) findViewById(R.id.bottomNavi);
        vpager = (ViewPager) findViewById(R.id.pager);
        aList = new ArrayList<Fragment>();
        LayoutInflater li = getLayoutInflater();
        aList.add(new HomePageFragment());
        aList.add(new MassageRemind());
        aList.add(new MineFragment());
        mAdapter = new MultiPages(getSupportFragmentManager(),aList);
        vpager.setAdapter(mAdapter);
        ColorStateList colorStateList = (ColorStateList) getResources().getColorStateList(R.color.navigation_menu_item_color);
        navigation.setItemTextColor(colorStateList);
        navigation.setItemIconTintList(colorStateList);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        vpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("position",String.valueOf(position));
                navigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getBundleExtra("data");
            Integer pages = bundle.getInt("fragment");
            vpager.setCurrentItem(pages);
        }
        catch (Exception e){
            System.out.println("unknown err");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {

    }

    private void CheckIn() {
        if(!myApp.isIn) {
            Intent intent = new Intent();
            intent.setClass(BottomNavigation.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
