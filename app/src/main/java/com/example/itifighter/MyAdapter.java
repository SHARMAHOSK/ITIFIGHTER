package com.example.itifighter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PreviousPaper PPFragment = new PreviousPaper();
                return PPFragment;
            case 1:
                MockTest MTFragment = new MockTest();
                return MTFragment;
            case 2:
                LiveTest LTFragment = new LiveTest();
                return LTFragment;
            case 3:
                TestSeries TSFragment = new TestSeries();
                return TSFragment;
            case 4:
                MyTestSeries MTSFragment = new MyTestSeries();
                return MTSFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}