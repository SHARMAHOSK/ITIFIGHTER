package com.example.itifighter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LiveTestHomeActivity extends AppCompatActivity {

    TextView countdown;
    CollectionReference colRef;
    private Context mContext;
    private View progressOverlay;
    private ArrayList<Question> questions, these_questions;
    private ArrayList<String> attemptedTestIDs; //tests that student has taken in past.
    Button btnPrev, btnFuture;

    private ListView listView, listView2;
    ArrayList<String> prevLives, prevIDs;
    private LiveTestBody upcomingTest;
    private ArrayList<String> futureTests;
    private String utID = "";

    Button startTestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_test_home);
        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.setVisibility(View.VISIBLE);
        colRef = FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests");

        listView = (ListView) findViewById(R.id.lt_prev_list);
        listView2 = (ListView) findViewById(R.id.lt_future_list);
        btnPrev = findViewById(R.id.BtnPrevList);
        btnFuture = findViewById(R.id.BtnFutureList);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.lt_prev_list).setVisibility(View.VISIBLE);
                findViewById(R.id.lt_future_list).setVisibility(View.INVISIBLE);
            }
        });
        btnFuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.lt_prev_list).setVisibility(View.INVISIBLE);
                findViewById(R.id.lt_future_list).setVisibility(View.VISIBLE);
            }
        });

        mContext = getApplicationContext();
        attemptedTestIDs = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("scoreboard").document("lt").collection("test").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        attemptedTestIDs.add(document.getId());
                    }
                }
                CustomizeView();
            }
        });
        /*CustomizeView();*/
    }

    private void CustomizeView() {
        //add list view in bottom to display previous tests list...

        startTestBtn  = findViewById(R.id.startTestBtn);
        startTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartLiveTest();
            }
        });
        startTestBtn.setVisibility(View.INVISIBLE);

        countdown = findViewById(R.id.countdown);
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    prevLives = new ArrayList<>();
                    prevIDs = new ArrayList<>();
                    futureTests = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("TestInHistory").equals("true") && attemptedTestIDs.contains(document.getId())){
                            prevLives.add(document.getString("title"));
                            prevIDs.add(document.getId());
                        }else{
                            if(upcomingTest == null || upcomingTest.sTime > document.getLong("sTime")) {
                                utID = document.getId();
                                upcomingTest = new LiveTestBody(Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                        document.getString("TestInHistory"),
                                        Objects.requireNonNull(document.getLong("duration")).intValue(),
                                        Objects.requireNonNull(document.getLong("marks")).intValue(),
                                        document.getLong("rTime"),
                                        document.getLong("sTime"),
                                        document.getString("title"));
                            }
                            futureTests.add(document.getString("title"));
                        }
                    }
                    new CountDownTimer(
                            upcomingTest.sTime - Calendar.getInstance().getTimeInMillis(), 1000) {

                        public void onTick(long millisUntilFinished) {
                            long secs = millisUntilFinished / 1000;
                            long min = secs / 60;
                            secs %= 60;
                            countdown.setText("" + (min > 9 ? min : "0"+min) + ":" + (secs > 9 ? secs : "0"+secs));
                        }

                        public void onFinish() {
                            countdown.setText("live!");
                            startTestBtn.setVisibility(View.VISIBLE);
                            //show start test btn...
                            if(attemptedTestIDs.contains(utID)){
                                countdown.setText("You have successfully attempted the test. Wait for the result!");
                                startTestBtn.setVisibility(View.INVISIBLE);
                            }else{
                                new CountDownTimer(
                                        (upcomingTest.sTime + (upcomingTest.duration*60*1000)) - Calendar.getInstance().getTimeInMillis(), 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        long secs = millisUntilFinished / 1000;
                                        long min = secs / 60;
                                        secs %= 60;
                                        countdown.setText("Test ends in: " + (min > 9 ? min : "0"+min) + ":" + (secs > 9 ? secs : "0"+secs));
                                    }
                                    public void onFinish() {
                                        countdown.setText("Test has ended!");
                                        //hide start test btn...
                                        startTestBtn.setVisibility(View.INVISIBLE);
                                        //move test to history...
                                    }
                                }.start();
                            }
                        }
                    }.start();

                    ((TextView)findViewById(R.id.LiveTestTitle)).setText(upcomingTest.title);

                    ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
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
                    listView2.setAdapter(adapter2);
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
    public int NOQs;
    public String TestInHistory;
    public int duration;
    public int marks;
    public long rTime;
    public long sTime;
    public String title;

    public LiveTestBody(int NOQs, String testInHistory, int duration, int marks, long rTime, long sTime, String title) {
        this.NOQs = NOQs;
        TestInHistory = testInHistory;
        this.duration = duration;
        this.marks = marks;
        this.rTime = rTime;
        this.sTime = sTime;
        this.title = title;
    }
}