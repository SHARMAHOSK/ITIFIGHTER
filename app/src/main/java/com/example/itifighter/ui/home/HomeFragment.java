package com.example.itifighter.ui.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.itifighter.CustomStackManager;
import com.example.itifighter.MyAdapter;
import com.example.itifighter.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class HomeFragment extends Fragment {


    private TabLayout tabLayout;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu_home, container, false);
        tabLayout = root.findViewById(R.id.tabLayoutX);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.white));

        final ViewPager viewPager = root.findViewById(R.id.viewPagerX);
        CustomStackManager.SetSPKeyValue(CustomStackManager.CURRENT_PAGE_KEY, 2);

        //----------------------------------------------------------//

        @SuppressLint("InflateParams")
        View view2 = getLayoutInflater().inflate(R.layout.custome_icon_tab, null);
        ImageView imageView = view2.findViewById(R.id.icon);
        Glide.with(getContext()).load(R.drawable.live_tab).into(imageView);
        TabLayout.Tab liveTest = tabLayout.newTab().setCustomView(imageView);

        //---------------------------------------------------------//

        tabLayout.addTab(tabLayout.newTab().setText("Previous Paper"));
        tabLayout.addTab(tabLayout.newTab().setText("Mock Test"));
        tabLayout.addTab(liveTest);
        tabLayout.addTab(tabLayout.newTab().setText("Test Series"));
        tabLayout.addTab(tabLayout.newTab().setText("My Test Series"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final MyAdapter adapter = new MyAdapter(getParentFragmentManager(),5);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(adapter);
                Objects.requireNonNull(viewPager.getAdapter()).finishUpdate(container);
                viewPager.setCurrentItem(2);
                tabLayout.selectTab(tabLayout.getTabAt(2));
                CustomStackManager.SetSPKeyValue(CustomStackManager.CURRENT_PAGE_KEY, 2);
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                CustomStackManager.SetSPKeyValue(CustomStackManager.CURRENT_PAGE_KEY, position);
            }

            @Override
            public void onPageSelected(int position) {
                CustomStackManager.SetSPKeyValue(CustomStackManager.CURRENT_PAGE_KEY, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                CustomStackManager.SetSPKeyValue(CustomStackManager.CURRENT_PAGE_KEY, tab.getPosition());
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

    protected Runnable mTabLayout_config = new Runnable() {
        @Override
        public void run() {
            if (tabLayout.getWidth() < requireContext().getResources().getDisplayMetrics().widthPixels) {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                ViewGroup.LayoutParams mParams = tabLayout.getLayoutParams();
                mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                tabLayout.setLayoutParams(mParams);
            } else {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        }
    };
}