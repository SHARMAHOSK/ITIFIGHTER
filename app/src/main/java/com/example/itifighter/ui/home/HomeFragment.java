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

public class HomeFragment extends Fragment {

    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /*Button logout = findViewById(R.id.logout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mFirebaseAuth.signOut();
            startActivity(new Intent(dashboard.this,Login.class));
            finish();
            }
        });*/

        TabLayout tabLayout = root.findViewById(R.id.tabLayoutX);
        viewPager = root.findViewById(R.id.viewPagerX);
        tabLayout.addTab(tabLayout.newTab().setText("Previous Paper"));
        tabLayout.addTab(tabLayout.newTab().setText("Mock Test"));
        tabLayout.addTab(tabLayout.newTab().setText("Daily Live Test"));
        tabLayout.addTab(tabLayout.newTab().setText("Test Series"));
        tabLayout.addTab(tabLayout.newTab().setText("My Test Series"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MyAdapter adapter = new MyAdapter(getFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
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

        return root;
    }

}