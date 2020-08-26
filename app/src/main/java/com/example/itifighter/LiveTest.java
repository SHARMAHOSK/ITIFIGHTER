package com.example.itifighter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LiveTest extends Fragment {

    private FirebaseFirestore db;
    private Context mContext;

    private ArrayList<CustomListItem> Subjects;
    ArrayList<String> SubjectID = new ArrayList<>();
    private ListView listView;

    public LiveTest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ltView = inflater.inflate(R.layout.fragment_live_test, container, false);
        listView = ltView.findViewById(R.id.lt_branch_list);
        CustomizeView(ltView);
        return ltView;
    }

    void LoadSubjects(final View _ltView){
        db.collection("section").document("lt").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //loadingFinished = true;
                //HIDE LOADING IT HAS FINISHED
                //spinner.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        /*list.add(document.getString("Name"));*/
                        SubjectID.add(document.getId());
                        Subjects.add(new CustomListItem(document.getString("name"),
                                document.getString("desc"),
                                0.00,
                                /*document.getString("Image")*/document.getString("name"),
                                /*getExamCount(document.getId())*/5,"lt"));
                    }

                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Intent intent = new Intent(getContext(), LiveTestHomeActivity.class);
                            intent.putExtra("sid", SubjectID.get(position));
                            startActivity(intent);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadSubjects(_ltView);
                }
            }
        });
    }

    private void CustomizeView(final View _ltView) {
        LoadSubjects(_ltView);
    }
}