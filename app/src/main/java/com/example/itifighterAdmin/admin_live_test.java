package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
/*

public class admin_live_test extends AppCompatActivity {

    TextView title, sTime, tDuration, rTime, tMarks, nOQs;
    boolean newTestAdded = false;

    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_live_test);

        docRef = FirebaseFirestore.getInstance().collection("section").document("lt");

        title = findViewById(R.id.Title);
        sTime = findViewById(R.id.STime);
        tDuration = findViewById(R.id.Duration);
        rTime = findViewById(R.id.RTime);
        tMarks = findViewById(R.id.ltMarks);
        nOQs = findViewById(R.id.NOQs);

        Calendar cc = Calendar.getInstance();

        title.setText("");
        sTime.setText(""+cc.getTimeInMillis());
        tDuration.setText("0");
        rTime.setText(""+cc.getTimeInMillis());
        tMarks.setText("1");
        nOQs.setText("0");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        title.setText(document.getString("title"));
                        sTime.setText(document.getString("sTime"));
                        tDuration.setText(document.getString("duration"));
                        rTime.setText(document.getString("rTime"));
                        tMarks.setText(document.getString("marks"));
                        nOQs.setText(document.getString("NOQs"));
                        newTestAdded = document.getString("NewTestAdded") != null && document.getString("NewTestAdded").equals("true");
                    } else {
                        Toast.makeText(admin_live_test.this, "no such document in database", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(admin_live_test.this, "got failed with: "+task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void EditDetails(View view) {
        if(newTestAdded && Calendar.getInstance().getTimeInMillis() >= Long.parseLong(sTime.getText().toString().trim())){
            Toast.makeText(this, "test is live!", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(admin_live_test.this, admin_edit_lt_details.class);
            intent.putExtra("title", title.getText().toString());
            intent.putExtra("st", sTime.getText().toString());
            intent.putExtra("rt", rTime.getText().toString());
            intent.putExtra("duration", tDuration.getText().toString());
            intent.putExtra("mpq", tMarks.getText().toString());
            intent.putExtra("noq", nOQs.getText().toString());
            intent.putExtra("newTestAdded", newTestAdded);
            startActivity(intent);
        }
    }
*/

public class admin_live_test extends AppCompatActivity {

    TextView title, sTime, tDuration, rTime, tMarks, nOQs;
    boolean newTestAdded = false;
    ArrayList<String> tests = new ArrayList<>();
    ArrayList<String> testIds = new ArrayList<>();

    int count = -1;
    TLDetails upcomingTest = null;
    ArrayAdapter<String> adapter;
    ListView testListView;
    String upcomingID;

    CollectionReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_live_test);
        testListView = findViewById(R.id.listLiveTestsAdmin);

        docRef = FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    tests = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("TestInHistory").equals("true")){
                            tests.add(""+document.getString("title"));
                            testIds.add(""+document.getId());
                        }else{
                            upcomingTest = new TLDetails(Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                    document.getString("TestInHistory"),
                                    Objects.requireNonNull(document.getLong("duration")).intValue(),
                                    Objects.requireNonNull(document.getLong("marks")).intValue(),
                                    document.getLong("rTime"),
                                    document.getLong("sTime"),
                                    document.getString("title"));
                            findViewById(R.id.UTD).setVisibility(View.VISIBLE);
                            TextView title, duration, mpq, sTime, rTime;
                            title = findViewById(R.id.uTitle);
                            duration = findViewById(R.id.uDuration);
                            mpq = findViewById(R.id.uMPQ);
                            sTime = findViewById(R.id.uSTime);
                            rTime = findViewById(R.id.uRTime);
                            title.setText("TITLE: "+upcomingTest.title);
                            duration.setText("DURATION: "+upcomingTest.duration);
                            mpq.setText("MARKS PER QUESTION: "+upcomingTest.marks);
                            sTime.setText("TEST START TIME: "+ upcomingTest.sTime);
                            rTime.setText("RESULT DECLARATION TIME: "+upcomingTest.rTime);
                            upcomingID = document.getId();
                        }
                        findViewById(R.id.addBtnLT).setVisibility(View.INVISIBLE);
                        findViewById(R.id.uploadBtnLT).setVisibility(View.VISIBLE);
                    }
                    count = tests.size();
                    adapter = new ArrayAdapter<String>(admin_live_test.this, android.R.layout.simple_list_item_1, tests);
                    testListView.setAdapter(adapter);
                }else{
                    //Log.d(TAG,"error getting tests: ", task.getException());
                }
            }
        });

        /*testListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditDetails(testIds.get(position));
            }
        });*/
    }

    public void EditDetails(View view) {
        if(count < 0){
            Toast.makeText(this, "loading data, wait...", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Intent intent = new Intent(admin_live_test.this, admin_edit_lt_details.class);
            intent.putExtra("test", upcomingID);
            startActivity(intent);
        }
    }

    public void addTest(View v) {
        if(count < 0 || upcomingTest != null){
            Toast.makeText(this, "test already created. cannot add more than 1 at a time.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(admin_live_test.this, admin_add_lt.class);
        intent.putExtra("count", count);
        startActivity(intent);
    }

    public void EditQuesList(View view) {
        if(upcomingTest == null || count < 0){
            Toast.makeText(this, "please create a test first...", Toast.LENGTH_SHORT).show();
            return;
        }
        //load upload excel panel and set destination to /section/lt/.
        Intent intent = new Intent(admin_live_test.this, admin_upload_excel.class);
        intent.putExtra("section", "lt");
        intent.putExtra("count", count);
        startActivity(intent);
    }
}