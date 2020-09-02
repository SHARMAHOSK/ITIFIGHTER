package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class admin_edit_TestInstruction extends AppCompatActivity {

    EditText etTI;
    Button saveBtn;
    DocumentReference mDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit__test_instruction);
        mDocRef = FirebaseFirestore.getInstance().collection("common").document("pre test");
        etTI = findViewById(R.id.EditTestInstruction);
        saveBtn = findViewById(R.id.SaveTestInstructionEdit);
        saveBtn.setVisibility(View.INVISIBLE);
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.getString("instruction") != null) etTI.setText(documentSnapshot.getString("instruction"));
                saveBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    public void SaveInstructions(View view){
        if(etTI.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "instruction shall not be blank..", Toast.LENGTH_SHORT).show();
            return;
        }
        saveBtn.setClickable(false);
        Map<String, String> branch = new HashMap<>();
        branch.put("instruction", etTI.getText().toString().trim());
        mDocRef.set(branch).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(admin_edit_TestInstruction.this, "instructions updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(admin_edit_TestInstruction.this, "unexpected error, try again", Toast.LENGTH_SHORT).show();
                    saveBtn.setClickable(true);
                }
            }
        });
    }
}