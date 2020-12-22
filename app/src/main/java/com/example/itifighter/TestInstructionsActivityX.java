package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestInstructionsActivityX extends AppCompatActivity {
    TextView tQues, tMarks, tMin,examTitle;
    CheckBox insCB;
    List<Question> questions;
    List<String> quetionId;
    FirebaseFirestore db;
    String quetion,marks,time,testName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction_testseries);
        db = FirebaseFirestore.getInstance();
        String currentSubject = getIntent().getStringExtra("currentSubject");
        String currentChapter = getIntent().getStringExtra("currentChapter");
        String currentTest = getIntent().getStringExtra("currentTest");
        assert currentChapter != null;
        assert currentSubject != null;
        assert currentTest != null;
        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("exam").document(currentChapter).collection("tests").document(currentTest).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    quetion = documentSnapshot.getString("qutions");
                    marks = documentSnapshot.getString("score");
                    time = documentSnapshot.getString("duration");
                    testName = documentSnapshot.getString("name");
                    if(documentSnapshot==null)Toast.makeText(TestInstructionsActivityX.this,"null value",Toast.LENGTH_SHORT).show();
                    else Toast.makeText(TestInstructionsActivityX.this,quetion+marks+time+testName,Toast.LENGTH_SHORT).show();
                }
            }
        });
        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("chapter").document(currentChapter).collection("tests").document(currentTest).collection("quetions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    questions = new ArrayList<>();
                    quetionId = new ArrayList<>();
                    for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                        quetionId.add(document.getId());
                    }
                }
            }
        });
        tQues = findViewById(R.id.TQuesX);
        tMarks = findViewById(R.id.TMarksY);
        tMin = findViewById(R.id.TMinZ);
        examTitle = findViewById(R.id.examTitle);
        tQues.setText("50");
        tMarks.setText("10");
        tMin.setText("20");
        //instructionTV = findViewById(R.id.InstructionText);
        insCB = findViewById(R.id.InsCBX);
        examTitle.setText("ibps");
       /* instructionTV.setText("Read each question carefully.\n" +
                "You cannot skip a question. You must provide an answer to each question to proceed with the test.\n" +
                "Once answered, you cannot go back to a question. You will be given an opportunity to review questions at the end of the test.\n" +
                "If you exit the test, you will not be able to resume your place in the test. You will have to start from the beginning.\n" +
                "There may be tools available (i.e., glossary, calculator, etc.) to help you answer the questions. You can access this information by clicking in the tools bar that may be located above the questions.\n" +
                "There may be support information that is required to answer the questions. You can access this information by clicking on \"Show Support Information\" located in the question box.\n" +
                "Your test will not be timed. You can take as much time as you need to complete this test. Please keep in mind, however, that the actual test that you take to gain employment with the County of Los Angeles will be timed.\n" +
                "This practice test was designed to build upon a basic understanding of the subject area being tested. It was not intended to serve as the sole instruction method or initial exposure to the subject area. Instead, it should serve as a refresher mechanism in your examination preparation. If more in-depth training is required, visit the Local Resources section of this site to locate colleges and organizations that may provide additional training.\n" +
                "For more detailed instructions, please visit our System Overview section.\n" +
                "This test is only a guide. The test questions that you complete during your actual employment test may vary in format, content, and level of difficulty.\n");*/
    }

    public void BeginTest(View view) {
        if(insCB.isChecked()){
            Intent myIntent = new Intent(TestInstructionsActivityX.this, TestQuestionsActivity.class);
            myIntent.putExtra("questions", (Parcelable) questions);
            myIntent.putExtra("_mpq",Integer.parseInt(marks));
            myIntent.putExtra("timer",Integer.parseInt(time));
            startActivity(myIntent);
        }else{
            Toast.makeText(this, "Please agree to the terms and conditions in order to proceed with test", Toast.LENGTH_SHORT).show();
        }
    }
}