package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import static com.example.itifighterAdmin.admin_edit_subject.PICK_IMAGE_REQUEST;

public class admin_add_chapter extends AppCompatActivity {

    EditText name, desc, price, discount, mpq, timer;
    int count = -1;
    CollectionReference mDatabaseReference;
    boolean ready = false;

    ImageButton subImg;
    String imgName = "", subName = "";
    StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_chapter);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(Objects.requireNonNull(getIntent().getStringExtra("section"))).collection("branch").document(Objects.requireNonNull(getIntent().getStringExtra("subject"))).collection("chapter");
        name = findViewById(R.id.NewChapterName);
        desc = findViewById(R.id.NewChapterDesc);
        price = findViewById(R.id.NewChapterPrice);
        discount = findViewById(R.id.NewChapterDiscount);
        mpq = findViewById(R.id.NewChapterMPQ);
        timer = findViewById(R.id.NewChapterTimer);
        subImg = findViewById(R.id.AddChapterImage);

        price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        discount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        count = getIntent().getIntExtra("count", -1);
    }

    public void PickNewChapImage(View view) {
        /*if(!ready)
            return;*/
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
        final String subImgName = "Chapter_00"+(count+1);
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_LOGOS + subImgName);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressBar.setVisibility(View.GONE);
                        //textViewStatus.setText("File Uploaded Successfully");
                        imgName = subImgName;

                        DocumentReference reference = mDatabaseReference.document("00"+(count+1));
                        Map<String,String> branch = new HashMap<>();
                        branch.put("Name", name.getText().toString().trim());
                        branch.put("description", desc.getText().toString().trim().length() > 0 ? desc.getText().toString().trim() : "--");
                        branch.put("price", price.getText().toString().trim().length() > 0 ? price.getText().toString().trim() : "0.0");
                        branch.put("discount", discount.getText().toString().trim().length() > 0 ? discount.getText().toString().trim() : "0.0");
                        branch.put("MPQ", mpq.getText().toString().trim().length() > 0 ? mpq.getText().toString().trim() : "1");
                        branch.put("Timer", timer.getText().toString().trim().length() > 0 ? timer.getText().toString().trim() : "60");
                        branch.put("Image", imgName);
                        reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Success", "subject updated with name: " + name.getText().toString());
                                Toast.makeText(admin_add_chapter.this, "subject img updated with name: " + name.getText().toString(), Toast.LENGTH_LONG).show();
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
    public void SaveNewChapter(View v) {
        if(count < 0)
            return;
        if(name.getText().toString().trim().length() <= 0){
            Toast.makeText(this, "Chapter name required", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference reference = mDatabaseReference.document("00"+(count+1));
        Map<String,String> branch = new HashMap<>();
        branch.put("Name", name.getText().toString().trim());
        branch.put("description", desc.getText().toString().trim().length() > 0 ? desc.getText().toString().trim() : "--");
        branch.put("price", price.getText().toString().trim().length() > 0 ? price.getText().toString().trim() : "0.0");
        branch.put("discount", discount.getText().toString().trim().length() > 0 ? discount.getText().toString().trim() : "0.0");
        branch.put("MPQ", mpq.getText().toString().trim().length() > 0 ? mpq.getText().toString().trim() : "1");
        branch.put("Timer", timer.getText().toString().trim().length() > 0 ? timer.getText().toString().trim() : "60");
        branch.put("Image", imgName);
        reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success", "chapter added with name: " + name.getText().toString());
                Toast.makeText(admin_add_chapter.this, "chapter added with name: " + name.getText().toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("subject", getIntent().getStringExtra("subject"));
                intent.putExtra("section", getIntent().getStringExtra("section"));
                //startActivity(intent);
                intent.putExtra("newChapter", name.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });



    }
}