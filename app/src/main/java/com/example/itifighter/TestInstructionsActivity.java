package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;

public class TestInstructionsActivity extends AppCompatActivity {

    private CheckBox insCB;
    private List<Question> questions;
    private int _mpq,timer;   //marks per question , //time in seconds
    private String title;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
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

        TextView tQues = findViewById(R.id.TQues);
        TextView tMarks = findViewById(R.id.TMarks);
        TextView tMin = findViewById(R.id.TMin);
        final TextView cbt = findViewById(R.id.TestTitleIP);
        insCB = findViewById(R.id.InsCB);



        //cbt.setVisibility(View.INVISIBLE);
        cbt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= cbt.getRight() - cbt.getTotalPaddingRight()) {
                        // your action for drawable click event
                        onBackPressed();
                        return true;
                    }
                }
                return true;
            }
        });


        tQues.setText("" + questions.size());
        tMarks.setText("" + _mpq * questions.size());
        tMin.setText("" + timer);
        cbt.setText(title != null ? title : "-");

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
            startActivity(myIntent);
        } else {
            Toast.makeText(this, "Please agree to the terms and conditions in order to proceed with test", Toast.LENGTH_SHORT).show();
        }
    }
}