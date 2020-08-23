package com.example.itifighterAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
    ArrayList<String> tests = new ArrayList<>();

    int count = -1;
    ArrayList<TLDetails> upcomingTest = new ArrayList<>();
    ArrayAdapter<String> adapter, adapter2;
    ListView testListView, testListView2;
    ArrayList<String> upcomingID = new ArrayList<>();

    CollectionReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_live_test);
        testListView = findViewById(R.id.listPastLiveTestsAdmin);
        testListView2 = findViewById(R.id.listUCLiveTestsAdmin);

        //docRef = FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests");
        docRef = FirebaseFirestore.getInstance().collection("section").document("lt")
                .collection("branch").document(""+getIntent().getStringExtra("subject"))
                .collection("tests");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    tests = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("TestInHistory").equals("true")){
                            tests.add(""+document.getString("title"));
                        }else{
                            if(document.getLong("rTime") < Calendar.getInstance().getTimeInMillis()){
                                //update test in history to "true"
                                Map<String, Object> upd = new HashMap<>();
                                final String tempTitle = document.getString("title");
                                upd.put("TestInHistory", "true");
                                docRef.document(document.getId()).update(upd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            tests.add(""+tempTitle);
                                        }
                                    }
                                });
                            }else{
                                upcomingTest.add(new TLDetails(Objects.requireNonNull(document.getLong("NOQs")).intValue(),
                                        document.getString("TestInHistory"),
                                        Objects.requireNonNull(document.getLong("duration")).intValue(),
                                        Objects.requireNonNull(document.getLong("marks")).intValue(),
                                        document.getLong("rTime"),
                                        document.getLong("sTime"),
                                        document.getString("title")));
                                upcomingID.add(document.getId());
                            }
                        }
                        //findViewById(R.id.addBtnLT).setVisibility(View.INVISIBLE);
                        //findViewById(R.id.uploadBtnLT).setVisibility(View.VISIBLE);
                    }
                    count = tests.size() + upcomingTest.size();
                    adapter = new ArrayAdapter<String>(admin_live_test.this, android.R.layout.simple_list_item_1, tests);
                    testListView.setAdapter(adapter);
                    adapter2 = new ArrayAdapter<String>(admin_live_test.this, android.R.layout.simple_list_item_1, upcomingID);
                    testListView2.setAdapter(adapter2);
                    testListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            EditQuesList(i);
                        }
                    });
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
            intent.putExtra("subject", getIntent().getStringExtra("subject"));
            startActivity(intent);
        }
    }

    public void addTest(View v) {
        if(count < 0){
            Toast.makeText(this, "data load in progress....", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(admin_live_test.this, admin_add_lt.class);
        intent.putExtra("count", count);
        Toast.makeText(this, "passing uts: "+upcomingTest.size(), Toast.LENGTH_SHORT).show();
        intent.putExtra("upcoming", (Serializable) upcomingTest);
        intent.putExtra("subject", getIntent().getStringExtra("subject"));
        startActivity(intent);
    }

    public void EditQuesList(int pos) {
        if(upcomingTest.size() <= 0 || count < 0){
            Toast.makeText(this, "please create a test first...", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "selected test id: "+upcomingID.get(pos), Toast.LENGTH_SHORT).show();
        //load upload excel panel and set destination to /section/lt/.
        Intent intent = new Intent(admin_live_test.this, admin_upload_excel.class);
        intent.putExtra("section", "lt");
        intent.putExtra("count", pos);
        intent.putExtra("tid", upcomingID.get(pos));
        intent.putExtra("subject", getIntent().getStringExtra("subject"));
        startActivity(intent);
    }
}