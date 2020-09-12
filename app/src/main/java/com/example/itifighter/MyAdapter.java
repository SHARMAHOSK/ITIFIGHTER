package com.example.itifighter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter {
    int totalTabs;
    public MyAdapter(FragmentManager fm, int totalTabs) {
        super(fm,totalTabs);
        this.totalTabs = totalTabs;
        System.out.println("java");
    }
    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new MockTest();
            case 2:
                return new LiveTest();
            case 3:
                return new TestSeries();
            case 4:
                return new MyTestSeries();
            default:
                return new PreviousPaper();
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}