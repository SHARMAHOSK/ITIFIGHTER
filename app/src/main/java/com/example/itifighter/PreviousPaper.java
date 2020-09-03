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
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreviousPaper#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviousPaper extends Fragment implements IOnBackPressed {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int currentLayer = 0;   //0=subjects, 1=exams, 2=pdfS
    private int currentSubjectPos = 0, currentExamPos = 0, currentPdfPos = 0;   //records which item was clicked in previous list

    private ArrayList<CustomListItem> Subjects, Exams;
    private ListView listView;
    private ArrayList<String> PdfS, pdfFile, SubjectIds, ExamIds;

    private View ppView, progressOverlay;
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

    public void CustomBackButton(){
        switch (currentLayer){
            case 1:
                LoadSubjects(ppView);
            case 2:
                LoadExams(ppView);
        }
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
        listView = (ListView) ppView.findViewById(R.id.branch_list);
        progressOverlay = ppView.findViewById(R.id.progress_overlay);
        ((Button)ppView.findViewById(R.id.CustomBackButtonPP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBackButton();
            }
        });
        CustomizeView(ppView);
        return ppView;
    }

    void LoadSubjects(final View _ppView){
        currentLayer = 0;
        progressOverlay.setVisibility(View.VISIBLE);
        db.collection("section").document("pp").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //loadingFinished = true;
                //HIDE LOADING IT HAS FINISHED
                //spinner.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    SubjectIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /*list.add(document.getString("Name"));*/
                        SubjectIds.add(document.getId());
                        Subjects.add(new CustomListItem(document.getString(/*"Name"*/"name"),
                                        document.getString(/*"description"*/"desc"), "pp"));
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
                    progressOverlay.setVisibility(View.GONE);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            /*Toast.makeText(mContext,
                                    "Clicked ListItem: " + list.get(position), Toast.LENGTH_LONG)
                                    .show();*/
                            currentSubjectPos = position;
                            LoadExams(_ppView);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadSubjects(_ppView);
                }
            }
        });
    }

    void LoadExams(final View __ppView){
        progressOverlay.setVisibility(View.VISIBLE);
        currentLayer = 1;
        db.collection("section").document("pp").collection("branch").document(SubjectIds.get(currentSubjectPos)).collection("exam").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*examList = new ArrayList<>();*/
                    Exams = new ArrayList<>();
                    ExamIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /*examList.add(document.getString("Name"));*/
                        ExamIds.add(document.getId());
                                            Exams.add(new CustomListItem(document.getString("name"),
                                        document.getString("desc"), "pp/chapter"));
                        /*Exams.add(new CustomListItem(document.getString("Name"),
                                "is a turner for the price of mechanic and include subjects equivalent to electrician. Copa COpa COpa!!!",
                                0.00, "sample_fitter_background", 4));*/
                    }
                                        /*ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                                                R.layout.activity__branch_list_view, examList);*/

                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Exams);

                                        /*examListView = (ListView) _ppView.findViewById(R.id.branch_list);
                                        examListView.setAdapter(adapter);*/
                    listView.setAdapter(adapter);
                    progressOverlay.setVisibility(View.GONE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            /*Toast.makeText(mContext,
                                    "Clicked ListItem: " + list.get(position), Toast.LENGTH_LONG)
                                    .show();*/
                            currentExamPos = position;
                            LoadPdfS(__ppView);
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadExams(__ppView);
                }
            }
        });
    }

    void LoadPdfS(final View _ppView){
        progressOverlay.setVisibility(View.VISIBLE);
        currentLayer = 2;
        /*db.collection("branch/00"+(currentSubjectPos+1)+"/exam/00"+(currentExamPos+1)+"/pdf")*/
        db.collection("section").document("pp").collection("branch/"+SubjectIds.get(currentSubjectPos)+"/exam").document(/*"00"+(currentExamPos+1)*/ExamIds.get(currentExamPos)).collection("pdf").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    PdfS = new ArrayList<>();
                    pdfFile = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PdfS.add(document.getId());
                        pdfFile.add(""+document.getString("Name"));
                    }
                    ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                            R.layout.activity__branch_list_view, PdfS);
                    listView.setAdapter(adapter);
                    progressOverlay.setVisibility(View.GONE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {


                            Intent intent = new Intent(mContext, LoadPdf.class);
                            intent.putExtra("pdf", pdfFile.get(position));
                            startActivity(intent);

                        }

                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadPdfS(_ppView);
                }
            }
        });
    }

    private void CustomizeView(final View _ppView) {
        //TextView tv = _ppView.findViewById(R.id.ppTextView);
        LoadSubjects(_ppView);
    }

    @Override
    public boolean onBackPressed() {
        switch (currentLayer){
            case 1:
                LoadSubjects(ppView);
                return true;
            case 2:
                LoadExams(ppView);
                return true;
            default:
                return false;
        }
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


