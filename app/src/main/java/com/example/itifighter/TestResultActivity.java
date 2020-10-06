package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestResultActivity extends AppCompatActivity {

    List<Question> questions;
    int[] sub_ans, selectedFeedbackOption;
    int total_marks = 0;
    long timeLeft;
    double accuracy = 0.0;
    int tpq = 0;
    int marks_obtained = 0;
    int _mpq = 1;
    String[] feedbackOptions = { "Wrong Question", "Wrong Options", "Incomplete Question", "Incorrect Grammar", "Question out of syllabus",
            "Question on old pattern", "Repeated Question"};
    boolean marksUploaded = false;

    int tca = 0, tra = 0, tsq = 0;
    TextView MO, TM, TCA, TRA, TSQ, Accuracy, TimePerQuestion;
    String targetSection, targetSubject, targetChapter;
    String finalTCID;

    CollectionReference mDatabaseReference;

    @SuppressLint({"WrongViewCast", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_xyz);



        findViewById(R.id.ResultLL).setVisibility(View.INVISIBLE);
        findViewById(R.id.UploadingTXT).setVisibility(View.VISIBLE);

        final TextView cbt = findViewById(R.id.ContinueBTNRT);
        cbt.setVisibility(View.INVISIBLE);
        cbt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= cbt.getRight() - cbt.getTotalPaddingRight()) {
                        // your action for drawable click event
                        FinishExam();
                        return true;
                    }
                }
                return true;
            }
        });

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("chapter");
        timeLeft = getIntent().getLongExtra("timeLeft", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        _mpq = getIntent().getIntExtra("_mpq", 1);

        finalTCID = targetSection.equals("lt") ? getIntent().getStringExtra("tid") : targetChapter;

        if(targetSection.equals("lt")){
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("tests").document(finalTCID).collection("scoreboard");
        }else{
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(finalTCID).collection("scoreboard");

        }

        MO = findViewById(R.id.marksObtained);
        TM = findViewById(R.id.totalMarks);
        TCA = findViewById(R.id.TCA);
        TRA = findViewById(R.id.TRA);
        TSQ = findViewById(R.id.TSQ);
        Accuracy = findViewById(R.id.Accuracy);
        TimePerQuestion = findViewById(R.id.TimePerQuestion);


        total_marks = _mpq * questions.size();
        TM.setText(""+(questions.size() * _mpq));

        int total_time_taken = 0;

        if(getIntent().getStringExtra("is_past_result") != null && getIntent().getStringExtra("is_past_result").contains("true")){
            marksUploaded = true;
            tsq = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_skipped")));
            tca = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_correct")));
            tra = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_attempted"))) - tca;
            String sub_list = Objects.requireNonNull(getIntent().getStringExtra("answer_key"));
            _mpq = getIntent().getIntExtra("_mpq", 1);
            String[] str_ans = sub_list.split("_");
            sub_ans = new int[str_ans.length];
            for(int i=0; i<str_ans.length; i++)
                sub_ans[i] = Integer.parseInt(str_ans[i]);
            findViewById(R.id.UploadingTXT).setVisibility(View.INVISIBLE);
            findViewById(R.id.ContinueBTNRT).setVisibility(View.VISIBLE);

            if(targetSection == "lt"){

            }else{
                findViewById(R.id.ResultLL).setVisibility(View.VISIBLE);
            }
            accuracy = Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("accuracy")));
            total_time_taken = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("tpq")));

        }else{
            sub_ans = getIntent().getIntArrayExtra("sub_ans");
            selectedFeedbackOption = getIntent().getIntArrayExtra("selectedFeedbackOption");
            //Toast.makeText(this, "total ques: total ans: "+questions.size()+" : "+sub_ans.length, Toast.LENGTH_LONG).show();

            for(int i=0; i< sub_ans.length; i++){
                //Toast.makeText(this, ""+questions.get(i).getAnswer(), Toast.LENGTH_LONG).show();
                if(sub_ans[i] == -1){
                    tsq++;
                }
                else if(sub_ans[i] == Integer.parseInt(questions.get(i).getAnswer())){
                    marks_obtained += _mpq;
                    tca++;
                }else{
                    tra++;
                }
            }
             total_time_taken = 0;
            accuracy = ((((double)tca)/(tca+tra))*100.0);
            accuracy = new BigDecimal(accuracy).setScale(2, RoundingMode.HALF_UP).doubleValue();
            if(tca+tra > 0){
                Accuracy.setText(""+accuracy);
                total_time_taken = Math.toIntExact(Math.round((((getIntent().getIntExtra("timer", 60)*60*1000) - timeLeft)/(1000.0))/((double)(tca+tra))));
            }
            tpq = total_time_taken;

            UploadData();
        }


        int timerValue = getIntent().getIntExtra("timer", 60);
        Toast.makeText(this, "tca="+tca+" tra="+tra+" timerValue="+timerValue+" timeLeft="+timeLeft, Toast.LENGTH_SHORT).show();
        if(tca+tra > 0){
            Accuracy.setText(""+accuracy);
        }
        else{
            Accuracy.setText("0");
        }
        TimePerQuestion.setText(""+total_time_taken);
        MO.setText(""+(tca * _mpq));
        TCA.setText(""+tca+" Correct");
        TRA.setText(""+tra+" Incorrect");;
        TSQ.setText(""+tsq+" Skipped");
    }

    void UploadData(){
        final String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(""+uuid);
        final DocumentReference UserTestRecordDoc = userDoc.collection("scoreboard").document(""+targetSection).collection("test").document(""+finalTCID);
        final float percentageMarks = (tca * _mpq)/total_marks;
        double userRecordScore = targetSection.equals("mt") ? percentageMarks : targetSection.equals("lt") ? percentageMarks*2 : percentageMarks*1.5;

        Map<String, String> userTestRecordMap = new HashMap<>();
        userTestRecordMap.put("score", ""+userRecordScore);
        userTestRecordMap.put("total_skipped", ""+tsq);
        userTestRecordMap.put("total_attempted", ""+(tca+tra));
        userTestRecordMap.put("total_correct", ""+tca);
        userTestRecordMap.put("_mpq", ""+_mpq);
        userTestRecordMap.put("accuracy", ""+accuracy);
        userTestRecordMap.put("tpq", ""+tpq);
        String answerString = "";
        for(int n : sub_ans)
            answerString+=n+"_";
        userTestRecordMap.put("answer_key", answerString.substring(0, answerString.length()-1));

        UserTestRecordDoc.set(userTestRecordMap);

        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    final String studentName = snapshot.getString("Name");
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                    Map<String, String> scoreboard = new HashMap<>();
                    scoreboard.put("Score", ""+(tca * _mpq));
                    scoreboard.put("name", ""+studentName);
                    DocumentReference reference = mDatabaseReference.document(""+ uuid);
                    reference.set(scoreboard).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(TestResultActivity.this, "score uploaded in database for user: "+FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            marksUploaded = true;
                            //((TextView)findViewById(R.id.UploadingTXT)).setText("uploading feedback, please wait..");
                            //final CollectionReference feedbackBasePath = FirebaseFirestore.getInstance().collection("common").document("post test").collection("feedback");
                            final CollectionReference feedbackBasePath = FirebaseFirestore.getInstance().collection("users").document(""+uuid).collection("feedback");
                            feedbackBasePath.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        int count = task.getResult().size();
                                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                        for(int i=0; i<selectedFeedbackOption.length; i++){
                                            if(selectedFeedbackOption[i] > -1){
                                                Map<String, String> _fb= new HashMap<>();
                                                _fb.put("question", questions.get(i).getQuestion());
                                                _fb.put("issue", feedbackOptions[selectedFeedbackOption[i]]);
                                                _fb.put("section", targetSection);
                                                _fb.put("subject", targetSubject);
                                                /*_fb.put("student", studentName);*/
                                                /*_fb.put("date", ""+Calendar.getInstance().getTimeInMillis());*/
                                                if(targetSection == "lt"){
                                                    _fb.put("testID", getIntent().getStringExtra("tid"));
                                                }else{
                                                    _fb.put("chapter", targetChapter);
                                                }
                                                //DocumentReference nycRef = feedbackBasePath.document("Feedback_" + (++count));
                                                DocumentReference nycRef = feedbackBasePath.document("Feedback_" + Calendar.getInstance().getTimeInMillis());
                                                batch.set(nycRef, _fb);
                                            }
                                        }
                                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                findViewById(R.id.UploadingTXT).setVisibility(View.INVISIBLE);
                                                findViewById(R.id.ContinueBTNRT).setVisibility(View.VISIBLE);

                                                if(targetSection == "lt"){

                                                }else{
                                                    findViewById(R.id.ResultLL).setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                                    }else{
                                        findViewById(R.id.UploadingTXT).setVisibility(View.INVISIBLE);
                                        findViewById(R.id.ContinueBTNRT).setVisibility(View.VISIBLE);

                                        if(targetSection == "lt"){

                                        }else{
                                            findViewById(R.id.ResultLL).setVisibility(View.VISIBLE);
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

    public void CheckLeaderBoard(View view){
        Intent intent = new Intent(this, TestLeaderBoardActivity.class);
        intent.putExtra("section", targetSection);
        intent.putExtra("subject", targetSubject);
        intent.putExtra("chapter", targetChapter);
        intent.putExtra("tid", finalTCID);
        startActivity(intent);
    }

    public void TakeARetest(View view){
        if(targetSection.contains("lt"))
            return;
        Intent myIntent = new Intent(this, TestInstructionsActivity.class);
        //clears all other activities from stack and makes the new one the root of stack
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("section", targetSection);
        myIntent.putExtra("subject", targetSubject);
        myIntent.putExtra("chapter", targetChapter);
        myIntent.putExtra("questions", (Serializable) questions);
        myIntent.putExtra("_mpq", _mpq);
        myIntent.putExtra("timer", getIntent().getIntExtra("timer", 60));
        myIntent.putExtra("title", getIntent().getStringExtra("title"));
        startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(TestResultActivity.this, MainDashboard.class));
        if(marksUploaded) finish();
        else {
            //Toast.makeText(this, "uploading marks, please wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void FinishExam() {
        //startActivity(new Intent(TestResultActivity.this, MainDashboard.class));
        if(marksUploaded) finish();
        else {
            //Toast.makeText(this, "uploading marks, please wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void CheckAnswerSheet(View view){
        Intent intent = new Intent(this, TestAnswerSheetActivity.class);
        intent.putExtra("questions", (Serializable) questions);
        intent.putExtra("answer_key", (Serializable) sub_ans);
        if(marksUploaded) startActivity(intent);
        else {
            //Toast.makeText(this, "uploading marks, please wait...", Toast.LENGTH_SHORT).show();
        }
    }
}