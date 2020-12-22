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
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LiveTest extends Fragment {

    public static LiveTest instance;
    private FirebaseFirestore db;
    private Context mContext;
    private ArrayList<CustomListItem> Subjects, Chapters,Chapters2;
    ArrayList<String> SubjectIds, CHapterIds;
    private ListView listView;
    private int currentLayer = 0;
    private int currentSubjectPos = 0, currentChapterPos = 0;
   // private View progressOverlay;
    private ProgressDialog dialog;
    private ImageButton back;
    private String currentSubject,currentChapter;
    private TextView emptyListMessage;

    public LiveTest() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        mContext = getContext();
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View ltView = inflater.inflate(R.layout.fragment_live_test, container, false);
        listView = ltView.findViewById(R.id.lt_branch_list);
        emptyListMessage = ltView.findViewById(R.id.emptyListMessagetslt);
        back = ltView.findViewById(R.id.CustomBackButtonLT);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { if (currentLayer == 1) LoadSubjects(); }
        });
        back.setVisibility(View.INVISIBLE);
        CustomizeView();
        return ltView;
    }

    void LoadSubjects(){
        CustomStackManager.GetInstance().SetPageState(0);
        dialog.show();
        currentLayer = 0;
        db.collection("section").document("lt").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Subjects = new ArrayList<>();
                    SubjectIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        SubjectIds.add(document.getId());
                        Subjects.add(new CustomListItem(document.getString("name"),
                                document.getString("desc"),"lt"));
                    }
                    //create our new array adapter
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    back.setVisibility(View.INVISIBLE);
                    emptyListMessage.setVisibility(SubjectIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubjectPos = position;
                            currentSubject = SubjectIds.get(position);
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
        currentLayer = 1;
        dialog.show();
        db.collection("section").document("lt").collection("branch").document(currentSubject).collection("chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Chapters = new ArrayList<>();
                    CHapterIds = new ArrayList<>();
                    Chapters2 = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        CHapterIds.add(document.getId());
                        Chapters.add(new CustomListItem(document.getString("name"),
                                document.getString("desc"),
                                document.getString("month1"),
                                Double.parseDouble(Objects.requireNonNull(document.getString("price1"))),
                                Double.parseDouble(Objects.requireNonNull(document.getString("discount1"))), "lt/chapter"));
                        Chapters2.add(new CustomListItem(document.getString("name"),
                                document.getString("month1"),document.getString("month2"),document.getString("month3"),
                                document.getString("price1"),document.getString("price2"),document.getString("price3"),
                                document.getString("discount1"),document.getString("discount2"),document.getString("discount3"),
                                document.getString("couponCODE"),document.getString("couponACTIVE"),document.getString("NOQ")
                        ));
                    }
                    emptyListMessage.setVisibility(CHapterIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Chapters);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    back.setVisibility(View.VISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            dialog.show();
                            currentChapterPos = position;
                            currentChapter = CHapterIds.get(currentChapterPos);
                            final String price = String.valueOf(Chapters.get(position).getPrice()),
                                    discount = String.valueOf(Chapters.get(position).getDiscount()),
                                    finalPrice = getFinalPrice(price,discount);
                            if(Double.parseDouble(finalPrice)<1) openTestHomeActivity();
                            else{ String Uid = FirebaseAuth.getInstance().getUid();
                                if(Uid!=null){ try{
                                    db.collection("users").document(Uid).collection("Products")
                                            .document("lt").collection("ProductId").document(currentChapter)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if(documentSnapshot!=null && documentSnapshot.exists()){
                                                String status = documentSnapshot.getString("status");
                                                String curruntSubjectTest = documentSnapshot.getString("currentSubject");
                                                String currentChapterTest = documentSnapshot.getString("currentChapter");
                                                String expiryDate = documentSnapshot.getString("ExpiryDate");
                                                if(Objects.equals(status, "1") && Objects.equals(currentChapterTest, currentChapter) &&
                                                        Objects.equals(curruntSubjectTest, currentSubject) && isNotExpired(expiryDate))  openTestHomeActivity();
                                                else openPaytmPaymentGateway(position);
                                            }else openPaytmPaymentGateway(position);
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
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadChapters();
                }
            }
        });
    }

    private boolean isNotExpired(String expiryDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");
        try {
            Date d = sdf.parse(expiryDate);
            return Objects.requireNonNull(d).after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String getFinalPrice(String price, String discount) {
        double price1 = Double.parseDouble(price),discount1 = Double.parseDouble(discount);
        return  String.valueOf((price1)-((price1*discount1)/100));
    }

    private void openPaytmPaymentGateway(int position){
        Intent intent = new Intent(mContext,PaytmPayment.class);
        intent.putExtra("price1",String.valueOf(Chapters2.get(position).getPrice()));
        intent.putExtra("price2",String.valueOf(Chapters2.get(position).getPrice2()));
        intent.putExtra("price3",String.valueOf(Chapters2.get(position).getPrice3()));
        intent.putExtra("discount1",String.valueOf(Chapters2.get(position).getDiscount()));
        intent.putExtra("discount2",String.valueOf(Chapters2.get(position).getDiscount2()));
        intent.putExtra("discount3",String.valueOf(Chapters2.get(position).getDiscount3()));
        intent.putExtra("month1",Chapters2.get(position).getMonths());
        intent.putExtra("month2",Chapters2.get(position).getMonth2());
        intent.putExtra("month3",Chapters2.get(position).getMonth3());
        intent.putExtra("titleName",Chapters2.get(position).getTopicHeader());
        intent.putExtra("currentSubject",currentSubject);
        intent.putExtra("currentChapter",currentChapter);
        intent.putExtra("currentSection","lt");
        intent.putExtra("countTest",Chapters2.get(position).getNOQ());
        dialog.dismiss();
        startActivity(intent);
    }
    private void openTestHomeActivity() {
        Intent myIntent = new Intent(getContext(), LiveTestHomeActivity.class);
        myIntent.putExtra("subject", SubjectIds.get(currentSubjectPos));
        myIntent.putExtra("chapter", CHapterIds.get(currentChapterPos));
        dialog.dismiss();
        startActivity(myIntent);
    }

    private void CustomizeView() {
        LoadSubjects();
    }
}