package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class admin_edit_lt_details extends AppCompatActivity {

    DatePicker t_datePicker, r_datePicker;
    TimePicker sTimePicker, rTimePicker;
    LinearLayout std, srd, sst, srt;
    boolean testInHistory = false;

    EditText title, tDuration, tMarks, tNOQs;
    int duration = 0;
    boolean ready = false;
    long sTime, rTime;

    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_lt_details);

        docRef = FirebaseFirestore.getInstance().collection("section").document("lt")
                .collection("branch")
                .document(""+getIntent().getStringExtra("subject"))
                .collection("tests")
                .document(""+ Objects.requireNonNull(getIntent().getStringExtra("test")).trim());
        title = findViewById(R.id.etLTTile);
        tDuration = findViewById(R.id.etLTDuration);
        tMarks = findViewById(R.id.etLTmpq);
        tNOQs = findViewById(R.id.etLTnOQs);
        t_datePicker = findViewById(R.id.t_date_picker);
        r_datePicker = findViewById(R.id.r_date_picker);
        sTimePicker = findViewById(R.id.sTime_picker);
        rTimePicker = findViewById(R.id.rTime_picker);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        testInHistory = Objects.equals(document.getString("TestInHistory"), "true");
                        Toast.makeText(admin_edit_lt_details.this, "tih: "+testInHistory, Toast.LENGTH_SHORT).show();
                        duration = Objects.requireNonNull(document.getLong("duration")).intValue();
                        Toast.makeText(admin_edit_lt_details.this, "duration: "+duration, Toast.LENGTH_SHORT).show();
                        sTime = document.getLong("sTime");
                        Toast.makeText(admin_edit_lt_details.this, "stime: "+sTime, Toast.LENGTH_SHORT).show();
                        rTime = document.getLong("rTime");
                        Toast.makeText(admin_edit_lt_details.this, "rtime: "+rTime, Toast.LENGTH_SHORT).show();
                        Toast.makeText(admin_edit_lt_details.this, "title1: "+document.getString("title"), Toast.LENGTH_SHORT).show();
                        title.setText(document.getString("title"));
                        Toast.makeText(admin_edit_lt_details.this, "title2: "+document.getString("title"), Toast.LENGTH_SHORT).show();
                        tDuration.setText(""+duration);
                        tMarks.setText(""+Objects.requireNonNull(document.getLong("marks")).intValue());
                        tNOQs.setText(""+Objects.requireNonNull(document.getLong("NOQs")).intValue());

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

                        if(!testInHistory && Calendar.getInstance().getTimeInMillis() < sTime){
                            Toast.makeText(admin_edit_lt_details.this, "setting minimum date threshold", Toast.LENGTH_SHORT).show();
                            t_datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());    //in case this doesn't set min threshold to today, try Calendar.getInstance().MILISECOND...
                            r_datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
                        }
                        ready = true;
                    } else {
                        Toast.makeText(admin_edit_lt_details.this, "no such document in database", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(admin_edit_lt_details.this, "got failed with: "+task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });

        /*t_datePicker.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                c.set(i, i1, i2);
                r_datePicker.setMinDate(c.getTimeInMillis());
            }
        });
        r_datePicker.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                c.set(i, i1, i2);
                t_datePicker.setMaxDate(c.getTimeInMillis());
            }
        });*/

        std = findViewById(R.id.std);
        srd = findViewById(R.id.srd);
        sst = findViewById(R.id.sst);
        srt = findViewById(R.id.srt);

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

    public void UpdateLTDetails(View view) {
        if(!ready){
            Toast.makeText(this, "previous data not loaded yet, wait...", Toast.LENGTH_SHORT).show();
        }else if(testInHistory){
            Toast.makeText(this, "this test is already over.", Toast.LENGTH_SHORT).show();
        }else if(Calendar.getInstance().getTimeInMillis() >= sTime){
            Toast.makeText(this, "test is live, update not allowed.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(admin_edit_lt_details.this, admin_live_test.class));
        }else{
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

                docRef.set(new TLDetails(Integer.parseInt(tNOQs.getText().toString().trim()), "false", Integer.parseInt(tDuration.getText().toString().trim()),
                        Integer.parseInt(tMarks.getText().toString().trim()), rTime, sTime,
                        title.getText().toString().trim())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(admin_edit_lt_details.this, "live test details updated.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(admin_edit_lt_details.this, admin_live_test.class);
                        intent.putExtra("subject", getIntent().getStringExtra(("subject")));
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(admin_edit_lt_details.this, "unexpected error, try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}

class TLDetails implements Serializable {
    public int NOQs;
    public String TestInHistory;
    public int duration;
    public int marks;
    public long rTime;
    public long sTime;
    public String title;

    public TLDetails(int NOQs, String testInHistory, int duration, int marks, long rTime, long sTime, String title) {
        this.NOQs = NOQs;
        TestInHistory = testInHistory;
        this.duration = duration;
        this.marks = marks;
        this.rTime = rTime;
        this.sTime = sTime;
        this.title = title;
    }
}