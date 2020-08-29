package com.example.itifighter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.itifighter.TestSeriesX.CustomListItemX;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterX;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class TestSeries extends Fragment {

    private String currentSubject;
    private ArrayList<CustomListItem> Subjects;
    private ArrayList<CustomListItemX> Chapters;
    private ListView listView;
    private FirebaseFirestore db;
    private Context mContext;
    private ArrayList<String> SubjectId,ChapterId;
    public TestSeries() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mtView = inflater.inflate(R.layout.fragment_test_series, container, false);
        listView = mtView.findViewById(R.id.testxtRecycle);
        LoadSubjects();
        return mtView;
    }

    void LoadSubjects(){
        db.collection("section").document("ts")
                .collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    SubjectId = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Subjects.add(new CustomListItem(document.getString("name"),
                                document.getString("desc"),
                                0.00,
                                document.getString("name"),
                                /*getExamCount(document.getId())*/5,"ts"));
                        SubjectId.add(document.getId());
                    }
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubject = SubjectId.get(position);
                            LoadChapters();
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadSubjects();
                }
            }
        });
    }
    void LoadChapters(){
        db.collection("section").document("ts")
                .collection("branch").document(currentSubject)
                .collection("exam").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Chapters = new ArrayList<>();
                    ChapterId = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Chapters.add(new CustomListItemX(document.getString("name"), document.getString("test"), "ts",document.getId(),document.getString("month1"),document.getString("month2"),document.getString("month3"),document.getString("price1"),document.getString("price2"),document.getString("price3"),document.getString("discount1"),document.getString("discount2"),document.getString("discount3")));
                        ChapterId.add(document.getId());
                    }
                    ArrayAdapter<CustomListItemX> adapter = new CustomListViewArrayAdapterX(mContext,
                            0,
                            Chapters,currentSubject,ChapterId);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(getContext(), "Go to MyTest Series Section", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    /*private void LoadExams() {
        Toast.makeText(mContext,"kzs,ls0",Toast.LENGTH_SHORT).show();
        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("exam").document(currentChapter).collection("tests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Tests = new ArrayList<>();
                    TestId = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){
                        Tests.add(new CustomListItemY(documentSnapshot.getString("name"),documentSnapshot.getString("quetion"),documentSnapshot.getString("score"),"ts",documentSnapshot.getString("duration")));
                        TestId.add(documentSnapshot.getId());
                    }
                    ArrayAdapter<CustomListItemY> adapter = new CustomListViewArrayAdapterY(mContext,0,Tests,TestId,currentSubject,currentChapter);
                    listView.setAdapter(adapter);
                }
                else LoadExams();
            }
        });*/
    }