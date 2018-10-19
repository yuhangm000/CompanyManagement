package com.company.management;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;


public class MultiPages extends FragmentPagerAdapter {
    private ArrayList<Fragment> viewLists;
    public MultiPages(FragmentManager fm, ArrayList<Fragment>vl){
        super(fm);
        this.viewLists = vl;
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public Fragment getItem(int position) {
        return viewLists.get(position);
    }
}
