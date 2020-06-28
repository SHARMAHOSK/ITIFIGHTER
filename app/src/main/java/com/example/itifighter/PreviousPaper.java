package com.example.itifighter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreviousPaper#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviousPaper extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /*private List<String> examList;*/
    /*private List<String> list;*/
    private ArrayList<CustomListItem> Subjects, Exams;
    private ListView listView, examListView;

    private View ppView;
    private FirebaseFirestore db;

    private Context mContext;

    //boolean loadingFinished = true;

    //private ProgressBar spinner;

    public PreviousPaper() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PreviousPaper.
     */
    // TODO: Rename and change types and number of parameters
    public static PreviousPaper newInstance(String param1, String param2) {
        PreviousPaper fragment = new PreviousPaper();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
       mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ppView =  inflater.inflate(R.layout.fragment_previous_paper, container, false);
        //this.spinner = R.layout.fragment_previous_paper.findViewById(R.id.progressBar1);

        //loadingFinished = false;

        //SHOW LOADING IF IT ISNT ALREADY VISIBLE
        //this.spinner.setVisibility(View.VISIBLE);

        CustomizeView(ppView);
        return ppView;
    }

    private void CustomizeView(final View _ppView) {
        //TextView tv = _ppView.findViewById(R.id.ppTextView);
        db.collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //loadingFinished = true;
                //HIDE LOADING IT HAS FINISHED
                //spinner.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    /*list = new ArrayList<>();*/
                    Subjects = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /*list.add(document.getString("Name"));*/
                        /*Subjects.add(new CustomListItem(document.getString("Name"),
                                        document.getString("Description"),
                                        document.getDouble("Price"),
                                        document.getString("Image"),
                                        *//*getExamCount(document.getId())*//*5));*/
                        Subjects.add(new CustomListItem(document.getString("Name"),
                                        "is a turner for the price of mechanic and include subjects equivalent to electrician. Copa COpa COpa!!!",
                                        0.00, "sample_fitter_background", 5));
                    }
                    /*ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                            R.layout.activity__branch_list_view, list);*/


                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);


                    listView = (ListView) _ppView.findViewById(R.id.branch_list);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            /*Toast.makeText(mContext,
                                    "Clicked ListItem: " + list.get(position), Toast.LENGTH_LONG)
                                    .show();*/
                            db.collection("branch/00"+(position+1)+"/exam").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        /*examList = new ArrayList<>();*/
                                        Exams = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            /*examList.add(document.getString("Name"));*/
                                            /*Exams.add(new CustomListItem(document.getString("Name"),
                                        document.getString("Description"),
                                        document.getDouble("Price"),
                                        document.getString("Image"),
                                        *//*getExamCount(document.getId())*//*5));*/
                                            Exams.add(new CustomListItem(document.getString("Name"),
                                                    "is a turner for the price of mechanic and include subjects equivalent to electrician. Copa COpa COpa!!!",
                                                    0.00, "sample_fitter_background", 4));
                                        }
                                        /*ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                                                R.layout.activity__branch_list_view, examList);*/

                                        //create our new array adapter
                                        ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Exams);

                                        examListView = (ListView) _ppView.findViewById(R.id.branch_list);
                                        examListView.setAdapter(adapter);
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                        }
                    });
                    listView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /*private int getExamCount(String id) {
        String path = "branch/"+id+"/exam";
        db.collection(path).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                //cannot return from inside of inside coz inside of inside is void...
                    task.getResult().size();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }*/
}
