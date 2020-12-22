package com.example.itifighter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.itifighter.TestSeriesX.CustomListItemY;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterY;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterZ;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class MyTestSeries extends Fragment {
    private ListView listView;
    private FirebaseFirestore db;
    public static MyTestSeries instance;
    private Context mContext;
    private String Uid = FirebaseAuth.getInstance().getUid();
    private ArrayList<CustomListItemY> ProductData,Tests;
    private ArrayList<String> SubjectId,ChapterId,TestId,MPQ,Timmr,Tittl;
    private String currentChapter,currentSubject,currentTest;
    private ProgressDialog dialog;
    private int currentLayer = 0,currentChapterPos=0,currentSubjectPos=0,currentTestPos=0;   //0=subjects, 1=chapters
    private ImageButton back;
    private TextView emptyListMessage;


    public MyTestSeries() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
        instance = this;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mtView = inflater.inflate(R.layout.fragment_my_test_series, container, false);
        listView = mtView.findViewById(R.id.testmtRecycle);
        back = mtView.findViewById(R.id.CustomBackButtonMTS);
        emptyListMessage = mtView.findViewById(R.id.emptyListMessagemts);
        dialog = new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBackButton();
            }
        });
        back.setVisibility(View.INVISIBLE);
        LoadChapters();
        return mtView;
    }

    private void CustomBackButton() {
        if (currentLayer == 1) LoadChapters();
    }

    void LoadChapters(){
        CustomStackManager.GetInstance().SetPageState(0);
        currentLayer = 0;
        dialog.show();
        db.collection("users").document(Uid)
                .collection("Products").document("ts")
                .collection("ProductId")
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ProductData = new ArrayList<>();
                    ChapterId = new ArrayList<>();
                    SubjectId = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())){
                        ProductData.add(new CustomListItemY(queryDocumentSnapshot.getString("currentSubject"),
                                queryDocumentSnapshot.getId(),queryDocumentSnapshot.getString("ExpiryDate")));
                        SubjectId.add(queryDocumentSnapshot.getString("currentSubject"));
                        ChapterId.add(queryDocumentSnapshot.getId());
                    }
                    emptyListMessage.setVisibility(SubjectId.size() <= 0 ? View.VISIBLE : View.GONE);
                    listView.setAdapter( new CustomListViewArrayAdapterY(mContext,0,ProductData));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            currentChapter = ChapterId.get(i);
                            currentSubject = SubjectId.get(i);
                            currentSubjectPos = currentChapterPos = i;
                            LoadTest();
                        }
                    });
                    dialog.dismiss();
                    back.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void LoadTest(){
        CustomStackManager.GetInstance().SetPageState(1);
        dialog.show();
        currentLayer = 1;
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
                    emptyListMessage.setVisibility(TestId.size() <= 0 ? View.VISIBLE : View.GONE);
                    listView.setAdapter(new CustomListViewArrayAdapterZ(mContext,0,Tests,currentSubject,currentChapter));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            currentTest = TestId.get(i);
                            currentTestPos = i;
                            LoadExam();
                        }
                    });
                    dialog.dismiss();
                    back.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void LoadExam() {
        dialog.show();
        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("chapter").document(currentChapter)
                .collection("tests").document(currentTest).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult()!=null) {
                    final ArrayList<Object> questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                    }
                    final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document("" + uuid);
                    final CollectionReference UserTestRecord = userDoc.collection("scoreboard").document("ts").collection("test");
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
                                    dialog.dismiss();
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
                                    dialog.dismiss();
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
                                dialog.dismiss();
                                startActivity(myIntent);
                            }
                        }
                    });
                } else {
                    Toast.makeText(mContext,"no test found",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadTest();
                }
            }
        });
    }
}