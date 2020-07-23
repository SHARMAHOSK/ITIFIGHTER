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

import com.example.itifighter.R;
import com.example.itifighterAdmin.pp.admin_add_exam;
import com.example.itifighterAdmin.pp.admin_mockChapQoes_list;
import com.example.itifighterAdmin.pp.admin_pdf_list;
import com.example.itifighterAdmin.pp.admin_pp_list;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class admin_chapter_list extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> Chapters=new ArrayList<String>();
    ArrayList<String> ChapterIds=new ArrayList<String>();
    int count = -1;
    ListView chapterListView;
    String targetSubject, targetSection;
    CollectionReference mDatabaseReference;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chapter_list);

        Intent intent = getIntent();
        targetSubject = intent.getStringExtra("subject");
        targetSection = intent.getStringExtra(("section"));
        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter");

        chapterListView = findViewById(R.id.listChapterAdmin);

        mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Chapters = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Chapters.add(""+document.getString("Name"));
                        ChapterIds.add((""+document.getId()));
                    }
                    count = Chapters.size();
                    adapter=new ArrayAdapter<String>(admin_chapter_list.this,
                            android.R.layout.simple_list_item_1,
                            Chapters);
                    chapterListView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        chapterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Intent intent = new Intent(admin_chapter_list.this, admin_mockChapQoes_list.class);
                intent.putExtra("subject", targetSubject);
                intent.putExtra("section", targetSection);
                intent.putExtra("chapter", ChapterIds.get(position));
                startActivity(intent);

            }

        });
    }

    public void addChapter(View v) {
        if(count < 0)
            return;
        Intent intent = new Intent(admin_chapter_list.this, admin_add_chapter.class);
        intent.putExtra("count", count);
        intent.putExtra("subject", getIntent().getStringExtra("subject"));
        intent.putExtra("section", getIntent().getStringExtra("section"));
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String newChapter = data.getStringExtra("newChapter");
                adapter.add(newChapter);
            }
        }
    }
}