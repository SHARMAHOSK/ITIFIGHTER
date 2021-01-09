package com.example.itifighter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyAdapter extends FragmentStatePagerAdapter{
    int totalTabs;
    public MyAdapter(FragmentManager fm, int totalTabs) {
        super(fm,totalTabs);
        this.totalTabs = totalTabs;
        System.out.println("java");
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                MockTest.instance = new MockTest();
                return MockTest.instance;
            case 2:
                LiveTest.instance = new LiveTest();
                return LiveTest.instance;
            case 3:
                TestSeries.instance = new TestSeries();
                return TestSeries.instance;
            case 4:
                MyTestSeries.instance = new MyTestSeries();
                return MyTestSeries.instance;
            default:
                PreviousPaper.instance = new PreviousPaper();
                return PreviousPaper.instance;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}