package com.example.itifighter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.itifighterAdmin.Question;

import java.util.List;

public class TestResultActivity extends AppCompatActivity {

    List<Question> questions;
    int[] sub_ans;

    private ListView listView;
    String[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        sub_ans = getIntent().getIntArrayExtra("sub_ans");
        Toast.makeText(this, "total ques: total ans: "+questions.size()+" : "+sub_ans.length, Toast.LENGTH_LONG).show();
        result = new String[sub_ans.length];

        listView = (ListView) findViewById(R.id.mt_result_list);

        for(int i=0; i< sub_ans.length; i++){
            //result[i] = ""+i;
            Toast.makeText(this, ""+questions.get(i).getAnswer(), Toast.LENGTH_LONG).show();
            result[i] = sub_ans[i] == -1 ? "skipped" : sub_ans[i] == Integer.parseInt(questions.get(i).getAnswer()) ? "right" : "wrong";
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(TestResultActivity.this,
                android.R.layout.simple_list_item_1,
                result);

        listView.setAdapter(adapter);
    }
}