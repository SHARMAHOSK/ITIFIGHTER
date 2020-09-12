package com.example.itifighter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

public class PreviousPaper extends Fragment {


    private int currentLayer = 0;
    private int currentSubjectPos = 0, currentExamPos = 0;  //records which item was clicked in previous list
    private ArrayList<CustomListItem> Subjects, Exams;
    private ListView listView;
    private ArrayList<String> PdfS, pdfFile, SubjectIds, ExamIds;
    private FirebaseFirestore db;
    private ProgressDialog dialog;
    private Context mContext;
    private ImageButton back;
    private ArrayAdapter adapter;

    public PreviousPaper() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }

    public void CustomBackButton(){

        Toast.makeText(getContext(), "current layer: "+currentLayer, Toast.LENGTH_LONG);

        switch (currentLayer){
            case 1: LoadSubjects();
            break;
            case 2: LoadExams();
            break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ppView = inflater.inflate(R.layout.fragment_previous_paper, container, false);
        //this.spinner = R.layout.fragment_previous_paper.findViewById(R.id.progressBar1);
        dialog = new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        listView = ppView.findViewById(R.id.branch_list);
        back = ppView.findViewById(R.id.CustomBackButtonPP);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBackButton();
            }
        });
        CustomizeView();
        return ppView;
    }


    void LoadSubjects(){
        currentLayer = 0;
        dialog.show();
        db.collection("section").document("pp").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    SubjectIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        SubjectIds.add(document.getId());
                        Subjects.add(new CustomListItem(document.getString(/*"Name"*/"name"),
                                        document.getString(/*"description"*/"desc"), "pp"));
                    }

                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                    /*listView = (ListView) _ppView.findViewById(R.id.branch_list);*/
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    back.setVisibility(View.INVISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {@Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubjectPos = position;
                            LoadExams();
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadSubjects();
                }
            }
        });
    }

    void LoadExams(){
        dialog.show();
        currentLayer = 1;
        db.collection("section").document("pp").collection("branch").document(SubjectIds.get(currentSubjectPos)).collection("exam").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*examList = new ArrayList<>();*/
                    Exams = new ArrayList<>();
                    ExamIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        /*examList.add(document.getString("Name"));*/
                        ExamIds.add(document.getId());
                                            Exams.add(new CustomListItem(document.getString("name"),
                                        document.getString("desc"), "pp/chapter"));
                    }
                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Exams);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    back.setVisibility(View.VISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentExamPos = position;
                            LoadPdfS();
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadExams();
                }
            }
        });
    }

    void LoadPdfS(){
        dialog.show();
        currentLayer = 2;
        db.collection("section").document("pp").collection("branch/"+SubjectIds.get(currentSubjectPos)+"/exam").document(/*"00"+(currentExamPos+1)*/ExamIds.get(currentExamPos)).collection("pdf").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    PdfS = new ArrayList<>();
                    pdfFile = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        PdfS.add(document.getId());
                        pdfFile.add(""+document.getString("Name"));
                    }
                    adapter = new ArrayAdapter<>(mContext,
                            R.layout.activity__branch_list_view, PdfS);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
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
                    LoadPdfS();
                }
            }
        });
    }


   private void CustomizeView() {
        LoadSubjects();
    }

}


