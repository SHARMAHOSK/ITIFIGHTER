package com.example.itifighter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.admin_section_list;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private int progressStatus = 0;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Handler handler = new Handler();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         mProgressBar = findViewById(R.id.progress_bar);
         mProgressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
         ImageView imageView = findViewById(R.id.splashImg);
         Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if( mFirebaseAuth.getCurrentUser() != null ){
                    Toast.makeText(MainActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    db.collection("users").document(mFirebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e){
                            if (e != null) { return;}
                            if (snapshot != null && snapshot.exists()) {
                                intent = new Intent(MainActivity.this, Objects.requireNonNull(snapshot.get("Role")).toString().contains("admin") ? admin_section_list.class : MainDashboard.class);
                                Toast.makeText(MainActivity.this, "welcome "+ snapshot.get("Role"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else intent = new Intent(MainActivity.this,Login.class);
            }
        };
         anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation){startloading();}
            @Override
            public void onAnimationRepeat(Animation animation){}
        });
        imageView.startAnimation(anim);
    }

    private void startloading(){
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 4;
                    handler.post(new Runnable() {
                        public void run() {
                            mProgressBar.setProgress(progressStatus);
                            if (progressStatus == 100) {
                                startActivity(intent);
                            }
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}