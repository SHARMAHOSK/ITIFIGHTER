package com.example.itifighter;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itifighterAdmin.Question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TestQuestionsActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    RadioGroup radioGroup;
    int currentQues = 0; //may also be used to go back to a particular question.
    List<Question> questions;
    int[] sub_ans;
    TextView questionText, timerText;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_questions);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        /* let's see if putting my code here works... */
        radioGroup = findViewById(R.id.radioGroup1);
        radioGroup.setOnCheckedChangeListener(radioListener);
        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.TestTimer);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");  //= question list from prev activity
        sub_ans = new int[questions.size()];
        Toast.makeText(this, "total ques: total ans: "+questions.size()+" : "+sub_ans.length, Toast.LENGTH_LONG).show();
        Arrays.fill(sub_ans, -1);
        /*my territory ends here.... idk what the hell is beyond here.*/
    }

    /*my func copied and pasted from overflow*/
    @Override
    protected void onResume() {
        super.onResume();
        //build our first question
        buildQuestion(0);
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerText.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerText.setText("done!");
                Intent intent = new Intent(TestQuestionsActivity.this, TestResultActivity.class);
                intent.putExtra("sub_ans", sub_ans);
                intent.putExtra("questions", (Serializable) questions);
                startActivity(intent);
            }
        }.start();
    }

    /**
     * This is how we will capture our User's response.  Once they click on a radio button,
     * the response can immediately be checked if it is correct.
     * <p/>
     * we can modify the accessor method submitAnswer(int) to be something like collectAnswer(int) instead.
     * Then with the use of a button on the screen which the user can use to submit their answer.
     */
    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:
                    Toast.makeText(TestQuestionsActivity.this, "No Support for questions with more than 4 possible answers. checkId: " + checkedId, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radioButton4:
                    Toast.makeText(TestQuestionsActivity.this, "option 4", Toast.LENGTH_SHORT).show();
                    submitAnswer(4);
                    break;
                case R.id.radioButton3:
                    Toast.makeText(TestQuestionsActivity.this, "option 3", Toast.LENGTH_SHORT).show();
                    submitAnswer(3);
                    break;
                case R.id.radioButton2:
                    Toast.makeText(TestQuestionsActivity.this, "option 2", Toast.LENGTH_SHORT).show();
                    submitAnswer(2);
                    break;
                case R.id.radioButton:
                    Toast.makeText(TestQuestionsActivity.this, "option 1", Toast.LENGTH_SHORT).show();
                    submitAnswer(1);
                    break;
            }
        }
    };

    /**
     * Build and Display question for user.
     *
     * @param question the position which question whould be shown to the user.
     */
    private void buildQuestion(int question) {
        //this method would set and display your question
        displayQuestionText(question);

        //this would gather your answers to display to your user.
        String[] orderedAnswers = displayPossibleAnswers(question);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton) {
                if(i < orderedAnswers.length) {
                    ((RadioButton) o).setText(orderedAnswers[i]);
                    ((RadioButton) o).setVisibility(View.VISIBLE);
                } else {
                    ((RadioButton) o).setText("");
                    ((RadioButton) o).setVisibility(View.GONE);
                }
            }
        }
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

    /**
     * Submit user's answer.  This also handles the return of checking answer to display to the user
     * whether they got the question correct or incorrect.
     *
     * @param i position of user's answer
     */
    private void submitAnswer(int i) {
        //call on next/submit click
        //currently ato-submit by on radio button lick listener above.
        //collect student answers in an array and proceed to next ques if not last.
        Toast.makeText(this, "saving ans index: "+currentQues, Toast.LENGTH_LONG).show();
        sub_ans[currentQues] = i;
        if(++currentQues > questions.size()){
            //submit test.
            Intent intent = new Intent(TestQuestionsActivity.this, TestResultActivity.class);
            intent.putExtra("sub_ans", sub_ans);
            intent.putExtra("questions", (Serializable) questions);
            startActivity(intent);
        }else{
            buildQuestion(currentQues);
        }
    }
    /*my territory ends here.... idk what the hell is beyond here.*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}