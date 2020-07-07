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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class admin_pdf_list extends AppCompatActivity {

    CollectionReference mDatabaseReference;
    String targetSubject, targetExam;
    ArrayList<String> pdfName, pdfFile;
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    ListView pdfListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pdf_list);

        targetSubject = getIntent().getStringExtra("subject");
        targetExam = getIntent().getStringExtra("exam");

        pdfListView = findViewById(R.id.listPdfAdmin);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("branch").document(targetSubject).collection("exam").document(targetExam).collection("pdf");

        mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    pdfName = new ArrayList<>();
                    pdfFile = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        pdfName.add(""+document.getId());
                        pdfFile.add(""+document.getString("Name"));
                    }

                    adapter=new ArrayAdapter<String>(admin_pdf_list.this,
                            android.R.layout.simple_list_item_1,
                            pdfName);
                    pdfListView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        pdfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Intent intent = new Intent(admin_pdf_list.this, LoadPdf.class);
                intent.putExtra("pdf", pdfFile.get(position));
                startActivity(intent);

            }

        });
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addPDF(View v) {
        //adapter.add("New Item");
        Intent intent = new Intent(admin_pdf_list.this, AdminUpdatePpPdfs.class);
        intent.putExtra("subject", targetSubject);
        intent.putExtra("exam", targetExam);
        startActivity(intent);
    }
}