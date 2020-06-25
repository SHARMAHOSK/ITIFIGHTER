package com.example.itifighter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.itifighter.ui.home.HomeFragment;

public class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PreviousPaper();
            case 1:
                return new MockTest();
            case 2:
                return new LiveTest();
            case 3:
                return new TestSeries();
            case 4:
                return new MyTestSeries();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}