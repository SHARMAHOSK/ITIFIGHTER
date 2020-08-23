package com.example.itifighter;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Counter extends AppCompatActivity{

    private TextView counterTime;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        counterTime = (TextView) findViewById(R.id.counterTime);
        countDownStart();
    }

    private void countDownStart() {

    }

    private void textViewGone() {

    }
}