package com.example.itifighterAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class admin_upload_excel extends AppCompatActivity implements View.OnClickListener {

    //this is the pic excel(imaginary code or whatever, idk) code used in file chooser
    final static int PICK_EXCEL_CODE = 2343;

    //these are the views
    TextView textViewStatus;
    ProgressBar progressBar;
    String targetSection, targetSubject, targetChapter;

    //the firebase objects for storage and database
    StorageReference mStorageReference;
    CollectionReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload_excel);

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("exam");

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
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // MIME-type for .xls: "application/vnd.ms-excel"
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
                parseFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this method is parsing the file
    private void parseFile(Uri data) {
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

        /*FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                XSSFRow row = (XSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                List data = new ArrayList();
                while (cells.hasNext()) {
                    XSSFCell cell = (XSSFCell) cells.next();
                    data.add(cell);
                }

                sheetData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUploadExcel:
                getExcel();
                break;
        }
    }
}