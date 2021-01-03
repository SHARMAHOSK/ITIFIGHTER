package com.example.itifighter.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.itifighter.CustomStackManager;
import com.example.itifighter.CustomViewPager;
import com.example.itifighter.MyAdapter;
import com.example.itifighter.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class HomeFragment extends Fragment {



    private TabLayout tabLayout;
    BottomNavigationView bnv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu_home, container, false);
        /*tabLayout = root.findViewById(R.id.tabLayoutX);*/
        bnv = root.findViewById(R.id.bottom_navigation);
        final CustomViewPager viewPager = root.findViewById(R.id.viewPagerX);
        viewPager.setPagingEnabled(false);
        bnv.setSelectedItemId(R.id.page_lt);
        CustomStackManager.current_page = 2;
        bnv.setItemIconTintList(null);
        bnv.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);   //LABEL_VISIBILITY_SELECTED: The label is only shown on the selected navigation item
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_pp:
                        // Respond to navigation item 1 click
                        viewPager.setCurrentItem(0);
                        CustomStackManager.current_page = 0;
                        return true;
                    case R.id.page_mt:
                        // Respond to navigation item 2 click
                        viewPager.setCurrentItem(1);
                        CustomStackManager.current_page = 1;
                        return true;
                    case R.id.page_lt:
                        // Respond to navigation item 3 click
                        viewPager.setCurrentItem(2);
                        CustomStackManager.current_page = 2;
                        return true;
                    case R.id.page_ts:
                        // Respond to navigation item 4 click
                        viewPager.setCurrentItem(3);
                        CustomStackManager.current_page = 3;
                        return true;
                    case R.id.page_mts:
                        // Respond to navigation item 5 click
                        viewPager.setCurrentItem(4);
                        CustomStackManager.current_page = 4;
                        return true;
                    default:
                        return false;
                }
            }
        });
        /*tabLayout.addTab(tabLayout.newTab().setText("Previous Paper"));
        tabLayout.addTab(tabLayout.newTab().setText("Mock Test"));
        tabLayout.addTab(tabLayout.newTab().setText("Daily Live Test"));
        tabLayout.addTab(tabLayout.newTab().setText("Test Series"));
        tabLayout.addTab(tabLayout.newTab().setText("My Test Series"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);*/
        System.out.println("hello");
        final MyAdapter adapter = new MyAdapter(getParentFragmentManager(), /*tabLayout.getTabCount()*/5);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
//                viewPager.canResolveLayoutDirection();
//                viewPager.clearOnPageChangeListeners();
                viewPager.setAdapter(adapter);
                Objects.requireNonNull(viewPager.getAdapter()).finishUpdate(container);
            }
        });
        System.out.println("sk");
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(2);
        /*tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        tabLayout.post(mTabLayout_config);*/
        return root;
    }
    /*protected Runnable mTabLayout_config = new Runnable()
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
    };*/
}