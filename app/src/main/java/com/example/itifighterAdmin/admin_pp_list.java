package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class admin_pp_list extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> Exams=new ArrayList<String>();
    ArrayList<String> ExamIds=new ArrayList<String>();
    ListView ppListView;
    String targetSubject;
    CollectionReference mDatabaseReference;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pp_list);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("branch").document(targetSubject).collection("exam");

        Intent intent = getIntent();
        targetSubject = intent.getStringExtra("subject");

        ppListView = findViewById(R.id.listPPAdmin);

        mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Exams = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Exams.add(""+document.getString("Name"));
                        ExamIds.add((""+document.getId()));
                    }

                    adapter=new ArrayAdapter<String>(admin_pp_list.this,
                            android.R.layout.simple_list_item_1,
                            Exams);
                    ppListView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        ppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                        Intent intent = new Intent(admin_pp_list.this, admin_pdf_list.class);
                        intent.putExtra("subject", targetSubject);
                        intent.putExtra("exam", ExamIds.get(position));
                        startActivity(intent);

                }

        });
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v) {
        //adapter.add("New Item");
        /*Intent intent = new Intent(admin_pp_list.this, AdminUpdatePpPdfs.class);
        intent.putExtra("subject", targetSubject);
        intent.putExtra("exam", targetExam);
        startActivity(intent);*/
    }
}