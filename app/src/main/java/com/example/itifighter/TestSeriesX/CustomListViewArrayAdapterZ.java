package com.example.itifighter.TestSeriesX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.itifighter.R;

import java.util.ArrayList;

public class CustomListViewArrayAdapterZ extends ArrayAdapter<CustomListItemY> {

    private ArrayList<CustomListItemY> Series;
    private Context context;
    private LayoutInflater inflater;
    private String currentSubject,currentChapter,currentTest;
    private Handler handler;

    @Override
    public int getCount() { return Series.size(); }

    @Nullable
    @Override
    public CustomListItemY getItem(int position) { return  Series.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CustomListViewArrayAdapterZ(@NonNull Context context, int resource, ArrayList<CustomListItemY> Series,
                                       String currentSubject, String currentChapter) {
        super(context, resource, Series);
        this.context = context;
        this.Series = Series;
        this.currentSubject = currentSubject;
        this.currentChapter = currentChapter;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final CustomListItemY property = Series.get(position);
        currentTest = property.getTestId();
        if (inflater == null) inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.test_series_tests, null);
        TextView Title = view.findViewById(R.id.testxyztitle),desc = view.findViewById(R.id.text_xy_desc);
        //Button start = view.findViewById(R.id.buttonTestStart),result = view.findViewById(R.id.buttonTestResult);
        Title.setText(property.getTestName());
        desc.setText(property.getTestQuetion()+" Qs | "+property.getTestDuration()+" Mins | "+property.getTestScore()+" Marks");
       /* start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TestInstructionsActivityX.class);
                intent.putExtra("currentSubject",currentSubject);
                intent.putExtra("currentChapter",currentChapter);
                intent.putExtra("currentTest",currentTest);
                context.startActivity(intent);
            }
        });*/
        return view;
    }
}
