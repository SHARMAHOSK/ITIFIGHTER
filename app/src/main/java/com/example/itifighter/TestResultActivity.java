

package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TestResultActivity extends AppCompatActivity {

    List<Question> questions;
    int[] sub_ans, selectedFeedbackOption;
    int total_marks = 0;
    long timeLeft;
    double accuracy = 0.0;
    int _mpq = 1;
    boolean pastResultLT = false;
    String[] feedbackOptions = {"Wrong Question", "Wrong Options", "Incomplete Question", "Incorrect Grammar", "Question out of syllabus",
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


        final TextView cbt = findViewById(R.id.ContinueBTNRT);
        //cbt.setVisibility(View.INVISIBLE);
        cbt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= cbt.getRight() - cbt.getTotalPaddingRight()) {
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

        if (targetSection.equals("lt")) {
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("tests").document(finalTCID).collection("scoreboard");
        } else {
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
        TM.setText("" + (questions.size() * _mpq));

        int total_time_taken = 0;

        marksUploaded = true;
        pastResultLT = true;
        tsq = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_skipped")));
        tca = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_correct")));
        tra = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_attempted"))) - tca;
        String sub_list = Objects.requireNonNull(getIntent().getStringExtra("answer_key"));
        _mpq = getIntent().getIntExtra("_mpq", 1);
        String[] str_ans = sub_list.split("_");
        sub_ans = new int[str_ans.length];
        for (int i = 0; i < str_ans.length; i++)
            sub_ans[i] = Integer.parseInt(str_ans[i]);

        accuracy = Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("accuracy")));
        total_time_taken = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("tpq")));
        if (tca + tra > 0) {
            Accuracy.setText("" + accuracy);
        } else {
            Accuracy.setText("0");
        }
        TimePerQuestion.setText("" + total_time_taken);
        MO.setText("" + (tca * _mpq));
        TCA.setText("" + tca + " Correct");
        TRA.setText("" + tra + " Incorrect");
        TSQ.setText("" + tsq + " Skipped");
    }

    public void CheckLeaderBoard(View view) {
        Intent intent = new Intent(this, TestLeaderBoardActivity.class);
        intent.putExtra("section", targetSection);
        intent.putExtra("subject", targetSubject);
        intent.putExtra("chapter", targetChapter);
        intent.putExtra("tid", finalTCID);
        startActivity(intent);
    }

    public void TakeARetest(View view) {
        if (targetSection.contains("lt"))
            return;
        Intent myIntent = new Intent(this, TestInstructionsActivity.class);
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
        if (marksUploaded) finish();

    }

    public void FinishExam() {
        //startActivity(new Intent(TestResultActivity.this, MainDashboard.class));
        if (marksUploaded) finish();

    }

    public void CheckAnswerSheet(View view) {
        Intent intent = new Intent(this, TestAnswerSheetActivity.class);
        intent.putExtra("questions", (Serializable) questions);
        intent.putExtra("answer_key", (Serializable) sub_ans);
        if (marksUploaded) startActivity(intent);
    }
}