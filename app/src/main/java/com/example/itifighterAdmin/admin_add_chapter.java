package com.example.itifighterAdmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class admin_add_chapter extends AppCompatActivity {

    EditText name, desc, price, discount;
    int count = -1;
    CollectionReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_chapter);

        mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(Objects.requireNonNull(getIntent().getStringExtra("section"))).collection("branch").document(Objects.requireNonNull(getIntent().getStringExtra("subject"))).collection("chapter");
        name = findViewById(R.id.NewChapterName);
        desc = findViewById(R.id.NewChapterDesc);
        price = findViewById(R.id.NewChapterPrice);
        discount = findViewById(R.id.NewChapterDiscount);

        price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        discount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        count = getIntent().getIntExtra("count", -1);
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
        branch.put("Image", "");
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