package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import java.io.Serializable;
import java.util.List;

public class TestInstructionsActivity extends AppCompatActivity {

    TextView tQues, tMarks, tMin;
    CheckBox insCB;

    List<Question> questions;
    int _mpq;   //marks per question
    int timer;  //time in seconds
    String title;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_instructions);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");  //= question list from prev activity
        _mpq = getIntent().getIntExtra("_mpq", 1);
        title = getIntent().getStringExtra("title");
        if (getIntent().getStringExtra("section").equals("lt")) {
            timer = getIntent().getIntExtra("duration", 60);   //value comes in milliseconds for lt
        } else {
            timer = getIntent().getIntExtra("timer", 60);   //default a min.
        }

        tQues = findViewById(R.id.TQues);
        tMarks = findViewById(R.id.TMarks);
        tMin = findViewById(R.id.TMin);

        tQues.setText("" + questions.size());
        tMarks.setText("" + _mpq * questions.size());
        tMin.setText("" + timer);
        ((TextView) findViewById(R.id.TestTitleIP)).setText(title != null ? title : "-");
        insCB = findViewById(R.id.InsCB);/*
        FirebaseFirestore.getInstance().collection("common").document("pre test").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                instructionTV.setText(""+documentSnapshot.getString("instruction"));
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                //startActivity(new Intent(TestInstructionsActivity.this, MainDashboard.class));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void BeginTest(View view) {
        if (insCB.isChecked()) {
            Intent myIntent = new Intent(TestInstructionsActivity.this, TestQuestionsActivity.class);
            myIntent.putExtra("questions", (Serializable) questions);
            myIntent.putExtra("_mpq", _mpq);
            myIntent.putExtra("title", title);
            myIntent.putExtra("section", getIntent().getStringExtra("section"));
            myIntent.putExtra("subject", getIntent().getStringExtra("subject"));
            myIntent.putExtra("chapter", getIntent().getStringExtra("chapter"));
            if (getIntent().getStringExtra("section").equals("lt")) {
                myIntent.putExtra("tid", getIntent().getStringExtra("tid"));
                myIntent.putExtra("timer", getIntent().getLongExtra("timer", 0)/*Calendar.getInstance().getTimeInMillis()*/);
                myIntent.putExtra("duration", getIntent().getIntExtra("duration", 60)/*55*/);
            } else {
                myIntent.putExtra("timer", timer);
            }
            //Toast.makeText(getApplicationContext(), "section, subject, chap, test: lt,"+getIntent().getStringExtra("subject")+","+getIntent().getStringExtra("chapter")+","+utID, Toast.LENGTH_LONG).show();

            startActivity(myIntent);
        } else {
            Toast.makeText(this, "Please agree to the terms and conditions in order to proceed with test", Toast.LENGTH_SHORT).show();
        }
    }
}