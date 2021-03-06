package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.itifighter.TestSeriesX.CustomListItemX;
import com.example.itifighter.TestSeriesX.CustomListItemY;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterX;
import com.example.itifighter.TestSeriesX.CustomListViewArrayAdapterZ;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class TestSeries extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static TestSeries instance;
    private String currentSubject, currentChapter, currentTest;
    private ArrayList<CustomListItem> Subjects;
    private ArrayList<CustomListItemX> Chapters;
    private ArrayList<CustomListItemY> Tests;
    private ListView listView;
    private FirebaseFirestore db;
    private Context mContext;
    private ArrayList<String> SubjectId, ChapterId, TestId, MPQ, Timmr, Tittl;
    private TextView emptyListMessage;
    private int currentTestPos = 0, currentChapterPos = 0;

    public TestSeries() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mContext = getContext();
        instance = this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View mtView = inflater.inflate(R.layout.fragment_test_series, container, false);
        try {
            listView = mtView.findViewById(R.id.testxtRecycle);
            emptyListMessage = mtView.findViewById(R.id.emptyListMessagets);
            //LoadSubjects();
            CustomizeView();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return mtView;
    }

    private void CustomizeView() {
        int currentState = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY, 0);
        if (currentState == 1) {
            currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
            if (bNull(currentSubject)) {
                LoadSubjects();
            } else {
                LoadChapters();
            }
        } else if(currentState == 2){
            currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY+CustomStackManager.TARGET_SUBJECT_KEY, "");
            currentChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY+CustomStackManager.TARGET_CHAPTER_KEY, "");
            if(currentSubject == null || currentSubject.isEmpty() || currentChapter == null || currentChapter.isEmpty())
                LoadSubjects();
            else
                LoadTest();
        }
        else {
            LoadSubjects();
        }
    }

    void LoadSubjects() {
        try {
            CustomStackManager.SetSPKeyValue(CustomStackManager.TS_STATE_KEY, 0);
            db.collection("section").document("ts")
                    .collection("branch").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        Subjects = new ArrayList<>();
                        SubjectId = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Subjects.add(new CustomListItem(
                                    xNull(document.getString("name")),
                                    xNull(document.getString("desc")),
                                    "ts"));
                            SubjectId.add(xNull(document.getId()));
                        }
                        emptyListMessage.setVisibility(SubjectId.size() <= 0 ? View.VISIBLE : View.GONE);
                        ArrayAdapter<CustomListItem> adapter = new CustomListViewArrayAdapter(mContext, 0, Subjects);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        currentSubject = SubjectId.get(position);
                                        if (bNull(currentSubject)) {
                                            CustomStackManager.SetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, currentSubject);
                                            LoadChapters();
                                        }
                                    }
                                });

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        LoadSubjects();
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        }
    }

    void LoadChapters() {
        try {
            CustomStackManager.SetSPKeyValue(CustomStackManager.TS_STATE_KEY, 1);
            currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
            db.collection("section").document("ts")
                    .collection("branch").document(xNull(currentSubject))
                    .collection("chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Chapters = new ArrayList<>();
                        ChapterId = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Chapters.add(
                                    new CustomListItemX(
                                            xNull(document.getString("name")),
                                            oNull(document.get("cc")),
                                            "ts",
                                            xNull(document.getId()),
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
                                            xNull(document.getString("couponACTIVE"))));
                            ChapterId.add(xNull(document.getId()));
                        }
                        emptyListMessage.setVisibility(
                                ChapterId.size() <= 0 ? View.VISIBLE : View.GONE
                        );
                        ArrayAdapter<CustomListItemX> adapter = new CustomListViewArrayAdapterX(mContext, 0, Chapters, currentSubject, ChapterId);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                currentChapter = xNull(ChapterId.get(i));
                                if (bNull(currentChapter)) {
                                    CustomStackManager.SetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, currentChapter);
                                    String Uid = FirebaseAuth.getInstance().getUid();
                                    assert Uid != null;
                                    currentChapterPos = iNull(i);
                                    final String price = xNull(Chapters.get(i).getPrice1()),
                                            discount = xNull(Chapters.get(i).getDiscount1()),
                                            finalPrice = xNull(getFinalPrice(price, discount));
                                    if (vNull(finalPrice) < 1) {
                                        LoadTest();
                                    } else {
                                        FirebaseFirestore.getInstance().collection("users").document(xNull(Uid))
                                                .collection("Products").document("ts")
                                                .collection("ProductId").document(xNull(currentChapter))
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                                        String status = xNull(documentSnapshot.getString("status"));
                                                        String curruntSubjectTest = xNull(documentSnapshot.getString("currentSubject"));
                                                        String currentChapterTest = xNull(documentSnapshot.getString("currentChapter"));
                                                        String expiryDate = xNull(documentSnapshot.getString("ExpiryDate"));
                                                        if (xNull(status).equals("1") && xNull(currentChapterTest).equals(currentChapter) &&
                                                                xNull(curruntSubjectTest).equals(currentSubject) && isNotExpired(expiryDate))
                                                            LoadTest();
                                                        else paytmPaymentGateway(i);
                                                    } else paytmPaymentGateway(i);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String oNull(Object cc) {
        String value = "";
        if (cc != null) {
            try {
                value = String.valueOf(cc);
            } catch (Exception e) {
                value = "";
            }
        }
        return value;
    }

    private boolean isNotExpired(String expiryDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");
        try {
            Date d = sdf.parse(expiryDate);
            return d.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    private void paytmPaymentGateway(int i) {
        Intent intent = new Intent(getContext(), PaytmPayment.class);
        intent.putExtra("ProductId", Chapters.get(i).getId());
        intent.putExtra("month1", Chapters.get(i).getMonth1());
        intent.putExtra("month2", Chapters.get(i).getMonth2());
        intent.putExtra("month3", Chapters.get(i).getMonth3());
        intent.putExtra("price1", Chapters.get(i).getPrice1());
        intent.putExtra("price2", Chapters.get(i).getPrice2());
        intent.putExtra("price3", Chapters.get(i).getPrice3());
        intent.putExtra("discount1", Chapters.get(i).getDiscount1());
        intent.putExtra("discount2", Chapters.get(i).getDiscount2());
        intent.putExtra("discount3", Chapters.get(i).getDiscount3());
        intent.putExtra("currentSection", Chapters.get(i).getImagex());
        intent.putExtra("titleName", Chapters.get(i).getTopicHeader());
        intent.putExtra("countTest", Chapters.get(i).getTest());
        intent.putExtra("currentSubject", currentSubject);
        intent.putExtra("currentChapter", currentChapter);
        intent.putExtra("couponCODE", xNull(Chapters.get(i).getCoupanCode()));
        intent.putExtra("couponACTIVE", xNull(Chapters.get(i).getCoupanActive()));
        intent.putExtra("coupanDiscount", xNull(Chapters.get(i).getCoupanDiscount()));
        mContext.startActivity(intent);
    }

    private String getFinalPrice(String price, String discount) {
        double price1 = Double.parseDouble(price), discount1 = Double.parseDouble(discount);
        return String.valueOf((price1) - ((price1 * discount1) / 100));
    }

    private void LoadTest() {
        CustomStackManager.SetSPKeyValue(CustomStackManager.TS_STATE_KEY, 2);
        currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
        currentChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
        db.collection("section").document("ts").collection("branch").document(currentSubject).collection("chapter").document(currentChapter)
                .collection("tests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Tests = new ArrayList<>();
                    TestId = new ArrayList<>();
                    MPQ = new ArrayList<>();
                    Timmr = new ArrayList<>();
                    Tittl = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        int duration = iNull(oNull(document.get("duration"))),
                                marks = iNull(oNull(document.get("marks"))),
                                NOQ = iNull(oNull(document.get("NOQs")));
                        int score = marks * NOQ;
                        Tests.add(
                                new CustomListItemY(
                                        xNull(document.getId()),
                                        xNull(document.getString("title")),
                                        intToStringNull(duration),
                                        intToStringNull(NOQ),
                                        intToStringNull(score)));
                        TestId.add(xNull(document.getId()));
                        MPQ.add(intToStringNull(marks));
                        Timmr.add(intToStringNull(duration));
                        Tittl.add(xNull(document.getString("title")));
                    }
                    emptyListMessage.setVisibility(TestId.size() <= 0 ? View.VISIBLE : View.GONE);
                    listView.setAdapter(new CustomListViewArrayAdapterZ(mContext, 0, Tests, currentSubject, currentChapter));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            currentTest = TestId.get(i);
                            if (bNull(currentTest)) {
                                CustomStackManager.SetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_EXAM_KEY, currentTest);
                                currentTestPos = i;
                                LoadExam();
                            }
                        }
                    });
                }
            }
        });
    }

    private void LoadExam() {
        //CustomStackManager.SetSPKeyValue(CustomStackManager.TS_STATE_KEY, 3);
        currentSubject = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_SUBJECT_KEY, "");
        currentChapter = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_CHAPTER_KEY, "");
        currentTest = CustomStackManager.GetSPKeyValue(CustomStackManager.TS_STATE_KEY + CustomStackManager.TARGET_EXAM_KEY, "");

        db.collection("section").document("ts").collection("branch")
                .document(xNull(currentSubject)).collection("chapter")
                .document(xNull(currentChapter)).collection("tests")
                .document(xNull(currentTest)).collection("question")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final ArrayList<Object> questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        questions.add(
                                new Question(
                                        xNull(document.getString("question")),
                                        xNull(document.getString("option1")),
                                        xNull(document.getString("option2")),
                                        xNull(document.getString("option3")),
                                        xNull(document.getString("option4")),
                                        xNull(document.getString("answer"))));
                    }
                    final String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(xNull(uuid));
                    final CollectionReference UserTestRecord = userDoc.collection("scoreboard").document("ts").collection("test");
                    UserTestRecord.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean found = false;
                                String total_attempted = "", total_skipped = "", total_correct = "";
                                String sub_list = "";
                                String accuracy = "", tpq = "", _mpq = "";
                                String targetChapterID = currentChapter;
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    if (document.getId().equals(targetChapterID)) {
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
                                Intent myIntent;
                                if (found) {
                                    //load result activity
                                    myIntent = new Intent(getContext(), TestResultActivity.class);
                                    myIntent.putExtra("is_past_result", "true");
                                    myIntent.putExtra("total_skipped", total_skipped);
                                    myIntent.putExtra("total_attempted", total_attempted);
                                    myIntent.putExtra("total_correct", total_correct);
                                    myIntent.putExtra("section", "ts");
                                    myIntent.putExtra("subject", currentSubject);
                                    myIntent.putExtra("chapter", currentChapter);
                                    myIntent.putExtra("questions", questions);
                                    myIntent.putExtra("answer_key", sub_list);
                                    assert _mpq != null;
                                    myIntent.putExtra("_mpq", Integer.parseInt(_mpq));
                                    myIntent.putExtra("timer", Integer.parseInt(Timmr.get(currentTestPos)));
                                    myIntent.putExtra("title", Tittl.get(currentTestPos));
                                    myIntent.putExtra("accuracy", accuracy);
                                    myIntent.putExtra("tpq", tpq);
                                } else {
                                    myIntent = new Intent(getContext(), TestInstructionsActivity.class);
                                    myIntent.putExtra("section", "ts");
                                    myIntent.putExtra("subject", currentSubject);
                                    myIntent.putExtra("chapter", currentChapter);
                                    myIntent.putExtra("questions", questions);
                                    myIntent.putExtra("_mpq", Integer.parseInt(MPQ.get(currentChapterPos)));
                                    myIntent.putExtra("timer", Integer.parseInt(Timmr.get(currentChapterPos)));
                                    myIntent.putExtra("title", Tittl.get(currentChapterPos) + " (Test Series)");
                                }
                                startActivity(myIntent);
                            }
                        }
                    });


                } else {
                    Toast.makeText(mContext, "no test found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    LoadTest();
                }
            }
        });
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

    public String intToStringNull(int value) {
        String num = "0";
        if (iNull(value) > 0) {
            try {
                num = String.valueOf(value);
            } catch (NumberFormatException e) {
                num = "0";
            }
        }
        return num;
    }
}