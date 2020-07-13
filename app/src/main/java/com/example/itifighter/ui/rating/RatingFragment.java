package com.example.itifighter.ui.rating;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RatingFragment extends Fragment {

    private boolean ValX = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu_rating, container, false);
        final Button btRating = root.findViewById(R.id.btRate);
        final RatingBar rate1 = root.findViewById(R.id.rate01);
        final RatingBar rate2 = root.findViewById(R.id.rate02);
        final RatingBar rate3 = root.findViewById(R.id.rate03);
        final RatingBar rate4 = root.findViewById(R.id.rate04);
        final RatingBar rate5 = root.findViewById(R.id.rate05);
        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String UserId = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        final DocumentReference reference = db.collection("Rating").document(UserId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                    try{
                        if(!(Objects.requireNonNull(documentSnapshot.get("date")).toString().equals(DateX()))) ValX = true;
                        rate1.setRating(Float.parseFloat(Objects.requireNonNull(documentSnapshot.getString("rate01"))));
                        rate2.setRating(Float.parseFloat(Objects.requireNonNull(documentSnapshot.getString("rate02"))));
                        rate3.setRating(Float.parseFloat(Objects.requireNonNull(documentSnapshot.getString("rate03"))));
                        rate4.setRating(Float.parseFloat(Objects.requireNonNull(documentSnapshot.getString("rate04"))));
                        rate5.setRating(Float.parseFloat(Objects.requireNonNull(documentSnapshot.getString("rate05"))));
                    }catch(Exception ee){
                        Toast.makeText(getContext(),"something wrong",Toast.LENGTH_SHORT).show();
                        ValX = true;
                    }
            }
        });
        btRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValX){
                    Map<String,String> branch = new HashMap<>();
                    branch.put("rate01",String.valueOf(rate1.getRating()));
                    branch.put("rate02",String.valueOf(rate2.getRating()));
                    branch.put("rate03",String.valueOf(rate3.getRating()));
                    branch.put("rate04",String.valueOf(rate4.getRating()));
                    branch.put("rate05",String.valueOf(rate5.getRating()));
                    branch.put("date",DateX());
                    reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Success", "Rating added with ID: " + UserId);
                            Toast.makeText(getContext(), "submitted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else Toast.makeText(getContext(),"Today's Rating is already submitted",Toast.LENGTH_LONG).show();
            }
        });
        return  root;
    }

    @NonNull
    private String DateX(){
        Date d = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat f1 = new SimpleDateFormat("dd");
        @SuppressLint("SimpleDateFormat") DateFormat f2 = new SimpleDateFormat("MM");
        @SuppressLint("SimpleDateFormat") DateFormat f3 = new SimpleDateFormat("yy");
        return f1.format(d)+f2.format(d)+f3.format(d);
    }
}