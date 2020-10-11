package com.example.itifighter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.itifighter.TestSeriesX.CustomListItemX;
import com.example.itifighter.TestSeriesX.CustomListItemY;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterX;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterZ;
import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class TestSeries extends Fragment {

    private String currentSubject,currentChapter,currentTest;
    private ArrayList<CustomListItem> Subjects;
    private ArrayList<CustomListItemX> Chapters;
    private ArrayList<CustomListItemY> Tests;
    private ListView listView;
    private FirebaseFirestore db;
    private Context mContext;
    private ArrayList<String> SubjectId,ChapterId,TestId,MPQ,Timmr,Tittl;

    private int currentLayer = 0,currentTestPos=0,currentSubjectPos=0,currentChapterPos=0;   //0=subjects, 1=chapters
    private View progressOverlay;
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
        final View mtView = inflater.inflate(R.layout.fragment_test_series, container, false);
        listView = mtView.findViewById(R.id.testxtRecycle);
        progressOverlay = mtView.findViewById(R.id.progress_overlay);
        mtView.findViewById(R.id.CustomBackButtonTS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBackButton();
            }
        });
        LoadSubjects();
        return mtView;
    }
    public void CustomBackButton(){
        switch (currentLayer){
            case 1:
                LoadSubjects();
/*            case 2:
                LoadChapters();*/
        }
    }

    void LoadSubjects(){
        currentLayer = 0;
        progressOverlay.setVisibility(View.VISIBLE);
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
                                "ts"));
                        SubjectId.add(document.getId());
                    }
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                    listView.setAdapter(adapter);
                    progressOverlay.setVisibility(View.GONE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubject = SubjectId.get(position);
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
        progressOverlay.setVisibility(View.VISIBLE);
        currentLayer = 1;
        db.collection("section").document("ts")
                .collection("branch").document(currentSubject)
                .collection("chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    progressOverlay.setVisibility(View.GONE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                            String Uid = FirebaseAuth.getInstance().getUid();
                            assert Uid != null;
                            FirebaseFirestore.getInstance().collection("users").document(Uid).collection("Products")
                                    .document("ts").collection("ProductId").document(ChapterId.get(i))
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if(documentSnapshot!=null && documentSnapshot.exists()){
                                                    currentChapter = ChapterId.get(i);
                                                    currentChapterPos = i;
                                                    LoadTest();
                                                }
                                                else{
                                                    Intent intent = new Intent(getContext(), PaytmPayment.class);
                                                    intent.putExtra("ProductId",Chapters.get(i).getId());
                                                    intent.putExtra("month1",Chapters.get(i).getMonth1());
                                                    intent.putExtra("month2",Chapters.get(i).getMonth2());
                                                    intent.putExtra("month3",Chapters.get(i).getMonth3());
                                                    intent.putExtra("price1",Chapters.get(i).getPrice1());
                                                    intent.putExtra("price2",Chapters.get(i).getPrice2());
                                                    intent.putExtra("price3",Chapters.get(i).getPrice3());
                                                    intent.putExtra("discount1",Chapters.get(i).getDiscount1());
                                                    intent.putExtra("discount2",Chapters.get(i).getDiscount2());
                                                    intent.putExtra("discount3",Chapters.get(i).getDiscount3());
                                                    intent.putExtra("currentSection",Chapters.get(i).getImagex());
                                                    intent.putExtra("titleName",Chapters.get(i).getTopicHeader());
                                                    intent.putExtra("countTest",Chapters.get(i).getTest());
                                                    intent.putExtra("currentSubject",currentSubject);
                                                    intent.putExtra("currentChapter",ChapterId.get(i));
                                                    mContext.startActivity(intent);
                                                }
                                            }
                                        }
                                    });
                        }
                    });
                }
            }
        });
    }

    private void LoadTest(){
        progressOverlay.setVisibility(View.VISIBLE);
        currentLayer = 2;
        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("chapter").document(currentChapter)
                .collection("tests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Tests = new ArrayList<>();
                    TestId = new ArrayList<>();
                    MPQ = new ArrayList<>();
                    Timmr = new ArrayList<>();
                    Tittl = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        int duration = Integer.parseInt(String.valueOf(document.get("duration"))), marks = Integer.parseInt(String.valueOf(document.get("marks"))), NOQ = Integer.parseInt(String.valueOf(document.get("NOQs")));
                        int score = marks*NOQ;
                        Tests.add(new CustomListItemY(document.getId(),
                                document.getString("title"), String.valueOf(duration),
                                String.valueOf(NOQ), String.valueOf(score)));
                        TestId.add(document.getId());
                        MPQ.add(String.valueOf(marks));
                        Timmr.add(String.valueOf(duration));
                        Tittl.add(document.getString("title"));
                    }
                    listView.setAdapter(new CustomListViewArrayAdapterZ(mContext,0,Tests,currentSubject,currentChapter));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                currentTest = TestId.get(i);
                                currentTestPos = i;
                                LoadExam();
                        }
                    });
                    progressOverlay.setVisibility(View.GONE);
                }
            }
        });
    }
    private void LoadExam() {

        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("chapter").document(currentChapter)
                .collection("tests").document(currentTest).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                            final ArrayList<Object> questions = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                                document.getString("option2"), document.getString("option3"),
                                                document.getString("option4"), document.getString("answer")));
                            }

                            final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document("" + uuid);
                            final CollectionReference UserTestRecord = userDoc.collection("scoreboard").document("mt").collection("test");
                            UserTestRecord.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    boolean found = false;
                                                    String total_attempted = "", total_skipped = "", total_correct = "";
                                                    String sub_list = "";
                                                    String accuracy = "", tpq = "", _mpq = "";
                                                    String targetChapterID = currentChapter;
                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                        if (document.getId().equals(targetChapterID)) {
                                                            found = true;
                                                            total_attempted = document.getString("total_attempted");
                                                            total_skipped = document.getString("total_skipped");
                                                            total_correct = document.getString("total_correct");
                                                            accuracy = document.getString("accuracy");
                                                            tpq = document.getString("tpq");
                                                            _mpq = document.getString("_mpq");
                                                            sub_list = document.getString("answer_key");
                                                            break;
                                                        }
                                                    }
                                                    if (found) {
                                                        //load result activity
                                                            Intent myIntent = new Intent(getContext(), TestResultActivity.class);
                                                            myIntent.putExtra("is_past_result", "true");
                                                            myIntent.putExtra("total_skipped", total_skipped);
                                                            myIntent.putExtra("total_attempted", total_attempted);
                                                            myIntent.putExtra("total_correct", total_correct);
                                                            myIntent.putExtra("section", "ts");
                                                            myIntent.putExtra("subject", currentSubject);
                                                            myIntent.putExtra("chapter", currentChapter);
                                                            myIntent.putExtra("questions", questions);
                                                            myIntent.putExtra("answer_key", sub_list);
                                                            assert _mpq != null;
                                                            myIntent.putExtra("_mpq", Integer.parseInt(_mpq));
                                                            myIntent.putExtra("timer", Integer.parseInt(Timmr.get(currentTestPos)));
                                                            myIntent.putExtra("title", Tittl.get(currentTestPos));
                                                            myIntent.putExtra("accuracy", accuracy);
                                                            myIntent.putExtra("tpq", tpq);
                                                            startActivity(myIntent);
                                                    } else {
                                                        Intent myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                                        myIntent.putExtra("section", "ts");
                                                        myIntent.putExtra("subject", SubjectId.get(currentSubjectPos));
                                                        myIntent.putExtra("chapter", ChapterId.get(currentChapterPos));
                                                        myIntent.putExtra("questions", questions);
                                                        myIntent.putExtra("_mpq", Integer.parseInt(MPQ.get(currentChapterPos)));
                                                        myIntent.putExtra("timer", Integer.parseInt(Timmr.get(currentChapterPos)));
                                                        myIntent.putExtra("title", Tittl.get(currentChapterPos) + " (Test Series)");
                                                        startActivity(myIntent);
                                                        }
                                                }else {
                                                    Intent myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                                    myIntent.putExtra("section", "ts");
                                                    myIntent.putExtra("subject", SubjectId.get(currentSubjectPos));
                                                    myIntent.putExtra("chapter", ChapterId.get(currentChapterPos));
                                                    myIntent.putExtra("questions", questions);
                                                    myIntent.putExtra("_mpq", Integer.parseInt(MPQ.get(currentChapterPos)));
                                                    myIntent.putExtra("timer", Integer.parseInt(Timmr.get(currentChapterPos)));
                                                    myIntent.putExtra("title", Tittl.get(currentChapterPos));
                                                    startActivity(myIntent);
                                                }
                                        }
                            });



                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadTest();
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