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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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


public class MockTest extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static MockTest instance;
    private int currentSubjectPos = 0, currentChapterPos = 0;   //records which item was clicked in previous list
    private ArrayList<CustomListItem> Subjects, Chapters;
    private ArrayList<String> MPQs, Timers, Titles, SubjectIds, CHapterIds;
    private ArrayList<Question> questions;
    private ListView listView;
    private TextView emptyListMessage;
    public String curruntSubject, curruntChapter;
    private FirebaseFirestore db;
    private Context mContext;

    public MockTest() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            db = FirebaseFirestore.getInstance();
            mContext = getContext();
            instance = this;
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mtView = inflater.inflate(R.layout.fragment_mock_test, container, false);
        listView = mtView.findViewById(R.id.mt_branch_list);
        emptyListMessage = mtView.findViewById(R.id.emptyListMessagetsmt);
        CustomizeView();
        return mtView;
    }

    void LoadSubjects() {
        CustomStackManager.SetSPKeyValue(CustomStackManager.MT_STATE_KEY, 0);
        db.collection("section").document("mt").collection("branch")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    SubjectIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        SubjectIds.add(xNull(document.getId()));
                        Subjects.add(
                                new CustomListItem(
                                        xNull(document.getString("name")),
                                        xNull(document.getString("desc")),
                                        "mt")
                        );
                    }
                    emptyListMessage.setVisibility(SubjectIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubjectPos = iNull(position);
                            curruntSubject = SubjectIds.get(position);
                            if(bNull(curruntSubject)){
                                CustomStackManager.SetSPKeyValue(CustomStackManager.MT_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, curruntSubject);
                                LoadChapters();
                            }
                        }
                    });

                } else {
                    emptyListMessage.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void LoadChapters() {
        CustomStackManager.SetSPKeyValue(CustomStackManager.MT_STATE_KEY, 1);
        curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.MT_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
        db.collection("section").document("mt").collection("branch")
                .document(xNull(curruntSubject)).collection("chapter").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Chapters = new ArrayList<>();
                    CHapterIds = new ArrayList<>();
                    MPQs = new ArrayList<>();
                    Timers = new ArrayList<>();
                    Titles = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        CHapterIds.add(xNull(document.getId()));
                        Chapters.add(
                                new CustomListItem(
                                        iNull(CHapterIds.size()),
                                        xNull(document.getString("name")),
                                        xNull(document.getString("desc")),
                                        vNull(document.getString("price")),
                                        vNull(document.getString("discount")),
                                        iNull(document.getString("NOQ")),
                                        iNull(document.getString("Timer")),
                                        iNull(document.getString("MPQ")),
                                        "mt/chapter"));

                        MPQs.add(xNull(document.getString("MPQ")));
                        Timers.add(xNull(document.getString("Timer")));
                        Titles.add(xNull(document.getString("name")));
                    }
                    emptyListMessage.setVisibility(CHapterIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Chapters);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            CustomListItem data = Chapters.get(position);
                            int Mcq = iNull(data.getQuesCount());
                            if (Mcq > 0) {
                                curruntChapter = CHapterIds.get(position);
                                if(bNull(curruntChapter)) {
                                    CustomStackManager.SetSPKeyValue(CustomStackManager.MT_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, curruntChapter);
                                    LoadTest();
                                }
                            }
                            currentChapterPos = iNull(position);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadSubjects();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadTest() {
        curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.MT_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
        curruntChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.MT_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
        db.collection("section").document("mt")
                .collection("branch").document(xNull(curruntSubject))
                .collection("chapter").document(xNull(curruntChapter))
                .collection("question")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    questions = new ArrayList<>();

                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        questions.add(
                                new Question(
                                        xNull(document.getString("question")),
                                        xNull(document.getString("option1")),
                                        xNull(document.getString("option2")),
                                        xNull(document.getString("option3")),
                                        xNull(document.getString("option4")),
                                        xNull(document.getString("answer")))
                        );
                    }

                    if (questions.isEmpty()) {
                        Toast.makeText(mContext, "Coming Soon..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final String uuid = xNull((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid());
                    final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users")
                            .document("" + uuid);
                    final CollectionReference UserTestRecord = userDoc.collection("scoreboard")
                            .document("mt").collection("test");
                    UserTestRecord.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean found = false;
                                String total_attempted = "", total_skipped = "", total_correct = "";
                                String sub_list = "";
                                String accuracy = "", tpq = "", _mpq = "";
                                String targetChapterID = xNull(CHapterIds.get(currentChapterPos));
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    if (xNull(document.getId()).equals(targetChapterID)) {
                                        found = true;
                                        total_attempted = xNull(document.getString("total_attempted"));
                                        total_skipped = xNull(document.getString("total_skipped"));
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
                                    //load result activity
                                    myIntent = new Intent(getContext(), TestResultActivity.class);
                                    myIntent.putExtra("is_past_result", "true");
                                    myIntent.putExtra("total_skipped", total_skipped);
                                    myIntent.putExtra("total_attempted", total_attempted);
                                    myIntent.putExtra("total_correct", total_correct);
                                    myIntent.putExtra("section", "mt");
                                    myIntent.putExtra("subject", xNull(SubjectIds.get(currentSubjectPos)));
                                    myIntent.putExtra("chapter", xNull(CHapterIds.get(currentChapterPos)));
                                    myIntent.putExtra("questions", questions);
                                    myIntent.putExtra("answer_key", sub_list);
                                    assert _mpq != null;
                                    myIntent.putExtra("_mpq", Integer.parseInt(_mpq));
                                    myIntent.putExtra("timer", Integer.parseInt(Timers.get(currentChapterPos)));
                                    myIntent.putExtra("title", xNull(Titles.get(currentChapterPos)));
                                    myIntent.putExtra("accuracy", accuracy);
                                    myIntent.putExtra("tpq", tpq);
                                } else {
                                    myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                    myIntent.putExtra("section", "mt");
                                    myIntent.putExtra("subject", xNull(SubjectIds.get(currentSubjectPos)));
                                    myIntent.putExtra("chapter", xNull(CHapterIds.get(currentChapterPos)));
                                    myIntent.putExtra("questions", questions);
                                    myIntent.putExtra("_mpq", iNull(MPQs.get(currentChapterPos)));
                                    myIntent.putExtra("timer", iNull(Timers.get(currentChapterPos)));
                                    myIntent.putExtra("title", xNull(Titles.get(currentChapterPos)) + " (Mock Test)");
                                }
                                startActivity(myIntent);
                            } else {
                                Intent myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                myIntent.putExtra("section", "mt");
                                myIntent.putExtra("subject", xNull(SubjectIds.get(currentSubjectPos)));
                                myIntent.putExtra("chapter", xNull(CHapterIds.get(currentChapterPos)));
                                myIntent.putExtra("questions", questions);
                                myIntent.putExtra("_mpq", iNull(MPQs.get(currentChapterPos)));
                                myIntent.putExtra("timer", iNull(Timers.get(currentChapterPos)));
                                myIntent.putExtra("title", xNull(Titles.get(currentChapterPos)));
                                startActivity(myIntent);
                            }
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
        int currentState = CustomStackManager.GetSPKeyValue(CustomStackManager.MT_STATE_KEY, 0);
        if (currentState == 1) {
            curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.MT_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
            if (bNull(curruntSubject)) {
                LoadSubjects();
            } else {
                LoadChapters();
            }
                /*case 2:
                curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.MT_STATE_KEY+CustomStackManager.TARGET_SUBJECT_KEY, "");
                curruntChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.MT_STATE_KEY+CustomStackManager.TARGET_CHAPTER_KEY, "");
                if(curruntSubject == null || curruntSubject.isEmpty() || curruntChapter == null || curruntChapter.isEmpty())
                    LoadSubjects();
                else
                    LoadTest();
                break;*/
        } else {
            LoadSubjects();
        }
    }


    public String xNull(String str) {
        if (str != null) return str;
        else return "";
    }

    public boolean bNull(String str){
        if(str == null) return false;
        else return !str.trim().isEmpty();
    }

    public Double dNull(Double dbl){
        if(dbl == null) return 0.0;
        else if(dbl<1.0) return 0.0;
        else return dbl;
    }

    public Double vNull(String str){
        double num = 0.0;
        if(bNull(xNull(str))){
            try {
                num = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                num = 0.0;
            }
        }
        return num;
    }
    public String vNull(double str){
        String num = "0.0";
        if(dNull(str)>0){
            try{
                num = String.valueOf(str);
            }catch (Exception e){
                num = "0.0";
            }
        }
        return num;
    }

    public int iNull(int value){
        if(value<1) return 0;
        else return value;
    }

    public int iNull(String value){
        int num = 0;
        if(bNull(xNull(value))){
            try {
                num = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                num = 0;
            }
        }
        return iNull(num);
    }
}