package com.example.itifighterAdmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.itifighter.R;

public class admin_item_options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_options);
    }

    public void returnEditCode(View view) {
        Toast.makeText(admin_item_options.this, "edit option selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("option", 1);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void returnOpenCode(View view) {
        Toast.makeText(admin_item_options.this, "open option selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("option", 2);
        setResult(RESULT_OK, intent);
        finish();
    }
}