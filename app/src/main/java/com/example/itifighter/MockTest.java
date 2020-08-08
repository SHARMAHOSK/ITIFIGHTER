package com.example.itifighter;

import android.content.Context;
import android.content.Intent;
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

import com.example.itifighterAdmin.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MockTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MockTest extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int currentLayer = 0;   //0=subjects, 1=exams, 2=pdfS
    private int currentSubjectPos = 0, currentChapterPos = 0;   //records which item was clicked in previous list

    private ArrayList<CustomListItem> Subjects, Chapters;
    private ArrayList<String>/* Chapters,*/ MPQs, Timers;
    private ArrayList<Question> questions;
    private ListView listView;

    private View mtView;
    private FirebaseFirestore db;

    int _mpq, timer;

    private Context mContext;

    //boolean loadingFinished = true;

    //private ProgressBar spinner;

    public MockTest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MockTest.
     */
    // TODO: Rename and change types and number of parameters
    public static MockTest newInstance(String param1, String param2) {
        MockTest fragment = new MockTest();
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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mtView = inflater.inflate(R.layout.fragment_mock_test, container, false);
        //this.spinner = R.layout.fragment_previous_paper.findViewById(R.id.progressBar1);

        //loadingFinished = false;

        //SHOW LOADING IF IT ISNT ALREADY VISIBLE
        //this.spinner.setVisibility(View.VISIBLE);
        listView = (ListView) mtView.findViewById(R.id.mt_branch_list);
        CustomizeView(mtView);
        return mtView;
    }

    void LoadSubjects(final View _mtView){
        db.collection("section").document("mt").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //loadingFinished = true;
                //HIDE LOADING IT HAS FINISHED
                //spinner.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /*list.add(document.getString("Name"));*/
                        Subjects.add(new CustomListItem(document.getString("Name"),
                                document.getString("description"),
                                0.00,
                                document.getString("Image"),
                                /*getExamCount(document.getId())*/5));
                        /*Subjects.add(new CustomListItem(document.getString("Name"),
                                "is a turner for the price of mechanic and include subjects equivalent to electrician. Copa COpa COpa!!!",
                                0.00, "cccc.png", 5));*/
                    }
                    /*ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                            R.layout.activity__branch_list_view, list);*/


                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);


                    /*listView = (ListView) _ppView.findViewById(R.id.branch_list);*/
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            /*Toast.makeText(mContext,
                                    "Clicked ListItem: " + list.get(position), Toast.LENGTH_LONG)
                                    .show();*/
                            currentSubjectPos = position;
                            //LoadExams(_mtView);
                            LoadChapters(_mtView);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadSubjects(_mtView);
                }
            }
        });
    }

    void LoadChapters(final View __mtView){
        db.collection("section").document("mt").collection("branch").document("00"+(currentSubjectPos+1)).collection("chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*examList = new ArrayList<>();*/
                    Chapters = new ArrayList<>();
                    MPQs = new ArrayList<>();
                    Timers = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /*examList.add(document.getString("Name"));*/
                                            /*Exams.add(new CustomListItem(document.getString("Name"),
                                        document.getString("Description"),
                                        document.getDouble("Price"),
                                        document.getString("Image"),
                                        *//*getExamCount(document.getId())*//*5));*/
                        Chapters.add(new CustomListItem(document.getString("Name"),
                                document.getString("description"),
                                0.00, document.getString("Image"), 4));
                        /*Chapters.add(document.getString("Name"));*/
                        MPQs.add(document.getString("MPQ"));
                        Timers.add(document.getString("Timer"));
                    }
                    /*ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_list_item_1,
                            Chapters);*/

                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Chapters);

                                        /*examListView = (ListView) _ppView.findViewById(R.id.branch_list);
                                        examListView.setAdapter(adapter);*/
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            /*Toast.makeText(mContext,
                                    "Clicked ListItem: " + list.get(position), Toast.LENGTH_LONG)
                                    .show();*/
                            currentChapterPos = position;
                            LoadTest(__mtView);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadChapters(__mtView);
                }
            }
        });
    }

    private void LoadTest(final View __mtView) {
        db.collection("section").document("mt").collection("branch").document("00"+(currentSubjectPos+1)).collection("chapter").document("00"+(currentChapterPos+1)).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*examList = new ArrayList<>();*/
                    questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /*examList.add(document.getString("Name"));*/
                                            /*Exams.add(new CustomListItem(document.getString("Name"),
                                        document.getString("Description"),
                                        document.getDouble("Price"),
                                        document.getString("Image"),
                                        *//*getExamCount(document.getId())*//*5));*/
                        /*Exams.add(new CustomListItem(document.getString("Name"),
                                "is a turner for the price of mechanic and include subjects equivalent to electrician. Copa COpa COpa!!!",
                                0.00, "sample_fitter_background", 4));*/
                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                    }

                    Intent myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                    myIntent.putExtra("questions", (Serializable) questions);
                    myIntent.putExtra("_mpq", Integer.parseInt(MPQs.get(currentChapterPos)));
                    myIntent.putExtra("timer", Integer.parseInt(Timers.get(currentChapterPos)));
                    startActivity(myIntent);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadTest(__mtView);
                }
            }
        });
    }

    private void CustomizeView(final View _mtView) {
        //TextView tv = _ppView.findViewById(R.id.ppTextView);
        LoadSubjects(_mtView);
    }
}