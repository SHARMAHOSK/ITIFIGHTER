package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    private ArrayList<Question> questions, these_questions;
    private ArrayList<String> attemptedTestIDs; //tests that student has taken in past.
    ArrayList<LiveTestBody> allTests;
    private LiveTestBody upcomingTest;
    private String utID = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_test_home);
        mContext = getApplicationContext();
        ltList = findViewById(R.id.LiveTestList);
        colRef = FirebaseFirestore.getInstance().collection("section").document("lt")
                .collection("branch").document("" + getIntent().getStringExtra("subject"))
                .collection("chapter").document("" + getIntent().getStringExtra("chapter"))
                .collection("tests");

        final TextView cbt = findViewById(R.id.ContinueBTNLBT);
        cbt.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= cbt.getRight() - cbt.getTotalPaddingRight()) {
                        finish();
                        return true;
                    }
                }
                return true;
            }
        });

        attemptedTestIDs = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser()).getUid()).collection("scoreboard").document("lt")
                .collection("branch").document("" + getIntent().getStringExtra("subject"))
                .collection("chapter").document("" + getIntent().getStringExtra("chapter"))
                .collection("test").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        attemptedTestIDs.add(
                                xNull(document.getId())
                        );
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
                ltRow.findViewById(R.id.upcomingCountDown).setBackgroundResource(R.color.green);
            }
            ltList.addView(ltRow, 0);
        } else if (phase == 1) {
            //future test
            ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setText("coming soon...");
            ltRow.findViewById(R.id.upcomingCountDown).setBackgroundResource(R.color.yellow);
            ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setTextColor(getResources().getColor(R.color.black));
            ltList.addView(ltRow, ftCount + 1);
            ftCount++;
        } else {
            //past test
            if (result <= Calendar.getInstance().getTimeInMillis()) {
                ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setText("expired...");
                ltRow.findViewById(R.id.upcomingCountDown).setBackgroundResource(R.color.red);
                ltRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //LoadPastTestResult(_id);
                        LoadPastResult(_id, min, title);
                    }
                });
            } else {
                ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setText("pending result...");
                ltRow.findViewById(R.id.upcomingCountDown).setBackgroundResource(R.color.yellow);
                ((TextView) ltRow.findViewById(R.id.upcomingCountDown)).setTextColor(getResources().getColor(R.color.black));
            }
            ltList.addView(ltRow);
        }
    }

    public void LoadPastResult(final String pid, final int min, final String title) {

        final String uuid = xNull((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid());
        final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document("" + uuid)
                .collection("scoreboard").document("lt")
                .collection("test").document(pid);

        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot valuee, @Nullable FirebaseFirestoreException error) {
                if (valuee != null && valuee.exists()) {
                    final DocumentSnapshot value = valuee;
                    colRef.document(xNull(pid)).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                these_questions = new ArrayList<>();
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    these_questions.add(
                                            new Question(
                                                    xNull(document.getString("question")),
                                                    xNull(document.getString("option1")),
                                                    xNull(document.getString("option2")),
                                                    xNull(document.getString("option3")),
                                                    xNull(document.getString("option4")),
                                                    xNull(document.getString("answer"))));
                                }

                                if(these_questions.isEmpty()){
                                    Toast.makeText(mContext, "selected test contains 0 questions..", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                String total_attempted, total_skipped, total_correct;
                                String sub_list;
                                String accuracy, tpq, _mpq;
                                total_attempted = xNull(value.getString("total_attempted"));
                                total_skipped = xNull(value.getString("total_skipped"));
                                total_correct = xNull(value.getString("total_correct"));
                                accuracy = xNull(value.getString("accuracy"));
                                tpq = xNull(value.getString("tpq"));
                                _mpq = xNull(value.getString("_mpq"));
                                sub_list = xNull(value.getString("answer_key"));

                                Intent myIntent = new Intent(getApplicationContext(), TestResultActivity.class);
                                myIntent.putExtra("is_past_result", "true");
                                myIntent.putExtra("total_skipped", total_skipped);
                                myIntent.putExtra("total_attempted", total_attempted);
                                myIntent.putExtra("total_correct", total_correct);
                                myIntent.putExtra("section", "lt");
                                myIntent.putExtra("subject", xNull(getIntent().getStringExtra("subject")));
                                myIntent.putExtra("chapter", xNull(getIntent().getStringExtra("chapter")));
                                myIntent.putExtra("tid", pid);
                                myIntent.putExtra("questions",these_questions);
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

        //..0..\\
        final String uuid = xNull((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid());
        final CollectionReference userDoc = FirebaseFirestore.getInstance()
                .collection("users").document(uuid)
                .collection("scoreboard").document("lt")
                .collection("test");

        userDoc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                attemptedTestIDs = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult()))
                        attemptedTestIDs.add(xNull(doc.getId()));
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
                        allTests.add(
                                new LiveTestBody(
                                        xNull(document.getId()),
                                        Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                        xNull(document.getString("TestInHistory")),
                                        Objects.requireNonNull(document.getLong("duration")).intValue(),
                                        Objects.requireNonNull(document.getLong("marks")).intValue(),
                                        lNull(document.getLong("rTime")),
                                        lNull(document.getLong("sTime")),
                                        xNull(document.getString("title"))));
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

                    //final long currentTime = Calendar.getInstance().getTimeInMillis();

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
                                        countdown.setBackgroundResource(R.color.green);
                                        countdown.setTextColor(getResources().getColor(R.color.white));
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
                }
            }
        });
    }

    public void StartLiveTest() {
        if (!utAvailable) {
            Toast.makeText(mContext, "test not available at the moment.", Toast.LENGTH_LONG).show();
            return;
        }
        questions = new ArrayList<>();
        colRef.document("" + utID).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                        xNull(document.getString("answer"))));
                    }

                    Intent myIntent = new Intent(mContext, TestInstructionsActivity.class);
                    myIntent.putExtra("section", "lt");
                    myIntent.putExtra("subject", xNull(getIntent().getStringExtra("subject")));
                    myIntent.putExtra("chapter", xNull(getIntent().getStringExtra("chapter")));
                    myIntent.putExtra("questions", questions);
                    myIntent.putExtra("_mpq", iNull(upcomingTest.marks));
                    myIntent.putExtra("timer", lNull(upcomingTest.sTime));
                    myIntent.putExtra("duration", iNull(upcomingTest.duration));
                    myIntent.putExtra("title", xNull(upcomingTest.title) + " (Live Test)");
                    myIntent.putExtra("tid", utID);
                    startActivity(myIntent);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
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

    public String aNull(int value){
        String num = "0";
        if(iNull(value)>0){
            try{
                num =  String.valueOf(value);
            }catch (Exception e){
                num = "0";
            }
        }
        return num;
    }


    public long lNull(long value){
        if(value<1)return 0;
        else return value;
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