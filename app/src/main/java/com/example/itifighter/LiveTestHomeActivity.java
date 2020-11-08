package com.example.itifighter;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    ArrayList<LiveTestBody> allTests;
    private LiveTestBody upcomingTest;
    private String utID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_test_home);
        mContext = getApplicationContext();
        Toast.makeText(mContext, "ltha: onCreate", Toast.LENGTH_SHORT).show();
        progressOverlay = findViewById(R.id.progress_overlay);
        ltList = findViewById(R.id.LiveTestList);
        progressOverlay.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, "ltha: progress activated in oncreate", Toast.LENGTH_SHORT).show();
        colRef = FirebaseFirestore.getInstance().collection("section").document("lt")
                .collection("branch").document("" + getIntent().getStringExtra("subject"))
                .collection("chapter").document("" + getIntent().getStringExtra("chapter"))
                .collection("tests");


        attemptedTestIDs = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("scoreboard").document("lt")
                .collection("branch").document("" + getIntent().getStringExtra("subject"))
                .collection("chapter").document("" + getIntent().getStringExtra("chapter"))
                .collection("test").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        attemptedTestIDs.add(document.getId());
                    }
                }
                CustomizeView();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void FillLiveTestList(final String _id, int phase, final String title, int noq, final int min, final int marks, long start, long result) {
        Calendar st = Calendar.getInstance();
        st.setTimeInMillis(start);
        String sTime = "" + st.get(Calendar.DAY_OF_MONTH) + "/" + (st.get(Calendar.MONTH) + 1) + "/" + st.get(Calendar.YEAR) + " " + st.get(Calendar.HOUR) + ":" + st.get(Calendar.MINUTE) + " am";

        Calendar rt = Calendar.getInstance();
        rt.setTimeInMillis(result);
        String rTime = "" + rt.get(Calendar.DAY_OF_MONTH) + "/" + (rt.get(Calendar.MONTH) + 1) + "/" + rt.get(Calendar.YEAR) + " " + rt.get(Calendar.HOUR) + ":" + rt.get(Calendar.MINUTE) + " am";

        View ltRow;
        ltRow = View.inflate(this, R.layout.activity_live_test_xyz, null);
        ((TextView) ltRow.findViewById(R.id.ltTitle)).setText(title);
        ((TextView) ltRow.findViewById(R.id.ltQMM)).setText("" + noq + " Qs  |  " + min + " Min's  |  " + marks + " Marks");
        ((TextView) ltRow.findViewById(R.id.ltSTRT)).setText("start: " + sTime + "   |   result: " + rTime);
        if (phase == 0) {
            if (!attemptedTestIDs.contains(_id)) {
                //haven't attempted this live test yet
                if (countdown == null) {
                    countdown = ltRow.findViewById(R.id.upcomingCountDown);
                    countdown.setText("countdown");
                }
                TestIsLiveTag = ltRow.findViewById(R.id.TestIsLiveTAG);
                utAvailable = false;
                ltRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StartLiveTest();
                    }
                });
            } else {
                ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setText("attempted...");
            }
            ltList.addView(ltRow, 0);
        } else if (phase == 1) {
            //future test
            ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setText("coming soon...");
            ltList.addView(ltRow, ftCount + 1);
            ftCount++;
        } else {
            //past test
            if (result <= Calendar.getInstance().getTimeInMillis()) {
                ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setText("expired...");
                ltRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //LoadPastTestResult(_id);
                        LoadPastResult(_id, min, title);
                    }
                });
            } else {
                ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setText("pending result...");
            }
            ltList.addView(ltRow);
        }
    }

    public void LoadPastResult(final String pid, final int min, final String title) {

        final String tid = pid;

        final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document("" + uuid)
                .collection("scoreboard").document("lt")
                .collection("test").document(pid);

        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot valuee, @Nullable FirebaseFirestoreException error) {
                if (valuee != null && valuee.exists()) {
                    final DocumentSnapshot value = valuee;
                    colRef.document("" + tid).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                these_questions = new ArrayList<>();
                                Toast.makeText(mContext, tid + " task successful: " + Objects.requireNonNull(task.getResult()).size(), Toast.LENGTH_SHORT).show();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Toast.makeText(mContext, "fetched: " + document.getId(), Toast.LENGTH_SHORT).show();
                                    these_questions.add(new Question(document.getString("question"), document.getString("option1"),
                                            document.getString("option2"), document.getString("option3"),
                                            document.getString("option4"), document.getString("answer")));
                                }


                                String total_attempted = "", total_skipped = "", total_correct = "";
                                String sub_list = "";
                                String accuracy = "", tpq = "", _mpq = "";
                                total_attempted = value.getString("total_attempted");
                                total_skipped = value.getString("total_skipped");
                                total_correct = value.getString("total_correct");
                                accuracy = value.getString("accuracy");
                                tpq = value.getString("tpq");
                                _mpq = value.getString("_mpq");
                                sub_list = value.getString("answer_key");


                                Intent myIntent = new Intent(getApplicationContext(), TestResultActivity.class);
                                myIntent.putExtra("is_past_result", "true");
                                myIntent.putExtra("total_skipped", total_skipped);
                                myIntent.putExtra("total_attempted", total_attempted);
                                myIntent.putExtra("total_correct", total_correct);
                                myIntent.putExtra("section", "lt");
                                myIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                                myIntent.putExtra("chapter", getIntent().getStringExtra("chapter"));
                                myIntent.putExtra("tid", pid);
                                myIntent.putExtra("questions", /*questions*/(Serializable) these_questions);
                                myIntent.putExtra("answer_key", sub_list);
                                assert _mpq != null;
                                myIntent.putExtra("_mpq", _mpq);
                                myIntent.putExtra("timer", min);
                                myIntent.putExtra("title", title);
                                myIntent.putExtra("accuracy", accuracy);
                                myIntent.putExtra("tpq", tpq);
                                startActivity(myIntent);


                            } else {
                                Toast.makeText(mContext, "error getting answer sheet", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Error getting answer sheet: ", task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(mContext, "error fetching test", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CustomizeView() {
        //0: get attempted ids from user
        //1: get all tests and sort by start time (making it in order of future-live-past)
        //2: list of past tests with declared results
        //3: list of past tests with pending results
        //4: list of upcoming tests other than latest one that's live
        //5: latest upcoming test that's live
        //6: latest test that's live but attempted already

        Toast.makeText(mContext, "ltha: customize view", Toast.LENGTH_SHORT).show();

        //..0..\\
        final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final CollectionReference userDoc = FirebaseFirestore.getInstance()
                .collection("users").document("" + uuid)
                .collection("scoreboard").document("lt")
                .collection("test");

        userDoc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                attemptedTestIDs = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult())
                        attemptedTestIDs.add(doc.getId());
                }
                BeginFetchingTests();
            }
        });
    }

    private void BeginFetchingTests() {
        allTests = new ArrayList<>();
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //..1..\\
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        allTests.add(new LiveTestBody(document.getId(), Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                document.getString("TestInHistory"),
                                Objects.requireNonNull(document.getLong("duration")).intValue(),
                                Objects.requireNonNull(document.getLong("marks")).intValue(),
                                document.getLong("rTime"),
                                document.getLong("sTime"),
                                document.getString("title")));
                    }

                    Collections.sort(allTests, new Comparator<LiveTestBody>() {
                        @Override
                        public int compare(LiveTestBody o1, LiveTestBody o2) {
                            Long s1 = o1.sTime;
                            Long s2 = o2.sTime;
                            return s1.compareTo(s2);
                        }
                    });

                    //finding the nearest live test
                    for (LiveTestBody ltb : allTests) {
                        if (!ltb.TestInHistory.equals("true") && ((ltb.sTime + (ltb.duration * 60 * 1000)) > Calendar.getInstance().getTimeInMillis())) {
                            if (upcomingTest == null || upcomingTest.sTime > ltb.sTime) {
                                utID = ltb._id;
                                upcomingTest = ltb;
                            }
                        }
                    }

                    if (upcomingTest != null) {
                        FillLiveTestList(upcomingTest._id, 0, upcomingTest.title, upcomingTest.NOQs,
                                upcomingTest.duration, upcomingTest.marks, upcomingTest.sTime, upcomingTest.rTime);
                    }

                    final long currentTime = Calendar.getInstance().getTimeInMillis();
                    boolean one = false;
                    for (LiveTestBody ltb : allTests) {
                        if(upcomingTest == null || !ltb._id.equals(utID)){
                            if (ltb.TestInHistory.equals("true") || ((ltb.sTime + (ltb.duration * 60 * 100)) <= Calendar.getInstance().getTimeInMillis())) {
                                //past with/without result declared
                                FillLiveTestList(ltb._id, 2, ltb.title, ltb.NOQs, ltb.duration, ltb.marks, ltb.sTime, ltb.rTime);
                            } else {
                                FillLiveTestList(ltb._id, 1, ltb.title, ltb.NOQs, ltb.duration, ltb.marks, ltb.sTime, ltb.rTime);
                            }
                        }
                    }

                    if (upcomingTest == null || countdown == null) {
                        progressOverlay.setVisibility(View.GONE);
                        return;
                    }
                    new CountDownTimer(
                            upcomingTest.sTime - Calendar.getInstance().getTimeInMillis(), 1000) {

                        @SuppressLint("SetTextI18n")
                        public void onTick(long millisUntilFinished) {
                            long secs = millisUntilFinished / 1000;
                            long min = secs / 60;
                            secs %= 60;
                            countdown.setText("Test start in " + (min > 9 ? min : "0" + min) + ":" + (secs > 9 ? secs : "0" + secs));
                        }

                        @SuppressLint("SetTextI18n")
                        public void onFinish() {
                            countdown.setVisibility(View.GONE);
                            TestIsLiveTag.setVisibility(View.VISIBLE);
                            //show start test btn...
                            if (attemptedTestIDs.contains(utID)) {
                                countdown.setText("You have successfully attempted the test. Wait for the result!");
                                //startTestBtn.setVisibility(View.INVISIBLE);
                            } else {
                                utAvailable = true;

                                new CountDownTimer(
                                        (upcomingTest.sTime + (upcomingTest.duration * 60 * 1000)) - Calendar.getInstance().getTimeInMillis(), 1000) {

                                    @SuppressLint("SetTextI18n")
                                    public void onTick(long millisUntilFinished) {
                                        long secs = millisUntilFinished / 1000;
                                        long min = secs / 60;
                                        secs %= 60;
                                        countdown.setText("Test ends in " + (min > 9 ? min : "0" + min) + ":" + (secs > 9 ? secs : "0" + secs));
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
                    progressOverlay.setVisibility(View.GONE);
                } else {
                    Toast.makeText(mContext, "ltha: error in fetching tests in customize view", Toast.LENGTH_SHORT).show();
                    progressOverlay.setVisibility(View.GONE);
                }
            }
        });
    }

    public void StartLiveTest() {
        if (!utAvailable) {
            Toast.makeText(mContext, "test not available at the moment.", Toast.LENGTH_LONG).show();
            return;
        }

        progressOverlay.setVisibility(View.VISIBLE);
        questions = new ArrayList<>();
        colRef.document("" + utID).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                    }

                    Intent myIntent = new Intent(mContext, TestInstructionsActivity.class);
                    myIntent.putExtra("section", "lt");
                    myIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                    myIntent.putExtra("chapter", getIntent().getStringExtra("chapter"));
                    myIntent.putExtra("questions", (Serializable) questions);
                    myIntent.putExtra("_mpq", upcomingTest.marks);
                    myIntent.putExtra("timer", (upcomingTest.sTime/*+(upcomingTest.duration*60*1000)*/));
                    myIntent.putExtra("duration", upcomingTest.duration);
                    myIntent.putExtra("title", upcomingTest.title + " (Live Test)");
                    myIntent.putExtra("tid", utID);
                    progressOverlay.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "section, subject, chap, test: lt," + getIntent().getStringExtra("subject") + "," + getIntent().getStringExtra("chapter") + "," + utID, Toast.LENGTH_LONG).show();

                    startActivity(myIntent);
                } else {
                    progressOverlay.setVisibility(View.GONE);
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}

class LiveTestBody {
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