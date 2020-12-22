package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

public class TestAnswerSheetActivity extends AppCompatActivity {

    LinearLayout AnswerList;
    List<Question> questions;
    int[] sub_ans;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_answer_sheet);
        AnswerList = findViewById(R.id.AnswerSheetList);

        final TextView cbt = findViewById(R.id.ContinueBTNAST);
        cbt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= cbt.getRight() - cbt.getTotalPaddingRight()) {
                        // your action for drawable click event
                        finish();
                        return true;
                    }
                }
                return true;
            }
        });

        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        sub_ans = getIntent().getIntArrayExtra("answer_key");
        int i=1;
        for(Question ques : questions){
            fillAnswerSheet(i, ques, sub_ans[i-1]);
            i++;
        }
    }

    private void fillAnswerSheet(int num, Question ques, int ans) {
        if(questions == null)
            return;
        View mAnswerRow = null;
        mAnswerRow = View.inflate(this, R.layout.activity_view_solution_xyz, null);
        ((TextView)mAnswerRow.findViewById(R.id.SheetIndex)).setText(""+num);
        ((TextView)mAnswerRow.findViewById(R.id.SheetQues)).setText(""+ques.getQuestion());
        ((TextView)mAnswerRow.findViewById(R.id.SheetOption1)).setText(""+ques.getOption1());
        ((TextView)mAnswerRow.findViewById(R.id.SheetOption2)).setText(""+ques.getOption2());
        ((TextView)mAnswerRow.findViewById(R.id.SheetOption3)).setText(""+ques.getOption3());
        ((TextView)mAnswerRow.findViewById(R.id.SheetOption4)).setText(""+ques.getOption4());

        TextView _response = mAnswerRow.findViewById(R.id.SheetResponseTag);

        int correct_ans = Integer.parseInt(ques.getAnswer().trim());
        if(ans <= 0){
            //display skipped tag
            _response.setText("Skipped");
            _response.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.holo_purple)));
            loadCheck(correct_ans, mAnswerRow, true);
        }else{
            if(correct_ans == ans){
                _response.setText("Correct Answer");
                _response.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.holo_green_dark)));
                loadCheck(ans, mAnswerRow, true);
            }else{
                _response.setText("Wrong Answer");
                _response.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.holo_red_dark)));
                loadCheck(ans, mAnswerRow, false);
                loadCheck(correct_ans, mAnswerRow, true);
            }
        }
        AnswerList.addView(mAnswerRow);
    }

    void loadCheck(int index, View mAnsRow, boolean affirmative){
        if(index<1 || index>4)
            return;
        ImageView _img = null;
        LinearLayout _strip = null;
        TextView _char = null;
        switch (index){
            case 1:
                _img = mAnsRow.findViewById(R.id.SheetCheck1);
                _strip = mAnsRow.findViewById(R.id.SheetStrip1);
                _char = mAnsRow.findViewById(R.id.SheetCHar1);
                break;
            case 2:
                _img = mAnsRow.findViewById(R.id.SheetCheck2);
                _strip = mAnsRow.findViewById(R.id.SheetStrip2);
                _char = mAnsRow.findViewById(R.id.SheetCHar2);
                break;
            case 3:
                _img = mAnsRow.findViewById(R.id.SheetCheck3);
                _strip = mAnsRow.findViewById(R.id.SheetStrip3);
                _char = mAnsRow.findViewById(R.id.SheetCHar3);
                break;
            case 4:
                _img = mAnsRow.findViewById(R.id.SheetCheck4);
                _strip = mAnsRow.findViewById(R.id.SheetStrip4);
                _char = mAnsRow.findViewById(R.id.SheetCHar4);
                break;
        }
        _img.setImageResource(affirmative ? R.drawable.checked : R.drawable.cancel);
        _strip.setBackgroundColor(Color.parseColor(affirmative ? "#DEFDC8" : "#FDC8C8"));
        _char.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(affirmative ? R.color.green1 : R.color.grey)));
    }

    public void CheckLeaderBoard(View view) {
        Intent intent = new Intent(TestAnswerSheetActivity.this, TestLeaderBoardActivity.class);
        intent.putExtra("tid", /*getIntent().getStringExtra("tid")*/"002");
        startActivity(intent);
    }
}