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

    @SuppressLint("StaticFieldLeak")
    public static LiveTest instance;
    private FirebaseFirestore db;
    private Context mContext;
    private ArrayList<CustomListItem> Subjects, Chapters, Chapters2;
    ArrayList<String> SubjectIds, CHapterIds;
    private ListView listView;
    private int currentSubjectPos = 0;
    private int currentChapterPos = 0;
    private String currentSubject, currentChapter;
    private TextView emptyListMessage;

    public LiveTest() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            db = FirebaseFirestore.getInstance();
            mContext = getContext();
            instance = this;
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View ltView = inflater.inflate(R.layout.fragment_live_test, container, false);
        listView = ltView.findViewById(R.id.lt_branch_list);
        emptyListMessage = ltView.findViewById(R.id.emptyListMessagetslt);
        CustomizeView();
        return ltView;
    }

    void LoadSubjects() {
        CustomStackManager.SetSPKeyValue(CustomStackManager.LT_STATE_KEY, 0);
        db.collection("section").document("lt").collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                        "lt")
                        );
                    }
                    //create our new array adapter
                    emptyListMessage.setVisibility(SubjectIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            currentSubjectPos = iNull(position);
                            currentSubject = xNull(SubjectIds.get(position));
                            if (bNull(currentSubject)) {
                                CustomStackManager.SetSPKeyValue(CustomStackManager.LT_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, currentSubject);
                                LoadChapters();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void LoadChapters() {
        /*CustomStackManager.GetInstance().SetPageState(1);*/
        CustomStackManager.SetSPKeyValue(CustomStackManager.LT_STATE_KEY, 1);
        currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.LT_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
        db.collection("section").document("lt").collection("branch")
                .document(xNull(currentSubject)).collection("chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Chapters = new ArrayList<>();
                    CHapterIds = new ArrayList<>();
                    Chapters2 = new ArrayList<>();
                    for (final QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                        CHapterIds.add(xNull(document.getId()));
                        Chapters.add(
                                new CustomListItem(
                                        iNull(CHapterIds.size()),
                                        iNull(document.getString("testCount")),
                                        xNull(document.getString("name")),
                                        xNull(document.getString("desc")),
                                        xNull(document.getString("month1")),
                                        vNull(document.getString("price1")),
                                        vNull(document.getString("discount1")),
                                        "lt/chapter"
                                )
                        );
                        Chapters2.add(
                                new CustomListItem(
                                        xNull(document.getString("name")),
                                        xNull(document.getString("month1")),
                                        xNull(document.getString("month2")),
                                        xNull(document.getString("month3")),
                                        xNull(document.getString("price1")),
                                        xNull(document.getString("price2")),
                                        xNull(document.getString("price3")),
                                        xNull(document.getString("discount1")),
                                        xNull(document.getString("discount2")),
                                        xNull(document.getString("discount3")),
                                        xNull(document.getString("couponCODE")),
                                        xNull(document.getString("coupanDiscount")),
                                        xNull(document.getString("couponACTIVE")),
                                        xNull(document.getString("NOQ"))
                                ));
                    }
                    emptyListMessage.setVisibility(CHapterIds.size() <= 0 ? View.VISIBLE : View.GONE);
                    ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Chapters);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            currentChapterPos = iNull(position);
                            currentChapter = xNull(CHapterIds.get(currentChapterPos));
                            if (bNull(currentChapter)) {
                                CustomStackManager.SetSPKeyValue(CustomStackManager.LT_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, currentChapter);

                                final String price = vNull(Chapters.get(position).getPrice()),
                                        discount = vNull(Chapters.get(position).getDiscount()),
                                        finalPrice = xNull(getFinalPrice(price, discount));

                                if (vNull(finalPrice) < 1) openTestHomeActivity();
                                else {
                                    String Uid = FirebaseAuth.getInstance().getUid();
                                    if (Uid != null) {
                                        try {
                                            db.collection("users").document(Uid).collection("Products")
                                                    .document("lt").collection("ProductId")
                                                    .document(xNull(currentChapter))
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                                        String status = xNull(documentSnapshot.getString("status"));
                                                        String curruntSubjectTest = xNull(documentSnapshot.getString("currentSubject"));
                                                        String currentChapterTest = xNull(documentSnapshot.getString("currentChapter"));
                                                        String expiryDate = xNull(documentSnapshot.getString("ExpiryDate"));
                                                        if (Objects.equals(status, "1") && Objects.equals(currentChapterTest, currentChapter) &&
                                                                Objects.equals(curruntSubjectTest, currentSubject) && isNotExpired(expiryDate))
                                                            openTestHomeActivity();
                                                        else openPaytmPaymentGateway(position);
                                                    } else openPaytmPaymentGateway(position);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), " something failure", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnCanceledListener(new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    Toast.makeText(getContext(), "task canceled", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
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
        double price1 = Double.parseDouble(price), discount1 = Double.parseDouble(discount);
        return String.valueOf((price1) - ((price1 * discount1) / 100));
    }

    private void openPaytmPaymentGateway(int position) {
        Intent intent = new Intent(mContext, PaytmPayment.class);
        intent.putExtra("price1", vNull(Chapters2.get(position).getPrice()));
        intent.putExtra("price2", vNull(Chapters2.get(position).getPrice2()));
        intent.putExtra("price3", vNull(Chapters2.get(position).getPrice3()));
        intent.putExtra("discount1", vNull(Chapters2.get(position).getDiscount()));
        intent.putExtra("discount2", vNull(Chapters2.get(position).getDiscount2()));
        intent.putExtra("discount3", vNull(Chapters2.get(position).getDiscount3()));
        intent.putExtra("month1", xNull(Chapters2.get(position).getMonths()));
        intent.putExtra("month2", xNull(Chapters2.get(position).getMonth2()));
        intent.putExtra("month3", xNull(Chapters2.get(position).getMonth3()));
        intent.putExtra("titleName", xNull(Chapters2.get(position).getTopicHeader()));
        intent.putExtra("couponCODE", xNull(Chapters2.get(position).getCoupanCode()));
        intent.putExtra("couponACTIVE", xNull(Chapters2.get(position).getCoupanActive()));
        intent.putExtra("coupanDiscount", xNull(Chapters2.get(position).getCoupanDiscount()));
        intent.putExtra("currentSubject", xNull(currentSubject));
        intent.putExtra("currentChapter", xNull(currentChapter));
        intent.putExtra("currentSection", "lt");
        intent.putExtra("countTest", iNull(Chapters2.get(position).getNOQ()));
        startActivity(intent);
    }

    private void openTestHomeActivity() {
        Intent myIntent = new Intent(getContext(), LiveTestHomeActivity.class);
        myIntent.putExtra("subject", SubjectIds.get(currentSubjectPos));
        myIntent.putExtra("chapter", CHapterIds.get(currentChapterPos));
        startActivity(myIntent);
    }

    private void CustomizeView() {
        int currentState = CustomStackManager.GetSPKeyValue(CustomStackManager.LT_STATE_KEY, 0);
        if (currentState == 1) {
            currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.LT_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
            if (currentSubject == null || currentSubject.isEmpty())
                LoadSubjects();
            else
                LoadChapters();
        } else {
            LoadSubjects();
        }
    }

    public String xNull(String str) {
        if (str != null) return str;
        else return "";
    }

    public boolean bNull(String str) {
        if (str == null) return false;
        else return !str.trim().isEmpty();
    }

    public Double dNull(Double dbl) {
        if (dbl == null) return 0.0;
        else if (dbl < 1.0) return 0.0;
        else return dbl;
    }

    public Double vNull(String str) {
        double num = 0.0;
        if (bNull(xNull(str))) {
            try {
                num = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                num = 0.0;
            }
        }
        return num;
    }

    public String vNull(double str) {
        String num = "0.0";
        if (dNull(str) > 0) {
            try {
                num = String.valueOf(str);
            } catch (Exception e) {
                num = "0.0";
            }
        }
        return num;
    }

    public int iNull(int value) {
        if (value < 1) return 0;
        else return value;
    }

    public int iNull(String value) {
        int num = 0;
        if (bNull(xNull(value))) {
            try {
                num = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                num = 0;
            }
        }
        return iNull(num);
    }

    public String aNull(int value) {
        String num = "0";
        if (iNull(value) > 0) {
            try {
                num = String.valueOf(value);
            } catch (Exception e) {
                num = "0";
            }
        }
        return num;
    }


    public long lNull(long value) {
        if (value < 1) return 0;
        else return value;
    }

    public String intToStringNull(int value) {
        String testCount = "0";
        if(value>0){
            try{
                testCount = String.valueOf(value);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return testCount;
    }

}