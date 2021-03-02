package com.example.itifighter;

import android.annotation.SuppressLint;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
    private ArrayList<CustomListItem> Subjects, Exams, PdfS_CL;
    private ArrayList<String> SubjectIds, ExamIds, PdfS, pdfFile;
    private ArrayList<Boolean> pdfPayment;
    private ListView listView;
    private FirebaseFirestore db;
    //private ProgressDialog dialog;
    private Context mContext;
    private ArrayAdapter adapter;
    public String curruntSubject = "", curruntChapter = "", currentPdf = "";
    public TextView emptyListMessage;
    private CollectionReference ref;


    public PreviousPaper() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            db = FirebaseFirestore.getInstance();
            mContext = getContext();
            instance = this;
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View ppView = inflater.inflate(R.layout.fragment_previous_paper, container, false);
            listView = ppView.findViewById(R.id.branch_list);
            emptyListMessage = ppView.findViewById(R.id.emptyListMessagepp);
            //setDialogMessage();
            CustomizeView();
            return ppView;
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return inflater.inflate(R.layout.fragment_previous_paper, container, false);
        }
    }

    public void LoadSubjects() {
        try {
            CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY, 0);
            CollectionReference reference = db.collection("section").document("pp").collection("branch");
            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Subjects = new ArrayList<>();
                        SubjectIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            SubjectIds.add(xNull(document.getId()));
                            Subjects.add(
                                    new CustomListItem(
                                            xNull(document.getString("name")),
                                            xNull(document.getString("desc")),
                                            "pp")
                            );
                        }
                        emptyListMessage.setVisibility(SubjectIds.size() <= 0 ? View.VISIBLE : View.GONE);
                        ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                        listView.setAdapter(adapter);
                        //dialog.dismiss();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                curruntSubject = xNull(SubjectIds.get(position));
                                if(bNull(curruntSubject)){
                                    CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, curruntSubject);
                                    LoadExams();
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        //   dialog.dismiss();
                        emptyListMessage.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                 //   dialog.dismiss();
                    emptyListMessage.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            //  dialog.dismiss();
            emptyListMessage.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void LoadExams() {
        try {
            // dialog.show();
            CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY, 1);
            curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
            db.collection("section").document("pp").collection("branch").document(xNull(curruntSubject))
                    .collection("exam").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Exams = new ArrayList<>();
                        ExamIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            ExamIds.add(xNull(document.getId()));
                            Exams.add(
                                    new CustomListItem(
                                            xNull(document.getString("name")),
                                            xNull(document.getString("desc")),
                                            "pp/chapter")
                            );
                        }
                        //dialog.dismiss();
                        emptyListMessage.setVisibility(ExamIds.size() <= 0 ? View.VISIBLE : View.GONE);
                        ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Exams);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                curruntChapter = xNull(ExamIds.get(position));
                                if(bNull(curruntChapter)){
                                    CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, curruntChapter);
                                    LoadPdfS();
                                }
                            }
                        });
                    } else {
                       // dialog.dismiss();
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        emptyListMessage.setVisibility(View.VISIBLE);
                        LoadSubjects();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //dialog.dismiss();
                    emptyListMessage.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            //dialog.dismiss();
            emptyListMessage.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void LoadPdfS() {
        try {
            //dialog.show();
            CustomStackManager.SetSPKeyValue(CustomStackManager.PP_STATE_KEY, 2);
            curruntChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
            String Uid = FirebaseAuth.getInstance().getUid();
            assert Uid != null;
            ref = db.collection("users").document(Uid).collection("Products")
                    .document("pp").collection("ProductId");

            db.collection("section").document("pp")
                    .collection("branch/" + xNull(curruntSubject) + "/exam")
                    .document(xNull(curruntChapter)).collection("pdf")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    try {
                        if (task.isSuccessful()) {
                            PdfS = new ArrayList<>();
                            PdfS_CL = new ArrayList<>();
                            pdfFile = new ArrayList<>();
                            pdfPayment = new ArrayList<>();
                            if (!(Objects.requireNonNull(task.getResult()).size() > 0)) {
                                emptyListMessage.setVisibility(View.VISIBLE);
                                ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, PdfS_CL);
                                listView.setAdapter(adapter);
                            } else {
                                for (final QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    final String curruntPdfSection = document.getId();
                                    ref.document(xNull(curruntPdfSection)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            pdfPayment.add(xNull(String.valueOf(documentSnapshot.get("status"))).equalsIgnoreCase("1"));
                                            PdfS.add(xNull(document.getId()));
                                            pdfFile.add(xNull(document.getString("Name")));
                                            PdfS_CL.add(
                                                    new CustomListItem(
                                                        xNull(document.getId()),
                                                        vNull(document.getString("price")),
                                                        vNull((document.getString("discount"))),
                                                        xNull(String.valueOf(documentSnapshot.get("status"))).equalsIgnoreCase("1")
                                                    )
                                            );
                                            adapter = new CustomListViewArrayAdapter(mContext, 0, PdfS_CL);
                                            listView.setAdapter(adapter);
                                            emptyListMessage.setVisibility(PdfS.size() <= 0 ? View.VISIBLE : View.GONE);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                                    //  dialog.show();
                                                    currentPdf = PdfS.get(position);
                                                    if(bNull(currentPdf)){
                                                        CustomListItem pdfData = PdfS_CL.get(position);
                                                        final String price = vNull(pdfData.getPrice()),
                                                                discount = vNull(pdfData.getDiscount()),
                                                                finalPrice = xNull(getFinalPrice(price, discount)),
                                                                PdfName = xNull(pdfFile.get(position)),
                                                                coupanCode = "shubham", coupanDiscount = "50.0";
                                                        boolean paymentStatus = pdfPayment.get(position);
                                                        if ((vNull(finalPrice) < 1) || paymentStatus) {
                                                            openPdf(PdfName);
                                                        }else {
                                                            openPaytmPaymentGateway(price, discount, PdfName, coupanCode, coupanDiscount);
                                                        }
                                                    }
                                                }

                                                private String getFinalPrice(String price, String discount) {
                                                    double price1 = Double.parseDouble(price), discount1 = Double.parseDouble(discount);
                                                    return String.valueOf((price1) - ((price1 * discount1) / 100));
                                                }
                                            });
                                        }
                                    });

                                }
                            }

                           // dialog.dismiss();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            LoadExams();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        LoadExams();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            LoadExams();
        }
    }


    private void openPdf(String pdfName) {
        try {
            //dialog.dismiss();
            Intent intent = new Intent(mContext, LoadPdf.class);
            intent.putExtra("pdf", pdfName);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            openPdf(pdfName);
        }
    }

    private void openPaytmPaymentGateway(String price, String discount, String PdfName, String coupanCode, String coupanDiscount) {
        try {
            //dialog.dismiss();
            Intent intent = new Intent(mContext, PaytmPaymentpp.class);
            intent.putExtra("price", price);
            intent.putExtra("discount", discount);
            intent.putExtra("titleName", PdfName);
            intent.putExtra("curruntPdf", currentPdf);
            intent.putExtra("currentSubject", curruntSubject);
            intent.putExtra("currentChapter", curruntChapter);
            intent.putExtra("coupanCode", coupanCode);
            intent.putExtra("coupanDiscount", coupanDiscount);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void CustomizeView() {
        try {
            int currentState = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY, 0);
            switch (currentState) {
                case 1:
                    curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
                    if (curruntSubject == null || curruntSubject.isEmpty())
                        LoadSubjects();
                    else
                        LoadExams();
                    break;
                case 2:
                    curruntSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
                    curruntChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.PP_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
                    if (curruntSubject == null || curruntSubject.isEmpty() || curruntChapter == null || curruntChapter.isEmpty())
                        LoadSubjects();
                    else
                        LoadExams();
                    break;
                default:
                    LoadSubjects();
                    break;
            }
        } catch (Exception e) {
            CustomizeView();
        }

    }

//    private void setDialogMessage() {
//        try {
//            dialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
//            dialog.setMessage("Loading...");
//            dialog.setCancelable(false);
//        } catch (Exception e) {
//            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            setDialogMessage();
//        }
//    }

    public String xNull(String str) {
        if (str != null) return str;
        else return "";
    }

    public boolean bNull(String str){
        if(str == null) return false;
        else return !str.trim().equalsIgnoreCase("");
    }

    public Double dNull(Double dbl){
        if(dbl == null) return 0.0;
        else if(dbl<1.0) return 0.0;
        else return dbl;
    }

    public Double vNull(String str){
        double num = 0.0;
        if(bNull(xNull(str))){
            try {
                num = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                num = 0.0;
            }
        }
        return num;
    }
    public String vNull(double str){
        String num = "0.0";
       if(dNull(str)>0){
        try{
            num = String.valueOf(str);
        }catch (Exception e){
            num = "0.0";
        }
       }
        return num;
    }
}