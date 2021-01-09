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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;


public class MockTest extends Fragment {

    public static MockTest instance;
    private int currentLayer = 0;
    private int currentSubjectPos = 0, currentChapterPos = 0;   //records which item was clicked in previous list
    private ProgressDialog dialog;
    private ArrayList<CustomListItem> Subjects, Chapters;
    private ArrayList<String> MPQs, Timers, Titles, SubjectIds, CHapterIds;
    private ArrayList<Question> questions;
    private ListView listView;
    private ImageButton back;
    private TextView emptyListMessage;

    //private View progressOverlay;
    private FirebaseFirestore db;
    private Context mContext;

    public MockTest() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mtView = inflater.inflate(R.layout.fragment_mock_test, container, false);
        listView = mtView.findViewById(R.id.mt_branch_list);
        dialog = new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        emptyListMessage = mtView.findViewById(R.id.emptyListMessagetsmt);
        back = mtView.findViewById(R.id.CustomBackButtonMT);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBackButton();
            }
        });
        CustomizeView();
        return mtView;
    }

    public void CustomBackButton(){
        if (currentLayer == 1) {
            LoadSubjects();
        }
    }

    void LoadSubjects(){
        CustomStackManager.GetInstance().SetPageState(0);
        currentLayer = 0;
        dialog.show();
        db.collection("section").document("mt").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    SubjectIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        /*list.add(document.getString("Name"));*/
                        SubjectIds.add(document.getId());
                        Subjects.add(new CustomListItem(document.getString("name"),
                                document.getString("desc"),"mt"));
                    }
                    emptyListMessage.setVisibility(SubjectIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);

                    /*listView = (ListView) _ppView.findViewById(R.id.branch_list);*/
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    back.setVisibility(View.INVISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubjectPos = position;
                            LoadChapters();
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadSubjects();
                }
            }
        });
    }

    void LoadChapters(){
        CustomStackManager.GetInstance().SetPageState(1);
        dialog.show();
        currentLayer = 1;
        db.collection("section").document("mt").collection("branch").document(SubjectIds.get(currentSubjectPos)).collection("chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*examList = new ArrayList<>();*/
                    Chapters = new ArrayList<>();
                    CHapterIds = new ArrayList<>();
                    MPQs = new ArrayList<>();
                    Timers = new ArrayList<>();
                    Titles = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        CHapterIds.add(document.getId());
                        Chapters.add(new CustomListItem(CHapterIds.size(), document.getString("name"),
                                document.getString("desc"),
                                Double.parseDouble(Objects.requireNonNull(document.getString("price"))),
                                Double.parseDouble(Objects.requireNonNull(document.getString("discount"))),
                                Integer.parseInt((Objects.requireNonNull(document.getString("NOQ")))), Integer.parseInt(Objects.requireNonNull(document.getString("Timer"))),
                                Integer.parseInt(Objects.requireNonNull(document.getString("MPQ"))),"mt/chapter"));
                        /*Chapters.add(document.getString("Name"));*/

                        MPQs.add(document.getString("MPQ"));
                        Timers.add(document.getString("Timer"));
                        Titles.add(document.getString("name"));
                    }
                    emptyListMessage.setVisibility(CHapterIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Chapters);

                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    back.setVisibility(View.VISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentChapterPos = position;
                            LoadTest();
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadChapters();
                }
            }
        });
    }

    private void LoadTest() {

        db.collection("section").document("mt").collection("branch").document(SubjectIds.get(currentSubjectPos)).collection("chapter").document(CHapterIds.get(currentChapterPos)).collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        questions.add(new Question(document.getString("question"), document.getString("option1"),
                                document.getString("option2"), document.getString("option3"),
                                document.getString("option4"), document.getString("answer")));
                    }

                    if(questions.isEmpty()){
                        Toast.makeText(mContext, "selected chapter contains 0 questions..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(""+uuid);
                    final CollectionReference UserTestRecord = userDoc.collection("scoreboard").document("mt").collection("test");
                    UserTestRecord.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                boolean found = false;
                                String total_attempted="", total_skipped="", total_correct="";
                                String sub_list = "";
                                String accuracy = "", tpq = "", _mpq = "";
                                String targetChapterID = CHapterIds.get(currentChapterPos);
                                for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                                    if(document.getId().equals(targetChapterID)){
                                        found = true;
                                        total_attempted = document.getString("total_attempted");
                                        total_skipped = document.getString("total_skipped");
                                        total_correct = document.getString("total_correct");
                                        accuracy = document.getString("accuracy");
                                        tpq = document.getString("tpq");
                                        _mpq = document.getString("_mpq");
                                        sub_list = document.getString("answer_key");
                                        break;
                                    }
                                }
                                if(found){
                                    //load result activity
                                    Intent myIntent = new Intent(getContext(), TestResultActivity.class);
                                    myIntent.putExtra("is_past_result", "true");
                                    myIntent.putExtra("total_skipped", total_skipped);
                                    myIntent.putExtra("total_attempted", total_attempted);
                                    myIntent.putExtra("total_correct", total_correct);
                                    myIntent.putExtra("section", "mt");
                                    myIntent.putExtra("subject", SubjectIds.get(currentSubjectPos));
                                    myIntent.putExtra("chapter", CHapterIds.get(currentChapterPos));
                                    myIntent.putExtra("questions", questions);
                                    myIntent.putExtra("answer_key", sub_list);
                                    assert _mpq != null;
                                    myIntent.putExtra("_mpq", Integer.parseInt(_mpq));
                                    myIntent.putExtra("timer", Integer.parseInt(Timers.get(currentChapterPos)));
                                    myIntent.putExtra("title", Titles.get(currentChapterPos));
                                    myIntent.putExtra("accuracy", accuracy);
                                    myIntent.putExtra("tpq", tpq);
                                    startActivity(myIntent);
                                }else{
                                    Intent myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                    myIntent.putExtra("section", "mt");
                                    myIntent.putExtra("subject", SubjectIds.get(currentSubjectPos));
                                    myIntent.putExtra("chapter", CHapterIds.get(currentChapterPos));
                                    myIntent.putExtra("questions", questions);
                                    myIntent.putExtra("_mpq", Integer.parseInt(MPQs.get(currentChapterPos)));
                                    myIntent.putExtra("timer", Integer.parseInt(Timers.get(currentChapterPos)));
                                    myIntent.putExtra("title", Titles.get(currentChapterPos) + " (Mock Test)");
                                    startActivity(myIntent);
                                }
                            }else{
                                Intent myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                myIntent.putExtra("section", "mt");
                                myIntent.putExtra("subject", SubjectIds.get(currentSubjectPos));
                                myIntent.putExtra("chapter", CHapterIds.get(currentChapterPos));
                                myIntent.putExtra("questions", questions);
                                myIntent.putExtra("_mpq", Integer.parseInt(MPQs.get(currentChapterPos)));
                                myIntent.putExtra("timer", Integer.parseInt(Timers.get(currentChapterPos)));
                                myIntent.putExtra("title", Titles.get(currentChapterPos));
                                startActivity(myIntent);
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadTest();
                }
            }
        });
    }

    private void CustomizeView() {
        LoadSubjects();
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadSubjects();
    }
}