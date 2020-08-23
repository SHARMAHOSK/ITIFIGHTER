package com.example.itifighter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.itifighter.TestSeriesX.CustomListItemX;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyTestSeries extends Fragment {
    private ArrayList<CustomListItemX> Chapters;
    private ListView listView;
    private FirebaseFirestore db;
    private Context mContext;


    public MyTestSeries() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mtView = inflater.inflate(R.layout.fragment_my_test_series, container, false);
        //listView = mtView.findViewById(R.id.testxtRecycle);
        //LoadChapters();
        return mtView;
    }
  /*  void LoadChapters(){
        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("exam").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Chapters = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Chapters.add(new CustomListItemX(document.getString("name"), document.getString("test"), "ts",document.getId(),document.getString("month1"),document.getString("month2"),document.getString("month3"),document.getString("price1"),document.getString("price2"),document.getString("price3"),document.getString("discount1"),document.getString("discount2"),document.getString("discount3")));
                    }
                    ArrayAdapter<CustomListItemX> adapter = new CustomListViewArrayAdapterX(mContext,
                            0,
                            Chapters,);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(getContext(), "Go to MyTest Series Section", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });






    }*/
}