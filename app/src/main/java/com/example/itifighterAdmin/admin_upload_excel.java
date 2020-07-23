package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itifighter.R;
import com.example.itifighterAdmin.pp.AdminUpdatePpPdfs;
import com.example.itifighterAdmin.pp.admin_pdf_list;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class admin_upload_excel extends AppCompatActivity implements View.OnClickListener {

    //this is the pic excel(imaginary code or whatever, idk) code used in file chooser
    final static int PICK_EXCEL_CODE = 2343;

    //these are the views
    TextView textViewStatus;
    ProgressBar progressBar;
    String targetSection, targetSubject, targetChapter;
    Workbook workbook;
    ArrayList<Question> questions;
    int count = 0;

    //the firebase objects for storage and database
    StorageReference mStorageReference;
    CollectionReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload_excel);

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("chapter");
        count = getIntent().getIntExtra("count", -1);
        if(count < 0){
            Toast.makeText(this, "go back", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(admin_upload_excel.this, admin_mockChapQoes_list.class);
            intent.putExtra("subject", targetSubject);
            intent.putExtra("section", targetSection);
            intent.putExtra("chapter", targetChapter);
            startActivity(intent);
            finish();
        }

        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("question");

        //getting the views
        textViewStatus = (TextView) findViewById(R.id.excelTextViewStatus);
        progressBar = (ProgressBar) findViewById(R.id.excelProgressbar);

        //attaching listeners to views
        findViewById(R.id.buttonUploadExcel).setOnClickListener(this);
    }

    //this function will get the excel from the storage
    private void getExcel() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }*/

        //creating an intent for file chooser
        Intent intent = new Intent();
        //MIME-type for .xlsx...
        intent.setType("application/vnd.ms-excel"); // MIME-type for .xls: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select question sheet"), PICK_EXCEL_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user chooses the file
        if (requestCode == PICK_EXCEL_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                try {
                    parseFile(data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this method is parsing the file
    private void parseFile(Uri data) throws IOException, BiffException {
        progressBar.setVisibility(View.VISIBLE);
        /*final String pdfName = ""+System.currentTimeMillis()+".pdf";
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + pdfName);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        textViewStatus.setText("File Uploaded Successfully");
                        final String fileName = editTextFilename.getText().toString();
                        DocumentReference reference = mDatabaseReference.document(fileName);
                        Map<String,String> branch = new HashMap<>();
                        branch.put("Name", pdfName);
                        reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Success", "pdf added with name: " + fileName+"["+pdfName+"]");
                                Toast.makeText(admin_upload_excel.this, "pdf added with name: " + fileName+"["+pdfName+"]", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(admin_upload_excel.this, admin_mockChapQoes_list.class);
                                intent.putExtra("subject", targetSubject);
                                intent.putExtra("exam", targetExam);
                                startActivity(intent);
                                finish();
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
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });*/

        questions = new ArrayList<>();
        InputStream fis = null;

        File temp= File.createTempFile("tempExcel",".xls",admin_upload_excel.this.getCacheDir());

        OutputStream out = null;
        try{
            fis = getContentResolver().openInputStream(data);;
            /*
            out = new FileOutputStream(temp);
            copyFile(fis, out);
            fis.close();
            out.flush();
            out.close();
            out = null;*/
            WorkbookSettings ws = new WorkbookSettings();
            ws.setGCDisabled(true);
            workbook = Workbook.getWorkbook(fis);

            Sheet sheet = workbook.getSheet(0);
            for(int i =0; i < sheet.getRows(); i++){
                Cell[] row = sheet.getRow(i);
                Toast.makeText(this, "rows: "+sheet.getRows()+" cols: "+sheet.getRow(0).length, Toast.LENGTH_SHORT).show();
                //make class for question module and add properties like new Question(row[0].getContents, row[1].getContents, ...);
                if(row.length > 5)
                questions.add(new Question(row[0].getContents(), row[1].getContents(), row[2].getContents(), row[3].getContents(), row[4].getContents(), row[5].getContents()));
            }

            WriteBatch batch = FirebaseFirestore.getInstance().batch();

            for (Question ques: questions) {
                DocumentReference nycRef = mDatabaseReference.document("Question "+ (++count));
                batch.set(nycRef, ques);
            }

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //Toast.makeText(admin_upload_excel.this, "questions added successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(admin_upload_excel.this, admin_mockChapQoes_list.class);
                    intent.putExtra("subject", targetSubject);
                    intent.putExtra("section", targetSection);
                    intent.putExtra("chapter", targetChapter);
                    startActivity(intent);
                    finish();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "some error occurred while parsing.", Toast.LENGTH_SHORT).show();
        }finally {
            if(fis != null){
                fis.close();
            }
        }
        
        
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUploadExcel:
                getExcel();
                break;
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}