package com.example.itifighterAdmin.pp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class admin_add_exam extends AppCompatActivity {

    EditText name, desc;
    int count = -1;
    CollectionReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_exam);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document("pp").collection("branch").document(Objects.requireNonNull(getIntent().getStringExtra("subject"))).collection("exam");
        name = findViewById(R.id.NewExamName);
        desc = findViewById(R.id.NewExamDesc);
        count = getIntent().getIntExtra("count", -1);
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void SaveNewExam(View v) {
        if(count < 0)
            return;
        if(name.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "Exam name required", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference reference = mDatabaseReference.document("00"+(count+1));
        Map<String,String> branch = new HashMap<>();
        branch.put("Name", name.getText().toString().trim());
        branch.put("description", desc.getText().toString().trim().length() > 0 ? desc.getText().toString().trim() : "--");
        branch.put("Image", "");
        reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success", "exam added with name: " + name.getText().toString());
                Toast.makeText(admin_add_exam.this, "exam added with name: " + name.getText().toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("subject", getIntent().getStringExtra("subject"));
                //startActivity(intent);
                intent.putExtra("newExam", name.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });



    }
}