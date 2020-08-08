package com.example.itifighter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itifighterAdmin.Question;

import java.util.List;

public class TestResultActivity extends AppCompatActivity {

    List<Question> questions;
    int[] sub_ans;
    int total_marks = 0;
    int marks_obtained = 0;
    int _mpq = 1;

    int tca = 0, tra = 0, tsq = 0;
    TextView MO, TM, TCA, TRA, TSQ;

    private ListView listView;
    String[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        _mpq = getIntent().getIntExtra("_mpq", 1);
        sub_ans = getIntent().getIntArrayExtra("sub_ans");
        Toast.makeText(this, "total ques: total ans: "+questions.size()+" : "+sub_ans.length, Toast.LENGTH_LONG).show();
        result = new String[sub_ans.length];

        total_marks = _mpq * questions.size();

        /*listView = (ListView) findViewById(R.id.mt_result_list);*/
        MO = findViewById(R.id.marksObtained);
        TM = findViewById(R.id.totalMarks);
        TCA = findViewById(R.id.TCA);
        TRA = findViewById(R.id.TRA);
        TSQ = findViewById(R.id.TSQ);

        for(int i=0; i< sub_ans.length; i++){
            //result[i] = ""+i;
            Toast.makeText(this, ""+questions.get(i).getAnswer(), Toast.LENGTH_LONG).show();
            if(sub_ans[i] == -1){
                result[i] =  "skipped";
                tsq++;
            }
            else if(sub_ans[i] == Integer.parseInt(questions.get(i).getAnswer())){
                result[i] =  "right";
                marks_obtained += _mpq;
                tca++;
            }else{
                result[i] =  "wrong";
                tra++;
            }
        }

        MO.setText(""+(tca * _mpq));
        TM.setText(""+(questions.size() * _mpq));
        TCA.setText(""+tca);
        TRA.setText(""+tra);;
        TSQ.setText(""+tsq);

        /*ArrayAdapter adapter = new ArrayAdapter<String>(TestResultActivity.this,
                android.R.layout.simple_list_item_1,
                result);

        listView.setAdapter(adapter);*/
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(TestResultActivity.this, MainDashboard.class));
        finish();
    }

    public void FinishExam(View view) {
        //startActivity(new Intent(TestResultActivity.this, MainDashboard.class));
        finish();
    }
}