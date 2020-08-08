package com.example.itifighter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itifighterAdmin.Question;

import java.io.Serializable;
import java.util.List;

public class TestInstructionsActivity extends AppCompatActivity {

    TextView tQues, tMarks, tMin, instructionTV;
    CheckBox insCB;

    List<Question> questions;
    int _mpq;   //marks per question
    int timer;  //time in seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_instructions);

        questions = (List<Question>) getIntent().getSerializableExtra("questions");  //= question list from prev activity
        _mpq = getIntent().getIntExtra("_mpq", 1);
        timer = getIntent().getIntExtra("timer", 60);   //default a min.

        tQues = findViewById(R.id.TQues);
        tMarks = findViewById(R.id.TMarks);
        tMin = findViewById(R.id.TMin);

        tQues.setText(""+questions.size());
        tMarks.setText(""+_mpq*questions.size());
        tMin.setText(""+timer);

        instructionTV = findViewById(R.id.InstructionText);
        insCB = findViewById(R.id.InsCB);
        instructionTV.setText("blah blah \nblah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah \nblah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah " +
                "\nblah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah " +
                "\nblah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah " +
                "\nblah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah " +
                "");
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
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    public void BeginTest(View view) {
        if(insCB.isChecked()){
            Intent myIntent = new Intent(TestInstructionsActivity.this, TestQuestionsActivity.class);
            myIntent.putExtra("questions", (Serializable) questions);
            myIntent.putExtra("_mpq", 2);
            myIntent.putExtra("timer", 45);
            startActivity(myIntent);
        }else{
            Toast.makeText(this, "Please agree to the terms and conditions in order to proceed with test", Toast.LENGTH_SHORT).show();
        }
    }
}