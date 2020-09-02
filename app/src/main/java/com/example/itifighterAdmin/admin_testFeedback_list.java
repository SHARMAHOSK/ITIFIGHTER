package com.example.itifighterAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class admin_testFeedback_list extends AppCompatActivity {

    ArrayList<String> listItems=new ArrayList<>();
    ArrayList<String> itemIds = new ArrayList<>();
    ListView tfListView;
    ArrayAdapter<String> adapter;
    CollectionReference mDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_test_feedback_list);
        mDocRef = FirebaseFirestore.getInstance().collection("common").document("post test").collection("feedback");
        tfListView = findViewById(R.id.TestFeedbackList);

        mDocRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        listItems.add("" + document.getId());
                        itemIds.add("" + document.getId());
                    }
                    adapter=new ArrayAdapter<>(admin_testFeedback_list.this,
                            android.R.layout.simple_list_item_1,
                            listItems);
                    tfListView.setAdapter(adapter);
                }
            }
        });

        tfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(admin_testFeedback_list.this, admin_feedbackDetails.class);
                intent.putExtra("feedbackID", itemIds.get(position));
                startActivity(intent);
            }
        });
    }
}