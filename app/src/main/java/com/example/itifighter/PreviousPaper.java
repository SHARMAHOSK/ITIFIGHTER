package com.example.itifighter;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.TextView;
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

    @SuppressLint("StaticFieldLeak")
    public static PreviousPaper instance;
    private String currentPdf = "";
    private ArrayList<CustomListItem> Subjects, Exams;
    private ListView listView;
    private ArrayList<String> PdfS, pdfFile, SubjectIds, ExamIds;
    private ArrayList<CustomListItem> PdfS_CL;
    private FirebaseFirestore db;
    private ProgressDialog dialog;
    private Context mContext;
    private ArrayAdapter adapter;
    public String curruntSubject="",curruntChapter="";
    TextView emptyListMessage;

    public PreviousPaper() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ppView = inflater.inflate(R.layout.fragment_previous_paper, container, false);
        listView = ppView.findViewById(R.id.branch_list);
        emptyListMessage = ppView.findViewById(R.id.emptyListMessagepp);
        setDialogMessage();
        CustomizeView();
        return ppView;
    }




    public void LoadSubjects(){
        /*CustomStackManager.GetInstance().SetPageState(0);*/
        CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY, 0);
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
                    emptyListMessage.setVisibility(SubjectIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {@Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            curruntSubject = SubjectIds.get(position);
                            CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, curruntSubject);
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

    public void LoadExams(){
        /*CustomStackManager.GetInstance().SetPageState(1);*/
        CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY, 1);
        dialog.show();
        curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
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
                    emptyListMessage.setVisibility(ExamIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Exams);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            curruntChapter = ExamIds.get(position);
                            CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, curruntChapter);
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
        /*CustomStackManager.GetInstance().SetPageState(2);*/
        CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY, 2);
        dialog.show();
        curruntChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
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
                    emptyListMessage.setVisibility(PdfS.size() <= 0 ? View.VISIBLE : View.GONE);
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
                                                    if(Objects.requireNonNull(status).equals("1") && Objects.equals(currentChapterPdf, curruntChapter) &&
                                                            Objects.equals(curruntSubjectPdf, curruntSubject))  openPdf(PdfName);
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

    private void CustomizeView() {
        int currentState = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY, 0);
        switch(currentState){
            case 1:
                curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY+CustomStackManager.TARGET_SUBJECT_KEY, "");
                if(curruntSubject == null || curruntSubject.isEmpty())
                    LoadSubjects();
                else
                    LoadExams();
                break;
            case 2:
                curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY+CustomStackManager.TARGET_SUBJECT_KEY, "");
                curruntChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY+CustomStackManager.TARGET_CHAPTER_KEY, "");
                if(curruntSubject == null || curruntSubject.isEmpty() || curruntChapter == null || curruntChapter.isEmpty())
                    LoadSubjects();
                else
                    LoadExams();
                break;
            default:
                LoadSubjects();
                break;
        }
    }

    private void setDialogMessage() {
        dialog = new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
    }
}