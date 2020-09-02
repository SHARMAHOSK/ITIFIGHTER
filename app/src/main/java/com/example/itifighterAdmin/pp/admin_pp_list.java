package com.example.itifighterAdmin.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class admin_pp_list extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> Exams=new ArrayList<String>();
    ArrayList<String> ExamIds=new ArrayList<String>();
    int count = -1;
    ListView ppListView;
    String targetSubject;
    CollectionReference mDatabaseReference;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pp_list);
        Intent intent = getIntent();
        targetSubject = intent.getStringExtra("subject");
        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document("pp").collection("branch").document(targetSubject).collection("exam");

        ppListView = findViewById(R.id.listPPAdmin);

        LoadAndSetExamList();

        ppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(admin_pp_list.this);
                builder.setCancelable(true);
                builder.setMessage("Select an action...");
                builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(admin_pp_list.this, admin_pdf_list.class);
                        intent.putExtra("subject", targetSubject);
                        intent.putExtra("exam", ExamIds.get(pos));
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete document from db
                        final DialogInterface _fd = dialog;
                        //recursive delete docs
                        mDatabaseReference.document(ExamIds.get(pos)).collection("pdf").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        final String pdfFile = document.getString("Name");
                                        document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseStorage.getInstance().getReference().child("uploads").child(pdfFile).delete();
                                            }
                                        });
                                    }
                                    mDatabaseReference.document(ExamIds.get(pos)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                Toast.makeText(admin_pp_list.this, "successfully deleted exam", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(admin_pp_list.this, "failed to delete exam", Toast.LENGTH_SHORT).show();
                                            LoadAndSetExamList();
                                            _fd.cancel();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    void LoadAndSetExamList(){
        mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Exams = new ArrayList<>();
                    ExamIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Exams.add(""+document.getString("name"));
                        ExamIds.add((""+document.getId()));
                    }
                    count = Exams.size();
                    adapter=new ArrayAdapter<String>(admin_pp_list.this,
                            android.R.layout.simple_list_item_1,
                            Exams);
                    ppListView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
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
        if(count < 0)
            return;
        Intent intent = new Intent(admin_pp_list.this, admin_add_exam.class);
        intent.putExtra("count", count);
        intent.putExtra("section", "pp");
        intent.putExtra("subject", getIntent().getStringExtra("subject"));

        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                /*String newExam = data.getStringExtra("newExam");
                adapter.add(newExam);*/
                LoadAndSetExamList();
            }
        }
    }
}