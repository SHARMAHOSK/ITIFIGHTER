package com.example.itifighter.TestSeriesX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.itifighter.R;
import com.example.itifighter.TestInstructionsActivityX;

import java.util.ArrayList;
import java.util.List;

public class CustomListViewArrayAdapterY extends ArrayAdapter<CustomListItemY> {
    private Context context;
    private List<CustomListItemY> Tests;
    private LayoutInflater inflater;
    private ArrayList<String> TestId;
    private String currentSubject,currentChapter;
    @Override
    public int getCount() { return Tests.size(); }

    @Nullable
    @Override
    public CustomListItemY getItem(int position) { return Tests.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CustomListViewArrayAdapterY(Context context, int resource, ArrayList<CustomListItemY> objects,ArrayList<String> TestId,String currentSubject,String currentChapter) {
        super(context, resource, objects);
        this.context = context;
        this.Tests = objects;
        this.TestId = TestId;
        this.currentSubject=currentSubject;
        this.currentChapter=currentChapter;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        CustomListItemY property = Tests.get(position);
        if (inflater == null) inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.test_series_tests, null);
        TextView test = view.findViewById(R.id.testxyzquetion);
        TextView topicHeader = view.findViewById(R.id.testxyztitle);
        TextView duration = view.findViewById(R.id.testxyztime);
        TextView score = view.findViewById(R.id.testxyzscore);
        Button b = view.findViewById(R.id.buttonxy);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(), TestInstructionsActivityX.class);
                myIntent.putExtra("currentSubject",currentSubject);
                myIntent.putExtra("currentChapter",currentChapter);
                myIntent.putExtra("position",TestId.get(view.getId()));
                context.startActivity(myIntent);
            }
        });

        topicHeader.setText(property.getTopicHeader());
        if(property.getQuetion() != null)test.setText(property.getQuetion()+" Quetions");
        else test.setText("0 Quetions");
        if(property.getTopicHeader()!=null)topicHeader.setText(property.getTopicHeader());
        else topicHeader.setText("Test 0");
        if(property.getDuration()!=null)duration.setText(property.getDuration()+" months");
        else duration.setText("0 months");
        if(property.getScore()!=null)score.setText("max "+ property.getScore() +" score");
        else score.setText("max 0 score");
        return b;
    }
}
