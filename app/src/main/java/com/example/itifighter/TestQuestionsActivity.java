package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.Question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class TestQuestionsActivity extends AppCompatActivity {
    RadioGroup radioGroup, feedbackGroup;
    long millisecondsLeft = 0;
    int skippedCount, attemptedCount;
    int[] pendingQuestions;
    TextView SkippedCount, AttemptedCount, PendingCount;
    int currentQues = 0; //may also be used to go back to a particular question.
    List<Question> questions;
    int[] sub_ans;
    boolean testBegan = false;
    int _mpq;   //marks per question
    int timer;  //time in seconds
    int currentApiVersion;
    TextView questionText, timerText, quesNumText;
    CountDownTimer testTimer = null;
    Button submitBtn, nextBtn, skipBtn;
    int picked_ans = -1;
    String title;
    LinearLayout quesNavPanel;
    ArrayList<Button> quesBtns;

    String[] feedbackOptions = { "Wrong Question", "Wrong Options", "Incomplete Question", "Incorrect Grammar", "Question out of syllabus",
            "Question on old pattern", "Repeated Question"};
    int[] selectedFeedbackOption;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_questions);
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
            {
                @Override
                public void onSystemUiVisibilityChange(int visibility)
                {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) decorView.setSystemUiVisibility(flags);
                }
            });
        }

        /* let's see if putting my code here works... */
        radioGroup = findViewById(R.id.radioGroup1);
        feedbackGroup = findViewById(R.id.feedBackGroup);
        radioGroup.setOnCheckedChangeListener(radioListener);
        feedbackGroup.setOnCheckedChangeListener(feedBackListener);
        questionText = findViewById(R.id.questionText);
        quesNumText = findViewById(R.id.QuesNum);
        timerText = findViewById(R.id.TestTimer);
        quesNavPanel = findViewById(R.id.QuesNavPanel);
        quesBtns = new ArrayList<>();
        ViewGroup.LayoutParams layoutParams = quesNavPanel.getLayoutParams();
        layoutParams.width = 0;
        quesNavPanel.setLayoutParams(layoutParams);
        title = getIntent().getStringExtra("title");
        /*Spinner spin = findViewById(R.id.TestQuestionFeedbackSpinner);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFeedbackOption[currentQues] = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter aa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,feedbackOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);*/
        submitBtn = findViewById(R.id.submitBtn);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);

        submitBtn.setVisibility(View.INVISIBLE);

        questions = (List<Question>) getIntent().getSerializableExtra("questions");  //= question list from prev activity
        _mpq = getIntent().getIntExtra("_mpq", 1);

        if(getIntent().getStringExtra("section").equals("lt")){
        }else{
            timer = getIntent().getIntExtra("timer", 60)/**60*/;   //default an hour.
        }

        sub_ans = new int[questions.size()];
        Toast.makeText(this, "total ans blocks: "+sub_ans.length, Toast.LENGTH_SHORT).show();
        selectedFeedbackOption = new int[questions.size()];
        Toast.makeText(this, "total ques: total ans: "+questions.size()+" : "+sub_ans.length, Toast.LENGTH_LONG).show();
        Arrays.fill(sub_ans, -1);
        Arrays.fill(selectedFeedbackOption, -1);

        pendingQuestions = new int[questions.size()];
        Arrays.fill(pendingQuestions, 0);

        ((TextView)findViewById(R.id.availableX)).setText(""+questions.size());
        PendingCount = findViewById(R.id.pendingX);
        AttemptedCount = findViewById(R.id.attemptedX);
        SkippedCount = findViewById(R.id.skippedX);

        skippedCount = 0;
        attemptedCount = 0;

        PendingCount.setText(""+questions.size());
        AttemptedCount.setText("0");
        SkippedCount.setText("0");

        int ii=1;
        while(ii*5<=questions.size()){
            fillQuesNumTable(quesNavPanel, ii*5);
            ii++;
        }
        ii--;
        ii *= 5;
        ii++;
        final int ii1 = ii;
        if(ii <= questions.size()){
            LinearLayout ll = quesNavPanel.findViewById(R.id.tableLayoutList);
            View mTableRow = null;
            mTableRow = View.inflate(this, R.layout.activity_test_quesnumlist_row, null);
            Button b1 = mTableRow.findViewById(R.id.B1);
            b1.setVisibility(View.VISIBLE);
            b1.setText(""+ii);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JumpTopQuestion(ii1-1);
                }
            });
            quesBtns.add(b1);
            ii++;
            if(ii <= questions.size()){
                final int ii2 = ii;
                Button b2 = mTableRow.findViewById(R.id.B2);
                b2.setVisibility(View.VISIBLE);
                b2.setText(""+ii);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JumpTopQuestion(ii2-1);
                    }
                });
                quesBtns.add(b2);
                ii++;
                if(ii <= questions.size()){
                    final int ii3 = ii;
                    Button b3 = mTableRow.findViewById(R.id.B3);
                    b3.setVisibility(View.VISIBLE);
                    b3.setText(""+ii);
                    b3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            JumpTopQuestion(ii3-1);
                        }
                    });
                    quesBtns.add(b3);
                    ii++;
                    if(ii <= questions.size()){
                        final int ii4 = ii;
                        Button b4 = mTableRow.findViewById(R.id.B4);
                        b4.setVisibility(View.VISIBLE);
                        b4.setText(""+ii);
                        b4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                JumpTopQuestion(ii4-1);
                            }
                        });
                        quesBtns.add(b4);
                        ii++;
                        if(ii <= questions.size()){
                            final int ii5 = ii;
                            Button b5 = mTableRow.findViewById(R.id.B5);
                            b5.setVisibility(View.VISIBLE);
                            b5.setText(""+ii);
                            b5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    JumpTopQuestion(ii5-1);
                                }
                            });
                            quesBtns.add(b5);
                        }
                    }
                }
            }
            ll.addView(mTableRow);
        }
        ((TextView)findViewById(R.id.TestTitle)).setText(title != null ? title : "IBPS PO");
        FirstLoad();
        /*my territory ends here.... idk what the hell is beyond here.*/
    }

    /*to hide bottom nav buttons permanently*/
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    /**/

    /*my func copied and pasted from overflow*/

    private void fillQuesNumTable(View v, int i) {
        final int i1 = i-4;
        final int i2 = i-3;
        final int i3 = i-2;
        final int i4 = i-1;
        final int i5 = i;
        LinearLayout ll = v.findViewById(R.id.tableLayoutList);

        View mTableRow = null;
        mTableRow = View.inflate(this, R.layout.activity_test_quesnumlist_row, null);

        Button b1 = mTableRow.findViewById(R.id.B1);
        Button b2 = mTableRow.findViewById(R.id.B2);
        Button b3 = mTableRow.findViewById(R.id.B3);
        Button b4 = mTableRow.findViewById(R.id.B4);
        Button b5 = mTableRow.findViewById(R.id.B5);
        b1.setVisibility(View.VISIBLE);
        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);
        b4.setVisibility(View.VISIBLE);
        b5.setVisibility(View.VISIBLE);


        b1.setText(""+(i-4));
        b2.setText(""+(i-3));
        b3.setText(""+(i-2));
        b4.setText(""+(i-1));
        b5.setText(""+(i));

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTopQuestion(i1-1);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTopQuestion(i1);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTopQuestion(i2);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTopQuestion(i3);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTopQuestion(i4);
            }
        });

        ll.addView(mTableRow);
        quesBtns.add(b1);
        quesBtns.add(b2);
        quesBtns.add(b3);
        quesBtns.add(b4);
        quesBtns.add(b5);
    }

    @Override
    public void onBackPressed() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                //startActivity(new Intent(TestQuestionsActivity.this, MainDashboard.class));
                if(testTimer != null)
                    testTimer.cancel();
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
        alert.show();*/
        findViewById(R.id.TQAlertQuitExamLayout).setVisibility(View.VISIBLE);
    }

    void FirstLoad(){
        if(testBegan)
            return;
        testBegan = true;
        /*super.onResume();*/
        //build our first question
        buildQuestion(0);
        testTimer = new CountDownTimer(Objects.equals(getIntent().getStringExtra("section"), "lt")
                ? (getIntent().getLongExtra("timer", 0)+ (getIntent().getIntExtra("duration", 60)*60*1000))-Calendar.getInstance().getTimeInMillis() /*(55*60*1000)*/
                : (timer * 60 * 1000), 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                millisecondsLeft = millisUntilFinished;
                long secs = millisUntilFinished / 1000;
                long min = secs / 60;
                secs %= 60;
                timerText.setText("TIME LEFT: " + (min > 9 ? min : "0"+min) + ":" + (secs > 9 ? secs : "0"+secs));
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                timerText.setText("done!");
                Intent intent = new Intent(TestQuestionsActivity.this, TestResultActivity.class);
                intent.putExtra("sub_ans", sub_ans);
                intent.putExtra("selectedFeedbackOption", selectedFeedbackOption);
                intent.putExtra("section", getIntent().getStringExtra("section"));
                intent.putExtra("subject", getIntent().getStringExtra("subject"));
                int tttt = getIntent().getStringExtra("section").contains("lt") ? getIntent().getIntExtra("duration", 60) : timer;
                intent.putExtra("timer", tttt);
                intent.putExtra("timeLeft", millisecondsLeft);
                //ab to lt me bhi chapter hai...
                intent.putExtra("chapter", getIntent().getStringExtra("chapter"));
                if(Objects.equals(getIntent().getStringExtra("section"), "lt")){
                    intent.putExtra("tid", getIntent().getStringExtra("tid"));
                }
                intent.putExtra("questions", (Serializable) questions);
                startActivity(intent);
                finish();
            }
        };
        testTimer.start();
    }

    /*@Override
    protected void onResume() {
        if(testBegan)
            return;
        testBegan = true;
        super.onResume();
        //build our first question
        buildQuestion(0);
        new CountDownTimer(getIntent().getStringExtra("section").equals("lt")
                ? (getIntent().getLongExtra("timer", 0)+ (getIntent().getIntExtra("duration", 60)*60*1000))-Calendar.getInstance().getTimeInMillis() *//*(55*60*1000)*//*
                : (timer * 60 * 1000), 1000) {
            public void onTick(long millisUntilFinished) {
                long secs = millisUntilFinished / 1000;
                long min = secs / 60;
                secs %= 60;
                timerText.setText("TIME LEFT: " + (min > 9 ? min : "0"+min) + ":" + (secs > 9 ? secs : "0"+secs));
            }
            public void onFinish() {
                timerText.setText("done!");
                Intent intent = new Intent(TestQuestionsActivity.this, TestResultActivity.class);
                intent.putExtra("sub_ans", sub_ans);
                intent.putExtra("selectedFeedbackOption", selectedFeedbackOption);
                intent.putExtra("section", getIntent().getStringExtra("section"));
                intent.putExtra("subject", getIntent().getStringExtra("subject"));
                if(getIntent().getStringExtra("section").equals("lt")){
                    intent.putExtra("tid", getIntent().getStringExtra("tid"));
                }else{
                    intent.putExtra("chapter", getIntent().getStringExtra("chapter"));
                }
                intent.putExtra("questions", (Serializable) questions);
                startActivity(intent);
            }
        }.start();
    }*/

    /**
     * This is how we will capture our User's response.  Once they click on a radio button,
     * the response can immediately be checked if it is correct.
     * <p/>
     * we can modify the accessor method submitAnswer(int) to be something like collectAnswer(int) instead.
     * Then with the use of a button on the screen which the user can use to submit their answer.
     */

    RadioGroup.OnCheckedChangeListener feedBackListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:
                    selectedFeedbackOption[currentQues] = -1;
                    break;
                case R.id.feedbackOption1:
                    selectedFeedbackOption[currentQues] = 0;
                    break;
                case R.id.feedbackOption2:
                    selectedFeedbackOption[currentQues] = 1;
                    break;
                case R.id.feedbackOption3:
                    selectedFeedbackOption[currentQues] = 2;
                    break;
                case R.id.feedbackOption4:
                    selectedFeedbackOption[currentQues] = 3;
                    break;
                case R.id.feedbackOption5:
                    selectedFeedbackOption[currentQues] = 4;
                    break;
                case R.id.feedbackOption6:
                    selectedFeedbackOption[currentQues] = 5;
                    break;
                case R.id.feedbackOption7:
                    selectedFeedbackOption[currentQues] = 6;
                    break;
            }
        }
    };

    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:
                    //Toast.makeText(TestQuestionsActivity.this, "No Support for questions with more than 4 possible answers. checkId: " + checkedId, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radioButton4:
                    //Toast.makeText(TestQuestionsActivity.this, "option 4", Toast.LENGTH_SHORT).show();
                    //submitAnswer(4);
                    picked_ans = 4;
                    break;
                case R.id.radioButton3:
                    //Toast.makeText(TestQuestionsActivity.this, "option 3", Toast.LENGTH_SHORT).show();
                    picked_ans = 3;
                    //submitAnswer(3);
                    break;
                case R.id.radioButton2:
                    //Toast.makeText(TestQuestionsActivity.this, "option 2", Toast.LENGTH_SHORT).show();
                    picked_ans = 2;
                    //submitAnswer(2);
                    break;
                case R.id.radioButton:
                    //Toast.makeText(TestQuestionsActivity.this, "option 1", Toast.LENGTH_SHORT).show();
                    picked_ans = 1;
                    //submitAnswer(1);
                    break;
            }
        }
    };


    public void SubmitAns(View v){
        if(picked_ans == -1){
            Toast.makeText(this, "please select an answer", Toast.LENGTH_SHORT).show();
        }else
            submitAnswer(picked_ans);
    }

    /**
     * Build and Display question for user.
     *
     * @param question the position which question whould be shown to the user.
     */
    @SuppressLint("SetTextI18n")
    private void buildQuestion(int question) {
        if(sub_ans[question] < 1){
            radioGroup.clearCheck();
        }else{
            radioGroup.check(radioGroup.getChildAt(sub_ans[question]-1).getId());
        }
        quesNumText.setText((question+1)+"/"+ questions.size());
        //this method would set and display your question
        displayQuestionText(question);

        //this would gather your answers to display to your user.
        String[] orderedAnswers = displayPossibleAnswers(question);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton) {
                if(i < orderedAnswers.length) {
                    ((RadioButton) o).setText(orderedAnswers[i]);
                    o.setVisibility(View.VISIBLE);
                } else {
                    ((RadioButton) o).setText("");
                    o.setVisibility(View.GONE);
                }
            }
        }
    }

    private void JumpTopQuestion(int question){
        buildQuestion(question);
        currentQues = question;
        if (currentQues == questions.size() - 1){
            submitBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.INVISIBLE);
        }else{
            submitBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
        ViewGroup.LayoutParams layoutParams = quesNavPanel.getLayoutParams();
        layoutParams.width = 0;
        quesNavPanel.setLayoutParams(layoutParams);
    }

    private String[] displayPossibleAnswers(int question) {
        String[] options = new String[4];
        options[0] = questions.get(question).getOption1();
        options[1] = questions.get(question).getOption2();
        options[2] = questions.get(question).getOption3();
        options[3] = questions.get(question).getOption4();

        return options;
    }

    private void displayQuestionText(int question) {
        //previous activity: fetch questions from fire-store- create class list of questions.
        questionText.setText(questions.get(question).getQuestion());
    }

    public void SkipQuestion(View v){
        submitAnswer(-1);
    }

    /**
     * Submit user's answer.  This also handles the return of checking answer to display to the user
     * whether they got the question correct or incorrect.
     *
     * @param i position of user's answer
     */
    @SuppressLint("SetTextI18n")
    private void submitAnswer(int i) {
        if(i != -1 && sub_ans[currentQues] == -1){
            attemptedCount++;
        }else if(i == -1 && pendingQuestions[currentQues] != 1){
            skippedCount++;
        }
        pendingQuestions[currentQues] = 1;
        PendingCount.setText(""+(questions.size() - (skippedCount+attemptedCount)));
        SkippedCount.setText(""+skippedCount);
        AttemptedCount.setText(""+attemptedCount);
        //call on next/submit click
        //currently ato-submit by on radio button lick listener above.
        //collect student answers in an array and proceed to next ques if not last.
        Toast.makeText(this, "saving ans index: "+currentQues, Toast.LENGTH_LONG).show();
        sub_ans[currentQues] = i;
        if(i == -1)
            quesBtns.get(currentQues).setBackgroundColor(getResources().getColor(R.color.design_default_color_error));
        else
            quesBtns.get(currentQues).setBackgroundColor(getResources().getColor(R.color.green1));
        if(++currentQues >= questions.size()){
            currentQues = questions.size()-1;
            if(i != -1){
                //confirmation box before submission.
                //submit test.
                /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Do you want to Submit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TestQuestionsActivity.this, TestResultActivity.class);
                        intent.putExtra("sub_ans", sub_ans);
                        intent.putExtra("selectedFeedbackOption", selectedFeedbackOption);
                        intent.putExtra("section", getIntent().getStringExtra("section"));
                        intent.putExtra("subject", getIntent().getStringExtra("subject"));
                        intent.putExtra("timeLeft", millisecondsLeft);
                        //now even lt has chapter list, so...
                        intent.putExtra("chapter", getIntent().getStringExtra("chapter"));
                        if(getIntent().getStringExtra("section").equals("lt")){
                            intent.putExtra("tid", getIntent().getStringExtra("tid"));
                        }
                        intent.putExtra("questions", (Serializable) questions);
                        if(testTimer != null)
                            testTimer.cancel();
                        startActivity(intent);
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
                alert.show();*/
                findViewById(R.id.TQAlertSubmitExamLayout).setVisibility(View.VISIBLE);
            }
        }else{
            if (currentQues == questions.size() - 1){
                submitBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.INVISIBLE);
            }
            radioGroup.clearCheck();
            picked_ans = -1;
            buildQuestion(currentQues);
        }
    }


    public void FinalSubmission(View view){
        Intent intent = new Intent(TestQuestionsActivity.this, ResultUploader.class);
        intent.putExtra("sub_ans", sub_ans);
        intent.putExtra("selectedFeedbackOption", selectedFeedbackOption);
        intent.putExtra("section", getIntent().getStringExtra("section"));
        intent.putExtra("subject", getIntent().getStringExtra("subject"));
        intent.putExtra("timeLeft", millisecondsLeft);
        //now even lt has chapter list, so...
        intent.putExtra("chapter", getIntent().getStringExtra("chapter"));
        if(getIntent().getStringExtra("section").equals("lt")){
            intent.putExtra("tid", getIntent().getStringExtra("tid"));
        }
        intent.putExtra("questions", (Serializable) questions);
        if(testTimer != null)
            testTimer.cancel();
        Toast.makeText(this, "section, subject, chap, test: "+getIntent().getStringExtra("section")+","+getIntent().getStringExtra("subject")+","+getIntent().getStringExtra("chapter")+","+getIntent().getStringExtra("tid"), Toast.LENGTH_LONG).show();
        startActivity(intent);
        finish();
    }

    public void CancelFinalSubmission(View view){
        findViewById(R.id.TQAlertSubmitExamLayout).setVisibility(View.GONE);
    }

    public void FinalizeExamQuit(View view){
        if(testTimer != null)
            testTimer.cancel();
        finish();
    }

    public void CancelQuitFinalization(View view){
        findViewById(R.id.TQAlertQuitExamLayout).setVisibility(View.GONE);
    }

    public void OpenQuesNavPanel(View view) {
        ViewGroup.LayoutParams layoutParams = quesNavPanel.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        quesNavPanel.setLayoutParams(layoutParams);
    }

    public void CloseQuesNavPanel(View view) {
        ViewGroup.LayoutParams layoutParams = quesNavPanel.getLayoutParams();
        layoutParams.width = 0;
        quesNavPanel.setLayoutParams(layoutParams);
    }

     public void SubmitQuestionFeedback(View view) {
        findViewById(R.id.TQFeedbackLayout).setVisibility(View.VISIBLE);
    }
    public void ConfirmSubmitFeedback(View view) {
        //...
        if(selectedFeedbackOption[currentQues] < 0)
            Toast.makeText(this, "select a reason...", Toast.LENGTH_SHORT).show();
        else{
            Toast.makeText(this, "your feedback has been recorded..", Toast.LENGTH_SHORT).show();
            findViewById(R.id.TQFeedbackLayout).setVisibility(View.GONE);
        }
    }
    public void CancelSubmitFeedback(View view) {
        selectedFeedbackOption[currentQues] = -1;
        findViewById(R.id.TQFeedbackLayout).setVisibility(View.GONE);
    }

    public void GoToPreviousQuestionWithoutSkip(View view) {
        if(currentQues<1) return;
        radioGroup.clearCheck();
        picked_ans = -1;
        buildQuestion(--currentQues);
    }
    /*my territory ends here.... idk what the hell is beyond here.*/
}