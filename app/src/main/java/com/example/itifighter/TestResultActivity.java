package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultActivity extends AppCompatActivity {

    List<Question> questions;
    int[] sub_ans, selectedFeedbackOption;
    int total_marks = 0;
    int marks_obtained = 0;
    int _mpq = 1;
    String[] feedbackOptions = { "Wrong Question", "Wrong Options", "Incomplete Question", "Incorrect Grammar", "Question out of syllabus",
            "Question on old pattern", "Repeated Question"};
    boolean marksUploaded = false;

    int tca = 0, tra = 0, tsq = 0;
    TextView MO, TM, TCA, TRA, TSQ;
    String targetSection, targetSubject, targetChapter;

    private ListView listView;
    String[] result;
    CollectionReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        findViewById(R.id.ResultLL).setVisibility(View.INVISIBLE);
        findViewById(R.id.UploadingTXT).setVisibility(View.VISIBLE);
        findViewById(R.id.ContinueBTNRT).setVisibility(View.INVISIBLE);

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("chapter");

        if(targetSection.equals("lt")){
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("tests").document(""+getIntent().getStringExtra("tid")).collection("scoreboard");
        }else{
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("scoreboard");

        }

        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        _mpq = getIntent().getIntExtra("_mpq", 1);
        sub_ans = getIntent().getIntArrayExtra("sub_ans");
        selectedFeedbackOption = getIntent().getIntArrayExtra("selectedFeedbackOption");
        Toast.makeText(this, "total ques: total ans: "+questions.size()+" : "+sub_ans.length, Toast.LENGTH_LONG).show();
        result = new String[sub_ans.length];

        total_marks = _mpq * questions.size();

        /*listView = (ListView) findViewById(R.id.mt_result_list);*/
        MO = findViewById(R.id.marksObtained);
        TM = findViewById(R.id.totalMarks);
        TCA = findViewById(R.id.TCA);
        TRA = findViewById(R.id.TRA);
        TSQ = findViewById(R.id.TSQ);

        for(int i=0; i< sub_ans.length; i++){
            //result[i] = ""+i;
            Toast.makeText(this, ""+questions.get(i).getAnswer(), Toast.LENGTH_LONG).show();
            if(sub_ans[i] == -1){
                result[i] =  "skipped";
                tsq++;
            }
            else if(sub_ans[i] == Integer.parseInt(questions.get(i).getAnswer())){
                result[i] =  "right";
                marks_obtained += _mpq;
                tca++;
            }else{
                result[i] =  "wrong";
                tra++;
            }
        }

        MO.setText(""+(tca * _mpq));
        TM.setText(""+(questions.size() * _mpq));
        TCA.setText(""+tca);
        TRA.setText(""+tra);;
        TSQ.setText(""+tsq);


        final String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(""+uuid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                            Toast.makeText(TestResultActivity.this, "score uploaded in database for user: "+FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            marksUploaded = true;
                            //((TextView)findViewById(R.id.UploadingTXT)).setText("uploading feedback, please wait..");
                            CollectionReference feedbackBasePath = FirebaseFirestore.getInstance().collection("common").document("post test").collection("feedback");
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
                                                _fb.put("student", studentName);
                                                _fb.put("date", ""+Calendar.getInstance().getTimeInMillis());
                                                if(targetSection == "lt"){
                                                    _fb.put("testID", getIntent().getStringExtra("tid"));
                                                }else{
                                                    _fb.put("chapter", targetChapter);
                                                }
                                                DocumentReference nycRef = mDatabaseReference.document("Feedback_ " + (++count));
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

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(TestResultActivity.this, MainDashboard.class));
        if(marksUploaded) finish();
        else
            Toast.makeText(this, "uploading marks, please wait...", Toast.LENGTH_SHORT).show();
    }

    public void FinishExam(View view) {
        //startActivity(new Intent(TestResultActivity.this, MainDashboard.class));
        if(marksUploaded) finish();
        else
            Toast.makeText(this, "uploading marks, please wait...", Toast.LENGTH_SHORT).show();
    }

    public void CheckAnswerSheet(View view){
        Intent intent = new Intent(this, TestAnswerSheetActivity.class);
        intent.putExtra("questions", (Serializable) questions);
        if(marksUploaded) startActivity(intent);
        else
            Toast.makeText(this, "uploading marks, please wait...", Toast.LENGTH_SHORT).show();
    }
}