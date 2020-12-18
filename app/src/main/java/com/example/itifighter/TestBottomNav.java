package com.example.itifighter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestBottomNav#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestBottomNav extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    BottomNavigationView bnv;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TestBottomNav() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestBottomNav.
     */
    // TODO: Rename and change types and number of parameters
    public static TestBottomNav newInstance(String param1, String param2) {
        TestBottomNav fragment = new TestBottomNav();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View bnView = inflater.inflate(R.layout.fragment_test_bottom_nav, container, false);
        bnv = bnView.findViewById(R.id.bottom_navigation);
        bnv.setSelectedItemId(R.id.page_lt);
        bnv.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);   //LABEL_VISIBILITY_SELECTED: The label is only shown on the selected navigation item
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_pp:
                        // Respond to navigation item 1 click
                        return true;
                    case R.id.page_mt:
                        // Respond to navigation item 2 click
                        return true;
                    case R.id.page_lt:
                        // Respond to navigation item 3 click
                        return true;
                    case R.id.page_ts:
                        // Respond to navigation item 4 click
                        return true;
                    case R.id.page_mts:
                        // Respond to navigation item 5 click
                        return true;
                    default:
                        return false;
                }
            }
        });
        return bnView;
    }
}