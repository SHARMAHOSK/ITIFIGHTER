package com.example.itifighterAdmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.itifighter.R;

import java.util.ArrayList;

public class admin_section_list extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<>();
    ListView sectionListView;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LIST_VIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_section_list);

        sectionListView = findViewById(R.id.list);

        listItems.add("Previous Papers");
        listItems.add("Mock Tests");
        listItems.add("Daily Live Test");
        listItems.add("Test Series");

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        sectionListView.setAdapter(adapter);

        sectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(admin_section_list.this, admin_subject_list.class);
                    //intent.putExtra("subject", "001");  //replace with call to subject list view in mid
                    intent.putExtra("section", "pp");
                    startActivity(intent);
                }
            }
        });
    }
}