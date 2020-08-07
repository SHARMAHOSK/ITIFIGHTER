package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class admin_add_lt extends AppCompatActivity {

    DatePicker t_datePicker, r_datePicker;
    TimePicker sTimePicker, rTimePicker;
    LinearLayout std, srd, sst, srt;
    EditText title, tDuration, tMarks, tNOQs;
    DocumentReference docRef;
    int count = -1;

    int duration = 0;
    long sTime, rTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_lt);
        count = getIntent().getIntExtra("count", -1);

        docRef = FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests").document("00"+(count+1));
        title = findViewById(R.id.atLTTile);
        tDuration = findViewById(R.id.atLTDuration);
        tMarks = findViewById(R.id.atLTmpq);
        tNOQs = findViewById(R.id.atLTnOQs);
        t_datePicker = findViewById(R.id.at_date_picker);
        r_datePicker = findViewById(R.id.ar_date_picker);
        sTimePicker = findViewById(R.id.asTime_picker);
        rTimePicker = findViewById(R.id.arTime_picker);

        duration = 0;
        sTime = Calendar.getInstance().getTimeInMillis();
        rTime = sTime;

        /*title.setText("");
        tDuration.setText(""+duration);
        tMarks.setText("1");
        tNOQs.setText("0");*/

        Calendar preSD = Calendar.getInstance();
        preSD.setTimeInMillis(sTime);
        t_datePicker.updateDate(preSD.get(Calendar.YEAR), preSD.get(Calendar.MONTH), preSD.get(Calendar.DAY_OF_MONTH));
        sTimePicker.setCurrentHour(preSD.get(Calendar.HOUR));
        sTimePicker.setCurrentMinute(preSD.get(Calendar.MINUTE));

        Calendar preRD = Calendar.getInstance();
        preRD.setTimeInMillis(rTime);
        r_datePicker.updateDate(preRD.get(Calendar.YEAR), preRD.get(Calendar.MONTH), preRD.get(Calendar.DAY_OF_MONTH));
        rTimePicker.setCurrentHour(preRD.get(Calendar.HOUR));
        rTimePicker.setCurrentMinute(preRD.get(Calendar.MINUTE));
        t_datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());    //in case this doesn't set min threshold to today, try Calendar.getInstance().MILISECOND...
        r_datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

        std = findViewById(R.id.astd);
        srd = findViewById(R.id.asrd);
        sst = findViewById(R.id.asst);
        srt = findViewById(R.id.asrt);

        sst.setVisibility(View.INVISIBLE);
        srt.setVisibility(View.INVISIBLE);
        srd.setVisibility(View.INVISIBLE);
    }

    public void SelectTDate(View view) {
        std.setVisibility(View.VISIBLE);
        srd.setVisibility(View.INVISIBLE);
        sst.setVisibility(View.INVISIBLE);
        srt.setVisibility(View.INVISIBLE);
    }

    public void SelectRDate(View view) {
        std.setVisibility(View.INVISIBLE);
        srd.setVisibility(View.VISIBLE);
        sst.setVisibility(View.INVISIBLE);
        srt.setVisibility(View.INVISIBLE);
    }

    public void SelectSTime(View view) {
        std.setVisibility(View.INVISIBLE);
        srd.setVisibility(View.INVISIBLE);
        sst.setVisibility(View.VISIBLE);
        srt.setVisibility(View.INVISIBLE);
    }

    public void SelectRTime(View view) {
        std.setVisibility(View.INVISIBLE);
        srd.setVisibility(View.INVISIBLE);
        sst.setVisibility(View.INVISIBLE);
        srt.setVisibility(View.VISIBLE);
    }

    public void AddLTDetails(View view) {
        if(count < 0)
            return;
        Calendar tDate = Calendar.getInstance();
        tDate.set(t_datePicker.getYear(), t_datePicker.getMonth(), t_datePicker.getDayOfMonth());
        Calendar rDate = Calendar.getInstance();
        rDate.set(r_datePicker.getYear(), r_datePicker.getMonth(), r_datePicker.getDayOfMonth());


        Calendar currentDT = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, sTimePicker.getCurrentHour());
        c.set(Calendar.MINUTE, sTimePicker.getCurrentMinute());
        Calendar cr = Calendar.getInstance();
        cr.set(Calendar.HOUR_OF_DAY, rTimePicker.getCurrentHour());
        cr.set(Calendar.MINUTE, rTimePicker.getCurrentMinute());
        if(rDate.getTimeInMillis() < tDate.getTimeInMillis()){
            Toast.makeText(this, "result date cannot be before test date", Toast.LENGTH_SHORT).show();
        }else if(currentDT.getTimeInMillis() > c.getTimeInMillis()){
            Toast.makeText(this, "test start time cannot be before current time", Toast.LENGTH_LONG).show();
        }else if(currentDT.getTimeInMillis() > cr.getTimeInMillis()){
            Toast.makeText(this, "test result time cannot be before current time", Toast.LENGTH_LONG).show();
        }else if(c.getTimeInMillis() > cr.getTimeInMillis()){
            Toast.makeText(this, "test result time cannot be before start time", Toast.LENGTH_LONG).show();
        }else if(title.getText().toString().trim().length() <= 0 ||
                tMarks.getText().toString().trim().length() <= 0 ||
                tNOQs.getText().toString().trim().length() <= 0 ||
                tDuration.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "some fields are empty", Toast.LENGTH_LONG).show();
        }else if( TimeUnit.MILLISECONDS.toMinutes(
                Math.abs(cr.getTimeInMillis() - c.getTimeInMillis())) < Integer.parseInt(tDuration.getText().toString().trim())){
            Toast.makeText(this, "difference between start time and result time cannot be less than test duration", Toast.LENGTH_LONG).show();
        }else{
            Calendar calendar1 = new GregorianCalendar(t_datePicker.getYear(),
                    t_datePicker.getMonth(),
                    t_datePicker.getDayOfMonth(),
                    sTimePicker.getCurrentHour(),
                    sTimePicker.getCurrentMinute());

            long sTime = calendar1.getTimeInMillis();

            Calendar calendar2 = new GregorianCalendar(r_datePicker.getYear(),
                    r_datePicker.getMonth(),
                    r_datePicker.getDayOfMonth(),
                    rTimePicker.getCurrentHour(),
                    rTimePicker.getCurrentMinute());

            long rTime = calendar2.getTimeInMillis();
            //check if date is before today...
            //in case of today's date, check if stime is before current...
            //check if rtime is earlier than stime...

            docRef.set(new TLDetails(title.getText().toString().trim(), sTime, rTime,
                    Integer.parseInt(tDuration.getText().toString().trim()),
                    Integer.parseInt(tMarks.getText().toString().trim()),
                    Integer.parseInt(tNOQs.getText().toString().trim()))).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(admin_add_lt.this, "live test details updated.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(admin_add_lt.this, admin_live_test.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(admin_add_lt.this, "unexpected error, try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}