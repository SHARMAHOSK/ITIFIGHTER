package com.example.itifighterAdmin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class admin_feedbackDetails extends AppCompatActivity {

    String feedbackID = null;
    Long fDate;
    TextView feedbackDate, feedbackStudent, feedbackIssue, feedbackQues, feedbackSubject, feedbackSection, feedbackTestChap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feedback_details);

        feedbackID = getIntent().getStringExtra("feedbackID");
        if(feedbackID != null){
            feedbackDate = findViewById(R.id.feedbackDate);
            feedbackStudent = findViewById(R.id.feedbackStudent);
            feedbackIssue = findViewById(R.id.feedbackIssue);
            feedbackQues = findViewById(R.id.feedbackQues);
            feedbackSection = findViewById(R.id.feedbackSection);
            feedbackSubject = findViewById(R.id.feedbackSubject);
            feedbackTestChap = findViewById(R.id.feedbackTestChap);
            FirebaseFirestore.getInstance().collection("common").document("post test").collection("feedback").document(feedbackID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    fDate = Long.parseLong(Objects.requireNonNull(documentSnapshot.getString("date")));
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(fDate);
                    String dateTime = ""+cal.get(Calendar.DAY_OF_MONTH)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR)
                            +", "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE);
                    feedbackDate.setText(dateTime);
                    feedbackStudent.setText(documentSnapshot.getString("student"));
                    feedbackIssue.setText(documentSnapshot.getString("issue"));
                    feedbackQues.setText(documentSnapshot.getString("question"));
                    feedbackSubject.setText(documentSnapshot.getString("subject"));
                    String section = documentSnapshot.getString("section");
                    feedbackSection.setText(section);

                    if(section == "lt"){
                        ((TextView)findViewById(R.id.feedbackTestChapHeader)).setText("Test Id:");
                        feedbackTestChap.setText(documentSnapshot.getString("testID"));
                    }else{
                        ((TextView)findViewById(R.id.feedbackTestChapHeader)).setText("Chapter Id:");
                        feedbackTestChap.setText(documentSnapshot.getString("chapter"));
                    }
                }
            });
        }

    }
}