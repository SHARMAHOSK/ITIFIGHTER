package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class admin_add_subject extends AppCompatActivity {

    EditText name, desc;
    int count = -1;
    CollectionReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_subject);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("branch");

        name = findViewById(R.id.NewSubjectName);
        desc = findViewById(R.id.NewSubjectDesc);
        count = getIntent().getIntExtra("count", -1);
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void SaveNewSubject(View v) {
        if(count < 0)
            return;
        if(name.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "Subject name required", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference reference = mDatabaseReference.document("00"+(count+1));
        Map<String,String> branch = new HashMap<>();
        branch.put("Name", name.getText().toString().trim());
        branch.put("description", desc.getText().toString().trim().length() > 0 ? desc.getText().toString().trim() : "--");
        branch.put("image", "");
        reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success", "subject added with name: " + name.getText().toString());
                Toast.makeText(admin_add_subject.this, "pdf added with name: " + name.getText().toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("section", getIntent().getStringExtra("section"));
                //startActivity(intent);
                intent.putExtra("newSubject", name.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });



    }
}