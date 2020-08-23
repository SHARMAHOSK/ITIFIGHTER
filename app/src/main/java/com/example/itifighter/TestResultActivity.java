package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultActivity extends AppCompatActivity {

    List<Question> questions;
    int[] sub_ans;
    int total_marks = 0;
    int marks_obtained = 0;
    int _mpq = 1;
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

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("chapter");

        if(targetSection.equals("lt")){
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("tests").document(""+getIntent().getStringExtra("tid")).collection("scoreboard");
        }else{
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("scoreboard");

        }

        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        _mpq = getIntent().getIntExtra("_mpq", 1);
        sub_ans = getIntent().getIntArrayExtra("sub_ans");
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

        Map<String, String> scoreboard = new HashMap<>();
        scoreboard.put("Score", ""+(tca * _mpq));
        scoreboard.put("Name", "La Belle Dame Sans Merci");
        DocumentReference reference = mDatabaseReference.document(""+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.set(scoreboard).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(TestResultActivity.this, "score uploaded in database for user: "+FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                marksUploaded = true;
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