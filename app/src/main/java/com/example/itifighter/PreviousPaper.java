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

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class PreviousPaper extends Fragment {


    private int currentLayer = 0;
    private String currentPdf = "";
    //private int currentSubjectPos = 0, currentExamPos = 0;  //records which item was clicked in previous list
    private ArrayList<CustomListItem> Subjects, Exams;
    private ListView listView;
    private ArrayList<String> PdfS, pdfFile, SubjectIds, ExamIds;
    private ArrayList<CustomListItem> PdfS_CL;
    private FirebaseFirestore db;
    private ProgressDialog dialog;
    private Context mContext;
    private ImageButton back;
    private ArrayAdapter adapter;
    private String curruntSubject="",curruntChapter="";

    public PreviousPaper() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
    }

    public void CustomBackButton(){

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
        back.setVisibility(View.INVISIBLE);
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
                            curruntSubject = SubjectIds.get(position);
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
        db.collection("section").document("pp").collection("branch").document(curruntSubject).collection("exam").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Exams = new ArrayList<>();
                    ExamIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
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
                            curruntChapter = ExamIds.get(position);
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
        db.collection("section").document("pp").collection("branch/"+curruntSubject+"/exam").document(curruntChapter).collection("pdf").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    PdfS = new ArrayList<>();
                    PdfS_CL = new ArrayList<>();
                    pdfFile = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        PdfS.add(document.getId());
                        PdfS_CL.add(new CustomListItem(document.getId(), Double.parseDouble(Objects.requireNonNull(document.getString("price"))),
                                Double.parseDouble(Objects.requireNonNull(document.getString("discount")))));
                        pdfFile.add(""+document.getString("Name"));
                    }
                    adapter = new CustomListViewArrayAdapter(mContext, 0, PdfS_CL);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            currentPdf = PdfS.get(position); dialog.show();
                            final String price = String.valueOf(PdfS_CL.get(position).getPrice()),
                                         discount = String.valueOf(PdfS_CL.get(position).getDiscount()),
                                         finalPrice = getFinalPrice(price,discount),
                                         PdfName = String.valueOf(pdfFile.get(position));
                            if(Double.parseDouble(finalPrice)<1){ openPdf(PdfName);}
                            else{
                                String Uid = FirebaseAuth.getInstance().getUid();
                                if(Uid!=null){ try{
                                        db.collection("users").document(Uid).collection("Products")
                                                .document("pp").collection("ProductId").document(currentPdf)
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if(documentSnapshot!=null && documentSnapshot.exists()){
                                                    String status = documentSnapshot.getString("status");
                                                    String curruntSubjectPdf = documentSnapshot.getString("currentSubject");
                                                    String currentChapterPdf = documentSnapshot.getString("currentChapter");
                                                    if(status.equals("1") && currentChapterPdf.equals(curruntChapter) &&
                                                            curruntSubjectPdf.equals(curruntSubject))  openPdf(PdfName);
                                                    else openPaytmPaymentGateway(price,discount,PdfName);
                                                }else openPaytmPaymentGateway(price,discount,PdfName);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext()," something failure",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnCanceledListener(new OnCanceledListener() {
                                            @Override
                                            public void onCanceled() {
                                                Toast.makeText(getContext(),"task canceled",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }catch (Exception e){
                                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        }

                        private String getFinalPrice(String price, String discount) {
                            double price1 = Double.parseDouble(price),discount1 = Double.parseDouble(discount);
                            return  String.valueOf((price1)-((price1*discount1)/100));
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadPdfS();
                }
            }
        });
    }


    private void CustomizeView() { LoadSubjects(); }
    private void openPdf(String pdfName){
        dialog.dismiss();
        Intent intent = new Intent(mContext, LoadPdf.class);
        intent.putExtra("pdf",pdfName);
        startActivity(intent);
    }

    private void openPaytmPaymentGateway(String price,String discount,String PdfName){
        dialog.dismiss();
        Intent intent = new Intent(mContext,PaytmPaymentpp.class);
        intent.putExtra("price",price);
        intent.putExtra("discount",discount);
        intent.putExtra("titleName",PdfName);
        intent.putExtra("curruntPdf",currentPdf);
        intent.putExtra("currentSubject",curruntSubject);
        intent.putExtra("currentChapter",curruntChapter);
        startActivity(intent);
    }
}