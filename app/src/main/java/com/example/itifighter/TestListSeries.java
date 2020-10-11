package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.itifighter.TestSeriesX.CustomListItemY;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterZ;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class TestListSeries extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listView;
    private String currentSubject;
    private String currentChapter;
    private ArrayList<CustomListItemY> Series;
    private FirebaseStorage mFirebaseStorage= FirebaseStorage.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list_series);
        listView = findViewById(R.id.testttRecycle);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        currentSubject = intent.getStringExtra("currentSubject");
        currentChapter = intent.getStringExtra("currentChapter");
        String expiryDate = intent.getStringExtra("ExpiryDate");
        String seriesName = intent.getStringExtra("SeriesName");
        String seriesCount = intent.getStringExtra("SeriesCount");
        TextView SeriesNameX = findViewById(R.id.testxy_chapter_title);
        TextView SeriesCountX = findViewById(R.id.testxy_desc_text);
        //TextView ExpiryDateX = findViewById(R.id.testxytbatch);
        ImageView SeriesImage = findViewById(R.id.testxy_image_view);
        StorageReference mmFirebaseStorageRef = mFirebaseStorage.getReference().child("menu/section/ts/chapter/");
        if(seriesName != null){
            if(Objects.requireNonNull(seriesName).trim().length() > 0){
                Glide.with(this)
                        .load(mmFirebaseStorageRef.child(seriesName +".png"))
                        .into(SeriesImage);
            }
        }
        SeriesNameX.setText(seriesName);
        SeriesCountX.setText(seriesCount +" Test");
        //ExpiryDateX.setText(expiryDate);
        LoadTests();
    }

    private void LoadTests() {
        db.collection("section").document("ts")
                .collection("branch").document(currentSubject)
                .collection("exam").document(currentChapter)
                .collection("tests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Series = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Series.add(new CustomListItemY(document.getId(),
                                document.getString("name"), document.getString("duration"),
                                document.getString("qutions"), document.getString("score")));
                    }
                    listView.setAdapter(new CustomListViewArrayAdapterZ(TestListSeries.this,0,Series,currentSubject,currentChapter));
                }
            }
        });
    }
}