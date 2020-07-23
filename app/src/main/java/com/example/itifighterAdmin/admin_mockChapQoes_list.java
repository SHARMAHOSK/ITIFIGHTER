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

import com.example.itifighter.LoadPdf;
import com.example.itifighter.R;
import com.example.itifighterAdmin.admin_upload_excel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class admin_mockChapQoes_list extends AppCompatActivity {

    CollectionReference mDatabaseReference;
    String targetSection, targetSubject, targetChapter;
    ArrayList<String> quesName, quesID;
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    ListView quesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mock_chap_qoes_list);

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("chapter");

        quesListView = findViewById(R.id.listMockQuesAdmin);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("question");

        mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    quesName = new ArrayList<>();
                    quesID = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        quesID.add(""+document.getId());
                        quesName.add(""+document.getString("question"));
                    }

                    adapter=new ArrayAdapter<String>(admin_mockChapQoes_list.this,
                            android.R.layout.simple_list_item_1,
                            quesName);
                    quesListView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        quesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                /*Intent intent = new Intent(admin_mockChapQoes_list.this, admin_edit_ques.class);
                intent.putExtra("quesID", quesID.get(position));
                startActivity(intent);*/

            }

        });
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void UploadMockQuestions(View v) {
        //adapter.add("New Item");
        Intent intent = new Intent(admin_mockChapQoes_list.this, admin_upload_excel.class);
        intent.putExtra("subject", targetSubject);
        intent.putExtra("chapter", targetChapter);
        intent.putExtra("section", targetSection);
        startActivity(intent);
    }
}