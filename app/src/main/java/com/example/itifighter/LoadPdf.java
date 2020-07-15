package com.example.itifighter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoadPdf extends AppCompatActivity {

    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    FirebaseStorage mFirebaseStorage;
    StorageReference mmFirebaseStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_load_pdf);

        pdfView= (PDFView)findViewById(R.id.pdfView);

        mFirebaseStorage= FirebaseStorage.getInstance();
        mmFirebaseStorageRef=mFirebaseStorage.getReference().child("uploads");
        final long ONE_MEGABYTE = 1024 * 1024;

        mmFirebaseStorageRef.child(getIntent().getStringExtra("pdf")).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                pdfView.fromBytes(bytes).load();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoadPdf.this,"download unsuccessful", Toast.LENGTH_LONG).show();
            }
        });
    }
}