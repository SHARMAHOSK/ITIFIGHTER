package com.example.itifighter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.itifighter.TestSeriesX.CustomListItemX;
import com.example.itifighter.TestSeriesX.CustomListItemY;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterY;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyTestSeries extends Fragment {
    private ArrayList<CustomListItemX> Chapters;
    private ListView listView;
    private FirebaseFirestore db;
    private Context mContext;
    private String Uid = FirebaseAuth.getInstance().getUid();
    private ArrayList<CustomListItemY> ProductData;


    public MyTestSeries() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mtView = inflater.inflate(R.layout.fragment_my_test_series, container, false);
        listView = mtView.findViewById(R.id.testmtRecycle);
        LoadChapters();
        return mtView;
    }

    void LoadChapters(){
        db.collection("users").document(Uid)
                .collection("Products").document("ts")
                .collection("ProductId")
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ProductData = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())){
                        ProductData.add(new CustomListItemY(queryDocumentSnapshot.getString("currentSubject"),
                                queryDocumentSnapshot.getId(),queryDocumentSnapshot.getString("ExpiryDate")));
                    }
                    listView.setAdapter( new CustomListViewArrayAdapterY(mContext,0,ProductData));
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getProductDetails();
    }

    private void getProductDetails() {

    }
}