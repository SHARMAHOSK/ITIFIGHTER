package com.example.itifighter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LiveTestHomeActivity extends AppCompatActivity {

    TextView countdown, TestIsLiveTag;
    CollectionReference colRef;
    boolean utAvailable = false;
    LinearLayout ltList;
    int ftCount = 0;
    private Context mContext;
    private View progressOverlay;
    private ArrayList<Question> questions, these_questions;
    private ArrayList<String> attemptedTestIDs; //tests that student has taken in past.

    ArrayList<LiveTestBody> testsLive;
    private LiveTestBody upcomingTest;
    private String utID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_test_home);
        progressOverlay = findViewById(R.id.progress_overlay);
        ltList = findViewById(R.id.LiveTestList);
        progressOverlay.setVisibility(View.VISIBLE);
        colRef = FirebaseFirestore.getInstance().collection("section").document("lt")
                .collection("branch").document(""+getIntent().getStringExtra("subject"))
                .collection("chapter").document(""+getIntent().getStringExtra("chapter"))
                .collection("tests");

        mContext = getApplicationContext();
        attemptedTestIDs = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("scoreboard").document("lt").collection("test").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        attemptedTestIDs.add(document.getId());
                    }
                }
                CustomizeView();
            }
        });
    }

    void FillLiveTestList(final String _id, int phase, String title, int noq, int min, int marks, long start, long result){
        Calendar st =Calendar.getInstance();
        st.setTimeInMillis(start);
        String sTime = ""+st.get(Calendar.DAY_OF_MONTH)+"/"+(st.get(Calendar.MONTH)+1)+"/"+st.get(Calendar.YEAR)+" "+st.get(Calendar.HOUR)+":"+st.get(Calendar.MINUTE)+" am";

        Calendar rt =Calendar.getInstance();
        rt.setTimeInMillis(result);
        String rTime = ""+rt.get(Calendar.DAY_OF_MONTH)+"/"+(rt.get(Calendar.MONTH)+1)+"/"+rt.get(Calendar.YEAR)+" "+rt.get(Calendar.HOUR)+":"+rt.get(Calendar.MINUTE)+" am";

        View ltRow;
        ltRow = View.inflate(this, R.layout.activity_live_test_xyz, null);
        ((TextView)ltRow.findViewById(R.id.ltTitle)).setText(title);
        ((TextView)ltRow.findViewById(R.id.ltQMM)).setText(""+noq+" Qs  |  "+min+" Min's  |  "+marks+" Marks");
        ((TextView)ltRow.findViewById(R.id.ltSTRT)).setText("start: "+sTime+"   |   result: "+rTime);
        if(phase == 0){
            //upcoming test
            countdown = ltRow.findViewById(R.id.upcomingCountDown);
            TestIsLiveTag = ltRow.findViewById(R.id.TestIsLiveTAG);
            ltList.addView(ltRow, 0);
            utAvailable = true;
            ltRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(utAvailable){
                        StartLiveTest();
                    }
                }
            });
        }else if(phase == 1){
            //future test
            ltList.addView(ltRow, ftCount+1);
            ftCount++;
        }else{
            ltList.addView(ltRow);
            //past test
            ltRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(utAvailable){
                        LoadPastTestResult(_id);
                    }
                }
            });
        }
    }

    private void LoadPastTestResult(String _id) {
        final String tid = _id;
        colRef.document(""+tid).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    these_questions = new ArrayList<>();
                    Toast.makeText(mContext, tid+" task successful: "+task.getResult().size(), Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Toast.makeText(mContext, "fetched: "+document.getId(), Toast.LENGTH_SHORT).show();
                        these_questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                    }
                    Intent intent = new Intent(mContext, TestAnswerSheetActivity.class);
                    intent.putExtra("questions", (Serializable) these_questions);
                    intent.putExtra("tid", tid);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "error getting answer sheet", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error getting answer sheet: ", task.getException());
                }
            }
        });
    }

    private void CustomizeView() {
        /*startTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartLiveTest();
            }
        });*/

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    testsLive = new ArrayList<>();
                    attemptedTestIDs = new ArrayList<>();
                    for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        if(document.getString("TestInHistory").equals("true") && attemptedTestIDs.contains(document.getId())){
                            testsLive.add(new LiveTestBody(document.getId(), Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                    document.getString("TestInHistory"),
                                    Objects.requireNonNull(document.getLong("duration")).intValue(),
                                    Objects.requireNonNull(document.getLong("marks")).intValue(),
                                    document.getLong("rTime"),
                                    document.getLong("sTime"),
                                    document.getString("title")));
                        }else{
                            if(upcomingTest == null || upcomingTest.sTime > document.getLong("sTime")) {
                                utID = document.getId();
                                upcomingTest = new LiveTestBody(document.getId(), Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                        document.getString("TestInHistory"),
                                        Objects.requireNonNull(document.getLong("duration")).intValue(),
                                        Objects.requireNonNull(document.getLong("marks")).intValue(),
                                        document.getLong("rTime"),
                                        document.getLong("sTime"),
                                        document.getString("title"));
                                testsLive.add((upcomingTest));
                            }else
                                testsLive.add(new LiveTestBody(document.getId(), Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                        document.getString("TestInHistory"),
                                        Objects.requireNonNull(document.getLong("duration")).intValue(),
                                        Objects.requireNonNull(document.getLong("marks")).intValue(),
                                        document.getLong("rTime"),
                                        document.getLong("sTime"),
                                        document.getString("title")));
                        }
                    }

                    Collections.sort(testsLive, new Comparator<LiveTestBody>() {
                        @Override
                        public int compare(LiveTestBody o1, LiveTestBody o2) {
                            Long s1 = o1.sTime;
                            Long s2 = o2.sTime;
                            return s1.compareTo(s2);
                        }
                    });

                    for(LiveTestBody ltb : testsLive){
                        if(ltb._id.equals(utID)){
                            FillLiveTestList(ltb._id, 0, ltb.title, ltb.NOQs, ltb.duration, ltb.marks, ltb.sTime, ltb.rTime);
                        }else if(ltb.TestInHistory == "true"){
                            FillLiveTestList(ltb._id, 2, ltb.title, ltb.NOQs, ltb.duration, ltb.marks, ltb.sTime, ltb.rTime);
                        }else{
                            FillLiveTestList(ltb._id, 1, ltb.title, ltb.NOQs, ltb.duration, ltb.marks, ltb.sTime, ltb.rTime);
                        }
                    }

                    new CountDownTimer(
                            upcomingTest.sTime - Calendar.getInstance().getTimeInMillis(), 1000) {

                        public void onTick(long millisUntilFinished) {
                            long secs = millisUntilFinished / 1000;
                            long min = secs / 60;
                            secs %= 60;
                            countdown.setText("Test start in " + (min > 9 ? min : "0"+min) + ":" + (secs > 9 ? secs : "0"+secs));
                        }

                        public void onFinish() {
                            countdown.setVisibility(View.GONE);
                            TestIsLiveTag.setVisibility(View.VISIBLE);
                            //show start test btn...
                            if(attemptedTestIDs.contains(utID)){
                                countdown.setText("You have successfully attempted the test. Wait for the result!");
                                //startTestBtn.setVisibility(View.INVISIBLE);
                            }else{
                                utAvailable = true;
                                new CountDownTimer(
                                        (upcomingTest.sTime + (upcomingTest.duration*60*1000)) - Calendar.getInstance().getTimeInMillis(), 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        long secs = millisUntilFinished / 1000;
                                        long min = secs / 60;
                                        secs %= 60;
                                        countdown.setText("Test ends in " + (min > 9 ? min : "0"+min) + ":" + (secs > 9 ? secs : "0"+secs));
                                    }
                                    public void onFinish() {
                                        utAvailable = false;
                                        countdown.setVisibility(View.GONE);
                                        TestIsLiveTag.setVisibility(View.INVISIBLE);
                                        //hide start test btn...
                                        //startTestBtn.setVisibility(View.INVISIBLE);
                                        //move test to history...
                                    }
                                }.start();
                            }
                        }
                    }.start();

                    /*ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                            R.layout.activity__branch_list_view, prevLives);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(mContext, "clicked: "+prevLives.get(i)+": "+prevIDs.get(i), Toast.LENGTH_SHORT).show();
                            //get question list...
                            final String tid = prevIDs.get(i);
                            FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests").document("002").collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        these_questions = new ArrayList<>();
                                        Toast.makeText(mContext, tid+" task successful: "+task.getResult().size(), Toast.LENGTH_SHORT).show();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Toast.makeText(mContext, "fetched: "+document.getId(), Toast.LENGTH_SHORT).show();
                                            these_questions.add(new Question(document.getString("question"), document.getString("option1"),
                                                    document.getString("option2"), document.getString("option3"),
                                                    document.getString("option4"), document.getString("answer")));
                                        }
                                        Intent intent = new Intent(mContext, TestAnswerSheetActivity.class);
                                        intent.putExtra("questions", (Serializable) these_questions);
                                        intent.putExtra("tid", tid);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(mContext, "error getting answer sheet", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "Error getting answer sheet: ", task.getException());
                                    }
                                }
                            });
                        }
                    });

                    ArrayAdapter adapter2 = new ArrayAdapter<String>(mContext,
                            R.layout.activity__branch_list_view, futureTests);
                    listView2.setAdapter(adapter2);*/
                    progressOverlay.setVisibility(View.GONE);
                }
            }
        });
    }

    public void StartLiveTest(){
        progressOverlay.setVisibility(View.VISIBLE);
        questions = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests").document(""+utID).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                    }

                    Intent myIntent = new Intent(mContext, TestInstructionsActivity.class);
                    myIntent.putExtra("section", "lt");
                    myIntent.putExtra("questions", (Serializable) questions);
                    myIntent.putExtra("_mpq", upcomingTest.marks);
                    myIntent.putExtra("timer", (upcomingTest.sTime/*+(upcomingTest.duration*60*1000)*/));
                    myIntent.putExtra("duration", upcomingTest.duration);
                    myIntent.putExtra("title", upcomingTest.title + " (Live Test)");
                    myIntent.putExtra("tid", utID);
                    progressOverlay.setVisibility(View.GONE);
                    startActivity(myIntent);
                } else {
                    progressOverlay.setVisibility(View.GONE);
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}

class LiveTestBody{
    public String _id;
    public int NOQs;
    public String TestInHistory;
    public int duration;
    public int marks;
    public long rTime;
    public long sTime;
    public String title;

    public LiveTestBody(String _id, int NOQs, String testInHistory, int duration, int marks, long rTime, long sTime, String title) {
        this._id = _id;
        this.NOQs = NOQs;
        TestInHistory = testInHistory;
        this.duration = duration;
        this.marks = marks;
        this.rTime = rTime;
        this.sTime = sTime;
        this.title = title;
    }
}