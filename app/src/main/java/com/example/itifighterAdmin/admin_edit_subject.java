package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class admin_edit_subject extends AppCompatActivity {

    ImageButton subImg;
    EditText name, desc;
    String imgName = "", subName = "", subDesc = "";
    CollectionReference mDatabaseReference;
    final static int PICK_IMAGE_REQUEST = 72;
    StorageReference mStorageReference;

    boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_subject);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("branch");
        mStorageReference = FirebaseStorage.getInstance().getReference();

        name = findViewById(R.id.EdtSubjectName);
        desc = findViewById(R.id.EdtSubjectDesc);
        subImg = findViewById(R.id.EdtSubImage);

        DocumentReference reference = mDatabaseReference.document(""+(getIntent().getStringExtra("target")));

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        subName = document.getString("Name");
                        subDesc = document.getString("description");
                        imgName = document.getString(("Image"));

                        ready = true;
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });


    }

    public void PickNewSubImage(View view) {
        if(!ready)
            return;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
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
        final String subImgName = "Subject_"+getIntent().getStringExtra("target");
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_LOGOS + subImgName);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressBar.setVisibility(View.GONE);
                        //textViewStatus.setText("File Uploaded Successfully");
                        imgName = subImgName;

                        DocumentReference reference = mDatabaseReference.document(""+(getIntent().getStringExtra("target")));
                        Map<String,String> branch = new HashMap<>();
                        branch.put("Image", imgName);
                        branch.put("Name", subName);
                        branch.put("description", subDesc);
                        reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Success", "subject updated with name: " + name.getText().toString());
                                Toast.makeText(admin_edit_subject.this, "subject img updated with name: " + name.getText().toString(), Toast.LENGTH_LONG).show();
                                try {
                                    Glide.with(getApplicationContext())
                                            .load(mStorageReference.child(Constants.STORAGE_PATH_LOGOS + subImgName))
                                            .into(subImg);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"something wrong to upload",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
    public void SaveEdtSubject(View v) {
        if(name.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "Subject name required", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference reference = mDatabaseReference.document(""+(getIntent().getStringExtra("target")));
        Map<String,String> branch = new HashMap<>();
        branch.put("Name", name.getText().toString().trim());
        branch.put("description", desc.getText().toString().trim().length() > 0 ? desc.getText().toString().trim() : "--");
        branch.put("Image", imgName);
        reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success", "subject updated with name: " + name.getText().toString());
                Toast.makeText(admin_edit_subject.this, "subject updated with name: " + name.getText().toString(), Toast.LENGTH_LONG).show();
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