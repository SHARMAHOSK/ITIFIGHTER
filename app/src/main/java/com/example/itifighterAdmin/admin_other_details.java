package com.example.itifighterAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighter.R;

import java.util.ArrayList;

public class admin_other_details extends AppCompatActivity {

    ArrayList<String> listItems=new ArrayList<>();
    ListView odListView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_other_details);

        odListView = findViewById(R.id.OtherDetailsList);

        listItems.add("Test Instructions");
        listItems.add("Student Feedback");

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        odListView.setAdapter(adapter);

        odListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent;
                switch(position){
                    case 0:
                        intent = new Intent(admin_other_details.this, admin_edit_TestInstruction.class);
                        startActivityForResult(intent, 0);
                        break;
                    case 1:
                        intent = new Intent(admin_other_details.this, admin_testFeedback_list.class);
                        startActivityForResult(intent, 1);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}