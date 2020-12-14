package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultUploader extends AppCompatActivity {
    List<Question> questions;
    int[] sub_ans, selectedFeedbackOption;
    int total_marks = 0;
    long timeLeft;
    double accuracy = 0.0;
    int tpq = 0;
    int timerValue = 0;
    int marks_obtained = 0;
    int _mpq = 1;
    String[] feedbackOptions = {"Wrong Question", "Wrong Options", "Incomplete Question", "Incorrect Grammar", "Question out of syllabus",
            "Question on old pattern", "Repeated Question"};
    boolean marksUploaded = false;

    int tca = 0, tra = 0, tsq = 0;
    String targetSection, targetSubject, targetChapter, sub_list;
    String finalTCID;

    CollectionReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_uploader);

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("chapter");
        timeLeft = getIntent().getLongExtra("timeLeft", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        _mpq = getIntent().getIntExtra("_mpq", 1);
        Toast.makeText(this, "ru: 1", Toast.LENGTH_SHORT).show();
        finalTCID = targetSection.equals("lt") ? getIntent().getStringExtra("tid") : targetChapter;

        if (targetSection.equals("lt")) {
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("tests").document(finalTCID).collection("scoreboard");
        } else {
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(finalTCID).collection("scoreboard");
        }

        total_marks = _mpq * questions.size();
        int total_time_taken;

        sub_ans = getIntent().getIntArrayExtra("sub_ans");
        selectedFeedbackOption = getIntent().getIntArrayExtra("selectedFeedbackOption");
        //Toast.makeText(this, "total ques: total ans: "+questions.size()+" : "+sub_ans.length, Toast.LENGTH_LONG).show();

        for (int i = 0; i < sub_ans.length; i++) {
            //Toast.makeText(this, ""+questions.get(i).getAnswer(), Toast.LENGTH_LONG).show();
            if (sub_ans[i] == -1) {
                tsq++;
            } else if (sub_ans[i] == Integer.parseInt(questions.get(i).getAnswer())) {
                marks_obtained += _mpq;
                tca++;
            } else {
                tra++;
            }
        }
        total_time_taken = 0;
        accuracy = ((((double) tca) / (tca + tra)) * 100.0);
        accuracy = new BigDecimal(accuracy).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (tca + tra > 0) {
            total_time_taken = Math.toIntExact(Math.round((((getIntent().getIntExtra("timer", 60) * 60 * 1000) - timeLeft) / (1000.0)) / ((double) (tca + tra))));
        }
        tpq = total_time_taken;

        UploadData();

        timerValue = getIntent().getIntExtra("timer", 60);
        Toast.makeText(this, "tca=" + tca + " tra=" + tra + " timerValue=" + timerValue + " timeLeft=" + timeLeft, Toast.LENGTH_SHORT).show();

    }

    void UploadData() {
        Toast.makeText(this, "ru: 2", Toast.LENGTH_SHORT).show();
        final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document("" + uuid);
        final DocumentReference UserTestRecordDoc = userDoc.collection("scoreboard").document("" + targetSection).collection("test").document("" + finalTCID);
        final float percentageMarks = (tca * _mpq) / total_marks;
        double userRecordScore = targetSection.equals("mt") ? percentageMarks : targetSection.equals("lt") ? percentageMarks * 2 : percentageMarks * 1.5;

        Map<String, String> userTestRecordMap = new HashMap<>();
        userTestRecordMap.put("score", "" + userRecordScore);
        userTestRecordMap.put("total_skipped", "" + tsq);
        userTestRecordMap.put("total_attempted", "" + (tca + tra));
        userTestRecordMap.put("total_correct", "" + tca);
        userTestRecordMap.put("_mpq", "" + _mpq);
        userTestRecordMap.put("accuracy", "" + accuracy);
        userTestRecordMap.put("tpq", "" + tpq);
        String answerString = "";
        for (int n : sub_ans)
            answerString += n + "_";
        sub_list = answerString.substring(0, answerString.length() - 1);
        userTestRecordMap.put("answer_key", sub_list);

        UserTestRecordDoc.set(userTestRecordMap);
        Toast.makeText(this, "ru: 3", Toast.LENGTH_SHORT).show();
        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    Toast.makeText(getApplicationContext(), "ru: 4", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "ru: 5", Toast.LENGTH_SHORT).show();
                    final String studentName = snapshot.getString("Name");
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                    Map<String, String> scoreboard = new HashMap<>();
                    scoreboard.put("Score", "" + (tca * _mpq));
                    scoreboard.put("name", "" + studentName);
                    DocumentReference reference = mDatabaseReference.document("" + uuid);
                    reference.set(scoreboard).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(TestResultActivity.this, "score uploaded in database for user: "+FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            marksUploaded = true;
                            Toast.makeText(getApplicationContext(), "ru: 6", Toast.LENGTH_SHORT).show();
                            //((TextView)findViewById(R.id.UploadingTXT)).setText("uploading feedback, please wait..");
                            final CollectionReference feedbackBasePath = FirebaseFirestore.getInstance().collection("common").document("post test").collection("feedback");
                            //final CollectionReference feedbackBasePath = FirebaseFirestore.getInstance().collection("users").document("" + uuid).collection("feedback");
                            feedbackBasePath.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Toast.makeText(getApplicationContext(), "ru: 7", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "ru: 8", Toast.LENGTH_SHORT).show();
                                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                        for (int i = 0; i < selectedFeedbackOption.length; i++) {
                                            if (selectedFeedbackOption[i] > -1) {
                                                Map<String, String> _fb = new HashMap<>();
                                                _fb.put("question", questions.get(i).getQuestion());
                                                _fb.put("issue", feedbackOptions[selectedFeedbackOption[i]]);
                                                _fb.put("section", targetSection);
                                                _fb.put("subject", targetSubject);
                                                _fb.put("chapter", targetChapter);
                                                _fb.put("student", studentName);
                                                _fb.put("date", ""+Calendar.getInstance().getTimeInMillis());
                                                if (targetSection.equals("lt")) {
                                                    _fb.put("testID", getIntent().getStringExtra("tid"));
                                                }
                                                //DocumentReference nycRef = feedbackBasePath.document("Feedback_" + (++count));
                                                DocumentReference nycRef = feedbackBasePath.document("Feedback_" + Calendar.getInstance().getTimeInMillis());
                                                batch.set(nycRef, _fb);
                                            }
                                        }
                                        Toast.makeText(getApplicationContext(), "ru: 9", Toast.LENGTH_SHORT).show();
                                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //add forwarding code here
                                                if (targetSection.contains("lt")) {
                                                    Toast.makeText(getApplicationContext(), "ru: 10", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    //forward to resultba
                                                    Toast.makeText(getApplicationContext(), "ru: 11", Toast.LENGTH_SHORT).show();
                                                    ForwardToResult();
                                                }
                                            }
                                        });
                                    } else {
                                        //and here
                                        Toast.makeText(getApplicationContext(), "ru: 12", Toast.LENGTH_SHORT).show();
                                        if (targetSection.contains("lt")) {
                                            Toast.makeText(getApplicationContext(), "ru: 13", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "ru: 14", Toast.LENGTH_SHORT).show();
                                            //forward to resultba
                                            ForwardToResult();
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

    private void ForwardToResult() {
        Toast.makeText(getApplicationContext(), "ru: 15", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), TestResultActivity.class);
        intent.putExtra("section", targetSection);
        intent.putExtra("subject", targetSubject);
        intent.putExtra("chapter", targetChapter);
        intent.putExtra("tid", finalTCID);
        intent.putExtra("timeLeft", timeLeft);
        intent.putExtra("questions", (Serializable) questions);
        intent.putExtra("_mpq", _mpq);

        intent.putExtra("total_skipped", ""+tsq);
        intent.putExtra("total_correct", ""+tca);
        intent.putExtra("total_attempted", ""+(tra+tca));
        intent.putExtra("answer_key", sub_list);
        intent.putExtra("accuracy", ""+accuracy);
        intent.putExtra("tpq", ""+tpq);
        intent.putExtra("timer", timerValue);
        startActivity(intent);
    }
}