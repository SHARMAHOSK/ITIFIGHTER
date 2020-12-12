package com.example.itifighter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LiveTest extends Fragment {

    private FirebaseFirestore db;
    private Context mContext;
    private ArrayList<CustomListItem> Subjects, Chapters;
    ArrayList<String> SubjectIds, CHapterIds;
    private ListView listView;
    private int currentLayer = 0;
    private int currentSubjectPos = 0, currentChapterPos = 0;
    private View progressOverlay;

    public LiveTest() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View ltView = inflater.inflate(R.layout.fragment_live_test, container, false);
        listView = ltView.findViewById(R.id.lt_branch_list);
        progressOverlay = ltView.findViewById(R.id.progress_overlay);
        /*((Button)ltView.findViewById(R.id.CustomBackButtonLT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBackButton(ltView);
            }
        });*/
        CustomizeView();
        return ltView;
    }

    public void CustomBackButton(){
        if (currentLayer == 1) {
            LoadSubjects();
        }
    }

    void LoadSubjects(){
        currentLayer = 0;
        progressOverlay.setVisibility(View.VISIBLE);
        db.collection("section").document("lt").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //loadingFinished = true;
                //HIDE LOADING IT HAS FINISHED
                //spinner.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    SubjectIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        /*list.add(document.getString("Name"));*/
                        SubjectIds.add(document.getId());
                        Subjects.add(new CustomListItem(document.getString("name"),
                                document.getString("desc"),"lt"));
                    }

                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);

                    listView.setAdapter(adapter);
                    progressOverlay.setVisibility(View.GONE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubjectPos = position;
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
        currentLayer = 1;
        db.collection("section").document("lt").collection("branch").document(SubjectIds.get(currentSubjectPos)).collection("chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*examList = new ArrayList<>();*/
                    Chapters = new ArrayList<>();
                    CHapterIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        CHapterIds.add(document.getId());
                        /*Chapters.add(new CustomListItem(document.getString("name"),
                                document.getString("desc")*//*,
                                Double.parseDouble(Objects.requireNonNull(document.getString("price"))),
                                Double.parseDouble(Objects.requireNonNull(document.getString("discount"))),
                                Integer.parseInt((Objects.requireNonNull(document.getString("NOQ")))), Integer.parseInt(Objects.requireNonNull(document.getString("Timer"))),
                                Integer.parseInt(Objects.requireNonNull(document.getString("MPQ")))*//*,"lt/chapter"));*/
                        Chapters.add(new CustomListItem(document.getString("name"),
                                document.getString("desc"),
                                document.getString("month1"),
                                Double.parseDouble(Objects.requireNonNull(document.getString("price1"))),
                                Double.parseDouble(Objects.requireNonNull(document.getString("discount1"))), "lt/chapter"));
                        /*Chapters.add(document.getString("Name"));*/
                    }
                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Chapters);

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentChapterPos = position;
                            Intent myIntent = new Intent(getContext(), LiveTestHomeActivity.class);
                            myIntent.putExtra("subject", SubjectIds.get(currentSubjectPos));
                            myIntent.putExtra("chapter", CHapterIds.get(currentChapterPos));
                            startActivity(myIntent);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadChapters();
                }
            }
        });
    }

    private void CustomizeView() {
        LoadSubjects();
    }
}