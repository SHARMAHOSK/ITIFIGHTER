package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveTest extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mtView;
    TextView countdown;
    DocumentReference documentReference;

    public LiveTest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveTest.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveTest newInstance(String param1, String param2) {
        LiveTest fragment = new LiveTest();
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

        documentReference = FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests")
                                .document("001");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mtView = inflater.inflate(R.layout.fragment_live_test, container, false);
        CustomizeView(mtView);
        return mtView;
    }

    private void CustomizeView(View mtView) {

        countdown = mtView.findViewById(R.id.countdown);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc != null){
                        new CountDownTimer((
                                doc.getLong("sTime") - Calendar.getInstance().getTimeInMillis())* 60 * 1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                long secs = millisUntilFinished / 1000;
                                long min = secs / 60;
                                secs %= 60;
                                countdown.setText("" + (min > 9 ? min : "0"+min) + ":" + (secs > 9 ? secs : "0"+secs));
                            }

                            public void onFinish() {
                                countdown.setText("done!");
                            }
                        }.start();
                    }
                }
            }
        });
    }
}