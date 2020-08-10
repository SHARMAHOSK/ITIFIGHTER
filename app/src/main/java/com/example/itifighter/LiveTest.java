package com.example.itifighter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveTest extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View ltView;
    TextView countdown;
    CollectionReference colRef;
    private Context mContext;
    private ArrayList<Question> questions;

    private ListView listView;
    ArrayList<String> prevLives;
    private LiveTestBody upcomingTest;

    Button startTestBtn;

    public LiveTest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveTest.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveTest newInstance(String param1, String param2) {
        LiveTest fragment = new LiveTest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        colRef = FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ltView = inflater.inflate(R.layout.fragment_live_test, container, false);
        listView = (ListView) ltView.findViewById(R.id.lt_prev_list);
        mContext = getContext();
        CustomizeView(ltView);
        return ltView;
    }

    private void CustomizeView(final View ltView) {
        //add list view in bottom to display previous tests list...

        startTestBtn  = ltView.findViewById(R.id.startTestBtn);

        countdown = ltView.findViewById(R.id.countdown);
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    prevLives = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("TestInHistory").equals("true")){
                            prevLives.add(document.getString("title"));
                        }else{
                            upcomingTest = new LiveTestBody(Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                    document.getString("TestInHistory"),
                                    Objects.requireNonNull(document.getLong("duration")).intValue(),
                                    Objects.requireNonNull(document.getLong("marks")).intValue(),
                                    document.getLong("rTime"),
                                    document.getLong("sTime"),
                                    document.getString("title"));
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
                            new CountDownTimer(
                                    (upcomingTest.sTime + upcomingTest.duration) - Calendar.getInstance().getTimeInMillis(), 1000) {

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
                    }.start();

                    ((TextView)ltView.findViewById(R.id.LiveTestTitle)).setText(upcomingTest.title);

                    ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                            R.layout.activity__branch_list_view, prevLives);
                    listView.setAdapter(adapter);
                }
            }
        });
    }

    public void StartLiveTest(View view){
        questions = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("section").document("lt").collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                    }

                    Intent myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                    myIntent.putExtra("section", "lt");
                    myIntent.putExtra("questions", (Serializable) questions);
                    myIntent.putExtra("_mpq", upcomingTest.marks);
                    myIntent.putExtra("timer", (upcomingTest.sTime+(upcomingTest.duration*60*1000)));
                    myIntent.putExtra("duration", upcomingTest.duration);
                    startActivity(myIntent);
                } else {
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