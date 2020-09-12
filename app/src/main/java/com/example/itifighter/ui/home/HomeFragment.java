package com.example.itifighter.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.itifighter.MyAdapter;
import com.example.itifighter.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class HomeFragment extends Fragment {



    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu_home, container, false);
        tabLayout = root.findViewById(R.id.tabLayoutX);
        final ViewPager viewPager = root.findViewById(R.id.viewPagerX);
        tabLayout.addTab(tabLayout.newTab().setText("Previous Paper"));
        tabLayout.addTab(tabLayout.newTab().setText("Mock Test"));
        tabLayout.addTab(tabLayout.newTab().setText("Daily Live Test"));
        tabLayout.addTab(tabLayout.newTab().setText("Test Series"));
        tabLayout.addTab(tabLayout.newTab().setText("My Test Series"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        System.out.println("hello");
        final MyAdapter adapter = new MyAdapter(getParentFragmentManager(), tabLayout.getTabCount());
        new Runnable(){
            @Override
            public void run() {
                viewPager.clearOnPageChangeListeners();
                viewPager.setAdapter(adapter);
                Objects.requireNonNull(viewPager.getAdapter()).finishUpdate(container);
            }
        }.run();
        System.out.println("sk");
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.post(mTabLayout_config);
        return root;
    }
    protected Runnable mTabLayout_config = new Runnable()
    {
        @Override
        public void run()
        {
            if(tabLayout.getWidth() < requireContext().getResources().getDisplayMetrics().widthPixels)
            {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                ViewGroup.LayoutParams mParams = tabLayout.getLayoutParams();
                mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                tabLayout.setLayoutParams(mParams);
            }
            else
            {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        }
    };
}