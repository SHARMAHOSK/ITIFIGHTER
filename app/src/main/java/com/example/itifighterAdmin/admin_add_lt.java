/*
package com.example.itifighterAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
            docRef.set(new TLDetails(Integer.parseInt(tNOQs.getText().toString().trim()), "false", Integer.parseInt(tDuration.getText().toString().trim()),
                    Integer.parseInt(tMarks.getText().toString().trim()), rTime, sTime, title.getText().toString().trim())).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}*/


package com.example.itifighterAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class admin_add_lt extends AppCompatActivity {

    MaterialDatePicker sMaterialDatePicker, rMaterialDatePicker;
    long sDateMS, rDateMS;
    TimePicker sTimePicker, rTimePicker;
    EditText title, tDuration, tMarks, tNOQs;
    LinearLayout sst, srt;
    DocumentReference docRef;
    int count = -1;

    int duration = 0;
    long sTime, rTime;
    public static ArrayList<TLDetails> upcomingTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_lt);
        count = getIntent().getIntExtra("count", -1);
        upcomingTest = (ArrayList<TLDetails>) getIntent().getSerializableExtra("upcoming");
        Toast.makeText(this, "got uts: "+upcomingTest.size(), Toast.LENGTH_SHORT).show();
        //docRef = FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests").document("00"+(count+1));
        docRef = FirebaseFirestore.getInstance().collection("section")
                .document("lt")
                .collection("branch")
                .document(""+getIntent().getStringExtra("subject"))
                .collection("tests")
                .document("00"+(count+1));
        title = findViewById(R.id.atLTTile);
        tDuration = findViewById(R.id.atLTDuration);
        tMarks = findViewById(R.id.atLTmpq);
        tNOQs = findViewById(R.id.atLTnOQs);
        sTimePicker = findViewById(R.id.asTime_picker);
        rTimePicker = findViewById(R.id.arTime_picker);

        //MaterialDatePicker:
        CalendarConstraints.Builder sConstraintBuilder = new CalendarConstraints.Builder();
        sConstraintBuilder.setValidator(new CustomStartDateValidator(MaterialDatePicker.todayInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
        Toast.makeText(this, "created validator with uts: "+upcomingTest.size(), Toast.LENGTH_SHORT).show();
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());
        MaterialDatePicker.Builder SDBuilder = MaterialDatePicker.Builder.datePicker();
        SDBuilder.setTitleText("SELECT TEST START DATE");
        SDBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        SDBuilder.setCalendarConstraints(constraintBuilder.build());
        sMaterialDatePicker = SDBuilder.build();
        sMaterialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Toast.makeText(admin_add_lt.this, "start date: "+selection.toString(), Toast.LENGTH_SHORT).show();
                sDateMS = Long.parseLong(selection.toString());
                ((TextView)findViewById(R.id.TSDT)).setText("TEST START DATE: "+sMaterialDatePicker.getHeaderText());
            }
        });
        MaterialDatePicker.Builder RDBuilder = MaterialDatePicker.Builder.datePicker();
        RDBuilder.setTitleText("SELECT TEST RESULT DATE");
        RDBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        RDBuilder.setCalendarConstraints(constraintBuilder.build());
        rMaterialDatePicker = RDBuilder.build();
        rMaterialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Toast.makeText(admin_add_lt.this, "result date: "+selection.toString(), Toast.LENGTH_SHORT).show();
                rDateMS = Long.parseLong(selection.toString());
                ((TextView)findViewById(R.id.TRDT)).setText("TEST RESULT DATE: "+rMaterialDatePicker.getHeaderText());
            }
        });
        duration = 0;
        sTime = Calendar.getInstance().getTimeInMillis();
        rTime = sTime;

        Calendar preSD = Calendar.getInstance();
        preSD.setTimeInMillis(sTime);
        sTimePicker.setCurrentHour(preSD.get(Calendar.HOUR));
        sTimePicker.setCurrentMinute(preSD.get(Calendar.MINUTE));

        Calendar preRD = Calendar.getInstance();
        preRD.setTimeInMillis(rTime);
        rTimePicker.setCurrentHour(preRD.get(Calendar.HOUR));
        rTimePicker.setCurrentMinute(preRD.get(Calendar.MINUTE));

        sst = findViewById(R.id.asst);
        srt = findViewById(R.id.asrt);

        sst.setVisibility(View.INVISIBLE);
        srt.setVisibility(View.INVISIBLE);

    }

    private boolean isDateLocked(long date) {
        for(TLDetails ut : upcomingTest){
            Toast.makeText(this, "checking date against: "+ut.sTime, Toast.LENGTH_SHORT).show();
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(date);
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(ut.sTime);
            if(c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH)){
                return true;
            }
        }
        return false;
    }

    public void SelectTDate(View view) {
        if(sMaterialDatePicker != null){
            sMaterialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        }
    }

    public void SelectRDate(View view) {
        if(rMaterialDatePicker != null){
            rMaterialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        }
    }

    public void SelectSTime(View view) {
        sst.setVisibility(View.VISIBLE);
        srt.setVisibility(View.INVISIBLE);
    }

    public void SelectRTime(View view) {
        sst.setVisibility(View.INVISIBLE);
        srt.setVisibility(View.VISIBLE);
    }

    public void AddLTDetails(View view) {
        if(count < 0)
            return;
        Calendar currentDT = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, sTimePicker.getCurrentHour());
        c.set(Calendar.MINUTE, sTimePicker.getCurrentMinute());
        Calendar cr = Calendar.getInstance();
        cr.set(Calendar.HOUR_OF_DAY, rTimePicker.getCurrentHour());
        cr.set(Calendar.MINUTE, rTimePicker.getCurrentMinute());
        if(rDateMS < sDateMS){
            Toast.makeText(this, "result date cannot be before test date", Toast.LENGTH_SHORT).show();
        }else if(sDateMS < MaterialDatePicker.todayInUtcMilliseconds() || rDateMS < MaterialDatePicker.todayInUtcMilliseconds()){
            Toast.makeText(this, "test or result date cannot be before current date", Toast.LENGTH_SHORT).show();
        }else if(isDateLocked(sDateMS)){
            Toast.makeText(this, "a test is already created for this date.", Toast.LENGTH_SHORT).show();
        }else if(sDateMS == MaterialDatePicker.todayInUtcMilliseconds() && currentDT.getTimeInMillis() > c.getTimeInMillis()){
            Toast.makeText(this, "test start time cannot be before current time", Toast.LENGTH_LONG).show();
        }else if(rDateMS == MaterialDatePicker.todayInUtcMilliseconds() && currentDT.getTimeInMillis() > cr.getTimeInMillis()){
            Toast.makeText(this, "test result time cannot be before current time", Toast.LENGTH_LONG).show();
        }else if(sDateMS == rDateMS && c.getTimeInMillis() > cr.getTimeInMillis()){
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
            Calendar selectedStartDate = Calendar.getInstance();
            selectedStartDate.setTimeInMillis(sDateMS);
            Calendar calendar1 = new GregorianCalendar(selectedStartDate.get(Calendar.YEAR),
                    selectedStartDate.get(Calendar.MONTH),
                    selectedStartDate.get(Calendar.DAY_OF_MONTH),
                    sTimePicker.getCurrentHour(),
                    sTimePicker.getCurrentMinute());

            long sTime = calendar1.getTimeInMillis();
            Calendar selectedResultDate = Calendar.getInstance();
            selectedResultDate.setTimeInMillis(rDateMS);
            Calendar calendar2 = new GregorianCalendar(selectedResultDate.get(Calendar.YEAR),
                    selectedResultDate.get(Calendar.MONTH),
                    selectedResultDate.get(Calendar.DAY_OF_MONTH),
                    rTimePicker.getCurrentHour(),
                    rTimePicker.getCurrentMinute());

            long rTime = calendar2.getTimeInMillis();

            docRef.set(new TLDetails(Integer.parseInt(tNOQs.getText().toString().trim()), "false", Integer.parseInt(tDuration.getText().toString().trim()),
                    Integer.parseInt(tMarks.getText().toString().trim()), rTime, sTime, title.getText().toString().trim())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(admin_add_lt.this, "live test details updated.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(admin_add_lt.this, admin_live_test.class);
                    intent.putExtra("subject", getIntent().getStringExtra(("subject")));
                    startActivity(intent);
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

class CustomStartDateValidator implements CalendarConstraints.DateValidator {

    long minDate, maxDate;

    CustomStartDateValidator(long minDate, long maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;

    }

    CustomStartDateValidator(Parcel parcel) {
        minDate = parcel.readLong();
        maxDate = parcel.readLong();
    }

    @Override
    public boolean isValid(long date) {
        return !(minDate > date || isDateLocked(date));
    }

    private boolean isDateLocked(long date) {
        for(TLDetails ut : admin_add_lt.upcomingTest){
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(date);
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(ut.sTime);
            if(c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(minDate);
        dest.writeLong(maxDate);
    }

    public static final Parcelable.Creator<CustomStartDateValidator> CREATOR = new Parcelable.Creator<CustomStartDateValidator>() {

        @Override
        public CustomStartDateValidator createFromParcel(Parcel parcel) {
            return new CustomStartDateValidator(parcel);
        }

        @Override
        public CustomStartDateValidator[] newArray(int size) {
            return new CustomStartDateValidator[size];
        }
    };
}