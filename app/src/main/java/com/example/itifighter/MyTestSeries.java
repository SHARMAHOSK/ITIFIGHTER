package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    @SuppressLint("StaticFieldLeak")
    public static MyTestSeries instance;
    private Context mContext;
    private final String Uid = FirebaseAuth.getInstance().getUid();
    private ArrayList<CustomListItemY> ProductData, Tests;
    private ArrayList<String> SubjectId, ChapterId, TestId, MPQ, Timmr, Tittl;
    private String currentChapter, currentSubject, currentTest;
    private int currentChapterPos = 0, currentSubjectPos = 0, currentTestPos = 0;   //0=subjects, 1=chapters
    private TextView emptyListMessage;


    public MyTestSeries() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mtView = inflater.inflate(R.layout.fragment_my_test_series, container, false);
        listView = mtView.findViewById(R.id.testmtRecycle);
        emptyListMessage = mtView.findViewById(R.id.emptyListMessagemts);
        LoadChapters();
        return mtView;
    }


    void LoadChapters() {
        CustomStackManager.SetSPKeyValue(CustomStackManager.MTS_STATE_KEY, 0);
        db.collection("users").document(Uid)
                .collection("Products").document("ts")
                .collection("ProductId")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ProductData = new ArrayList<>();
                    ChapterId = new ArrayList<>();
                    SubjectId = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        ProductData.add(
                                new CustomListItemY(
                                        xNull(queryDocumentSnapshot.getString("currentSubject")),
                                        xNull(queryDocumentSnapshot.getId()),
                                        xNull(queryDocumentSnapshot.getString("ExpiryDate"))));
                        SubjectId.add(xNull(queryDocumentSnapshot.getString("currentSubject")));
                        ChapterId.add(xNull(queryDocumentSnapshot.getId()));
                    }
                    emptyListMessage.setVisibility(SubjectId.size() <= 0 ? View.VISIBLE : View.GONE);
                    listView.setAdapter(new CustomListViewArrayAdapterY(mContext, 0, ProductData));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            currentChapter = ChapterId.get(i);
                            currentSubject = SubjectId.get(i);
                            if (bNull(currentSubject) && bNull(currentChapter)) {
                                currentSubjectPos = currentChapterPos = i;
                                CustomStackManager.SetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, currentSubject);
                                CustomStackManager.SetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, currentChapter);
                                LoadTest();
                            }
                        }
                    });
                }
            }
        });
    }

    private void LoadTest() {

        CustomStackManager.SetSPKeyValue(CustomStackManager.MTS_STATE_KEY, 1);
        currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
        currentChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
        db.collection("section").document("ts")
                .collection("branch").document(xNull(currentSubject))
                .collection("chapter").document(xNull(currentChapter))
                .collection("tests")
                .get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Tests = new ArrayList<>();
                            TestId = new ArrayList<>();
                            MPQ = new ArrayList<>();
                            Timmr = new ArrayList<>();
                            Tittl = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                int duration = iNull(oNull(document.get("duration"))),
                                        marks = iNull(oNull(document.get("marks"))),
                                        NOQ = iNull(oNull(document.get("NOQs")));
                                int score = marks * NOQ;
                                Tests.add(
                                        new CustomListItemY(
                                                xNull(document.getId()),
                                                xNull(document.getString("title")),
                                                intToStringNull(duration),
                                                intToStringNull(NOQ),
                                                intToStringNull(score)));
                                TestId.add(xNull(document.getId()));
                                MPQ.add(intToStringNull(marks));
                                Timmr.add(intToStringNull(duration));
                                Tittl.add(xNull(document.getString("title")));
                            }
                            emptyListMessage.setVisibility(TestId.size() <= 0 ? View.VISIBLE : View.GONE);
                            listView.setAdapter(new CustomListViewArrayAdapterZ(mContext, 0, Tests, currentSubject, currentChapter));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    currentTest = TestId.get(i);
                                    if (bNull(currentTest)) {
                                        currentTestPos = i;
                                        CustomStackManager.SetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_EXAM_KEY, currentTest);
                                        LoadExam();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void LoadExam() {

        CustomStackManager.SetSPKeyValue(CustomStackManager.MTS_STATE_KEY, 2);
        currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
        currentChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
        currentTest = CustomStackManager.GetSPKeyValue(CustomStackManager.MTS_STATE_KEY + CustomStackManager.TARGET_EXAM_KEY, "");

        db.collection("section").document("ts")
                .collection("branch").document(xNull(currentSubject))
                .collection("chapter").document(xNull(currentChapter))
                .collection("tests").document(xNull(currentTest))
                .collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    final ArrayList<Object> questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        questions.add(
                                new Question(
                                        xNull(document.getString("question")),
                                        xNull(document.getString("option1")),
                                        xNull(document.getString("option2")),
                                        xNull(document.getString("option3")),
                                        xNull(document.getString("option4")),
                                        xNull(document.getString("answer"))
                                ));
                    }
                    final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(xNull(uuid));
                    final CollectionReference UserTestRecord = userDoc.collection("scoreboard").document("ts")
                            .collection("test");
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
                                        total_attempted = xNull(document.getString("total_attempted"));
                                        total_skipped =  xNull( document.getString("total_skipped"));
                                        total_correct = xNull(document.getString("total_correct"));
                                        accuracy = xNull(document.getString("accuracy"));
                                        tpq = xNull(document.getString("tpq"));
                                        _mpq = xNull(document.getString("_mpq"));
                                        sub_list = xNull(document.getString("answer_key"));
                                        break;
                                    }
                                }
                                Intent myIntent;
                                if (found) {
                                    myIntent = new Intent(getContext(), TestResultActivity.class);
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
                                } else {
                                    myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                    myIntent.putExtra("section", "ts");
                                    myIntent.putExtra("subject", SubjectId.get(currentSubjectPos));
                                    myIntent.putExtra("chapter", ChapterId.get(currentChapterPos));
                                    myIntent.putExtra("questions", questions);
                                    myIntent.putExtra("_mpq", Integer.parseInt(MPQ.get(currentChapterPos)));
                                    myIntent.putExtra("timer", Integer.parseInt(Timmr.get(currentChapterPos)));
                                    myIntent.putExtra("title", Tittl.get(currentChapterPos) + " (Test Series)");
                                }
                                startActivity(myIntent);
                            } else {
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
                    Toast.makeText(mContext, "no test found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadTest();
                }
            }
        });
    }

    public String xNull(String str) {
        if (str != null) return str;
        else return "";
    }

    public boolean bNull(String str) {
        if (str == null) return false;
        else return !str.trim().isEmpty();
    }

    public Double dNull(Double dbl) {
        if (dbl == null) return 0.0;
        else if (dbl < 1.0) return 0.0;
        else return dbl;
    }

    public Double vNull(String str) {
        double num = 0.0;
        if (bNull(xNull(str))) {
            try {
                num = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                num = 0.0;
            }
        }
        return num;
    }

    public String vNull(double str) {
        String num = "0.0";
        if (dNull(str) > 0) {
            try {
                num = String.valueOf(str);
            } catch (Exception e) {
                num = "0.0";
            }
        }
        return num;
    }

    public int iNull(int value) {
        if (value < 1) return 0;
        else return value;
    }

    public int iNull(String value) {
        int num = 0;
        if (bNull(xNull(value))) {
            try {
                num = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                num = 0;
            }
        }
        return iNull(num);
    }

    public String intToStringNull(int value) {
        String num = "0";
        if (iNull(value) > 0) {
            try {
                num = String.valueOf(value);
            } catch (NumberFormatException e) {
                num = "0";
            }
        }
        return num;
    }

    private String oNull(Object cc) {
        String value = "";
        if (cc != null) {
            try {
                value = String.valueOf(cc);
            } catch (Exception e) {
                value = "";
            }
        }
        return value;
    }
}