package com.example.itifighterAdmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class admin_add_subject extends AppCompatActivity {

    EditText name, desc;
    ImageButton subImg;
    int count = -1;
    CollectionReference mDatabaseReference;
    String imgName;
    final static int PICK_IMAGE_REQUEST = 72;
    StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_subject);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(Objects.requireNonNull(getIntent().getStringExtra("section"))).collection("branch");
        mStorageReference = FirebaseStorage.getInstance().getReference();
        name = findViewById(R.id.NewSubjectName);
        desc = findViewById(R.id.NewSubjectDesc);
        count = getIntent().getIntExtra("count", -1);

        subImg = findViewById(R.id.AddSubImageAS);
    }

    public void PickNewSubImage(View view) {
        if(count < 0)
            return;
        Intent intent = new Intent();
        /*intent.setType("image/*");*/
        intent.setType("image/png");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user chooses the file
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data) {
        //progressBar.setVisibility(View.VISIBLE);
        if(name.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "Subject name required", Toast.LENGTH_LONG).show();
            return;
        }
        final String subImgName = /*"Subject_"+getIntent().getStringExtra("target")*/name.getText().toString().trim();
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_LOGOS+""+getIntent().getStringExtra("section")+"/"+subImgName);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressBar.setVisibility(View.GONE);
                        //textViewStatus.setText("File Uploaded Successfully");
                        imgName = subImgName;

                        try {
                            Glide.with(getApplicationContext())
                                    .load(mStorageReference.child(Constants.STORAGE_PATH_LOGOS+""+getIntent().getStringExtra("section")+"/" + subImgName))
                                    .into(subImg);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(getApplicationContext(),"something wrong to upload",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });

    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void SaveNewSubject(View v) {
        if(count < 0)
            return;
        if(name.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "Subject name required", Toast.LENGTH_LONG).show();
            return;
        }if(imgName == null || imgName.isEmpty() || imgName.trim().length() <= 0){
            Toast.makeText(this, "Subject image required", Toast.LENGTH_LONG).show();
            return;
        }
        Map<String,String> branch = new HashMap<>();
        /*if(getIntent().getStringExtra("section") == "ts")*/{
            branch.put("name", name.getText().toString().trim());
            branch.put("desc", desc.getText().toString().trim().length() > 0 ? desc.getText().toString().trim() : "--");

            mDatabaseReference.add(branch).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("Success", "subject added with name: " + name.getText().toString());
                    Toast.makeText(admin_add_subject.this, "ts subject added with name: " + name.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("section", getIntent().getStringExtra("section"));
                    //startActivity(intent);
                    intent.putExtra("newSubject", name.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }/*else{
            branch.put("Name", name.getText().toString().trim());
            branch.put("description", desc.getText().toString().trim().length() > 0 ? desc.getText().toString().trim() : "--");
            branch.put("Image", "");
            DocumentReference reference = mDatabaseReference.document("00"+(count+1));
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
            });}*/
    }
}