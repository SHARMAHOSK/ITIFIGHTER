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
import android.widget.Toast;

import com.example.itifighter.R;
import com.example.itifighterAdmin.pp.admin_pp_list;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class admin_subject_list extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> ItemId = new ArrayList<>();
    ListView sectionListView;
    int count = -1;
    int targetSubject = -1;
    CollectionReference mDatabaseReference;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LIST_VIEW
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_subject_list);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(Objects.requireNonNull(getIntent().getStringExtra("section"))).collection("branch");

        sectionListView = findViewById(R.id.adminListSubject);

        /*listItems.add("Fitter");
        listItems.add("Turner");
        listItems.add("Machinist");
        listItems.add("Electrician");
        listItems.add("Copa");*/

        mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    listItems = new ArrayList<>();
                    ItemId = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /*listItems.add("" + document.getString("Name"));*/
                        listItems.add("" + document.getString("name"));

                        ItemId.add("" + document.getId());
                    }
                    count = listItems.size();
                    adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            listItems);
                    sectionListView.setAdapter(adapter);
                    sectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                                targetSubject = position;
                                SubjectOptions(view);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void SubjectOptions(View v) {
        Intent intent = new Intent(admin_subject_list.this, admin_item_options.class);
        startActivityForResult(intent, 0);
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addSubject(View v) {
        if(count < 0)
            return;
        Intent intent = new Intent(admin_subject_list.this, admin_add_subject.class);
        intent.putExtra("count", count);
        intent.putExtra("section", getIntent().getStringExtra("section"));
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                int option = data.getIntExtra("option", 0);
                if(option == 1)
                    EditSubject();
                else if(option == 2)
                    OpenSubject();
            }
        }
        else if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String newSubject = data.getStringExtra("newSubject");
                //adapter.add(newSubject);
                mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listItems = new ArrayList<>();
                            ItemId = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listItems.add("" + document.getString("Name"));
                                ItemId.add("" + document.getId());
                            }
                            count = listItems.size();
                            adapter = new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_list_item_1,
                                    listItems);
                            sectionListView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        }
    }

    private void EditSubject() {
        if(targetSubject < 0)
            return;
        Intent intent = new Intent(admin_subject_list.this, admin_edit_subject.class);
        intent.putExtra("target", ItemId.get(targetSubject));
        intent.putExtra("section", getIntent().getStringExtra("section"));
        startActivityForResult(intent, 1);
    }

    private void OpenSubject() {
        if(targetSubject < 0)
            return;
        Intent intent;
        if(getIntent().getStringExtra("section").contains("pp")){
            intent = new Intent(admin_subject_list.this, admin_pp_list.class);
            intent.putExtra("subject", ItemId.get(targetSubject));
            Toast.makeText(admin_subject_list.this, intent.getStringExtra("subject") + "=" + ItemId.get(targetSubject), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }else if(getIntent().getStringExtra("section").contains("mt")){
            intent = new Intent(admin_subject_list.this, admin_chapter_list.class);
            intent.putExtra("subject", ItemId.get(targetSubject));
            intent.putExtra("section", getIntent().getStringExtra("section"));
            Toast.makeText(admin_subject_list.this, intent.getStringExtra("subject") + "=" + ItemId.get(targetSubject), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }else if(getIntent().getStringExtra("section").contains("ts")){
            intent = new Intent(admin_subject_list.this, admin_chapter_list.class);
            intent.putExtra("subject", ItemId.get(targetSubject));
            intent.putExtra("section", getIntent().getStringExtra("section"));
            Toast.makeText(admin_subject_list.this, intent.getStringExtra("subject") + "=" + ItemId.get(targetSubject), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }else if(getIntent().getStringExtra("section").contains("lt")){
            intent = new Intent(admin_subject_list.this, admin_live_test.class);
            intent.putExtra("subject", ItemId.get(targetSubject));
            Toast.makeText(admin_subject_list.this, intent.getStringExtra("subject") + "=" + ItemId.get(targetSubject), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

    }
}