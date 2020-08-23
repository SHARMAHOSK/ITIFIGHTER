package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.Question;

import java.util.List;

public class TestAnswerSheetActivity extends AppCompatActivity {

    LinearLayout AnswerList;
    List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_answer_sheet);
        AnswerList = findViewById(R.id.AnswerSheetList);

        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        int i=1;
        for(Question ques : questions){
            switch (ques.getAnswer()){
                case "1":
                    fillAnswerSheet(i++, ques.getQuestion(), ques.getOption1());
                    break;
                case "2":
                    fillAnswerSheet(i++, ques.getQuestion(), ques.getOption2());
                    break;
                case "3":
                    fillAnswerSheet(i++, ques.getQuestion(), ques.getOption3());
                    break;
                case "4":
                    fillAnswerSheet(i++, ques.getQuestion(), ques.getOption4());
                    break;
            }
        }
    }

    private void fillAnswerSheet(int num, String ques, String ans) {
        if(questions == null)
            return;
        View mAnswerRow = null;
        mAnswerRow = View.inflate(this, R.layout.fragment_answer_sheet_row, null);
        ((TextView)mAnswerRow.findViewById(R.id.SheetIndex)).setText("Question "+num+":");
        ((TextView)mAnswerRow.findViewById(R.id.SheetQues)).setText(""+ques);
        ((TextView)mAnswerRow.findViewById(R.id.SheetQues)).setText(""+ans);
        AnswerList.addView(mAnswerRow);
    }

    public void CheckLeaderBoard(View view) {
        Intent intent = new Intent(TestAnswerSheetActivity.this, TestLeaderBoardActivity.class);
        intent.putExtra("tid", /*getIntent().getStringExtra("tid")*/"002");
        startActivity(intent);
    }
}