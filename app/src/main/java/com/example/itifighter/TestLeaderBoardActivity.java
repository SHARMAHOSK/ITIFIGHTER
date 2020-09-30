package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.itifighterAdmin.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;

public class TestLeaderBoardActivity extends AppCompatActivity {

    String tid = "";
    LinearLayout LeaderBoard;
    ArrayList<LeaderBoardQualifier> leaderBoard;
    StorageReference mStorageReference;
    CollectionReference mDatabaseReference;
    String targetSection, targetSubject, targetChapter;
    String finalTCID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_leader_board);
        tid = getIntent().getStringExtra("tid");
        LeaderBoard = findViewById(R.id.LBList);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        targetSection = getIntent().getStringExtra("section");
        targetSubject = getIntent().getStringExtra("subject");
        targetChapter = getIntent().getStringExtra("chapter");

        finalTCID = getIntent().getStringExtra("tid");

        if(targetSection.equals("lt")){
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(targetChapter).collection("tests").document(finalTCID).collection("scoreboard");
        }else{
            mDatabaseReference = FirebaseFirestore.getInstance().collection("section").document(targetSection).collection("branch").document(targetSubject).collection("chapter").document(finalTCID).collection("scoreboard");

        }
        FirebaseFirestore.getInstance().collection("section").document("lt").collection("tests").document(""+tid).collection("scoreboard").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    leaderBoard = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        leaderBoard.add(new LeaderBoardQualifier(document.getId(),
                                document.getString("name"),
                                Integer.parseInt(document.getString("Score").trim()), -1));
                    }
                    Collections.sort(leaderBoard, new Comparator<LeaderBoardQualifier>() {
                        @Override
                        public int compare(LeaderBoardQualifier o1, LeaderBoardQualifier o2) {
                            Integer s1 = o1.getScore();
                            Integer s2 = o2.getScore();
                            return s1.compareTo(s2);
                        }
                    });
                    int _rank = 1;
                    for(LeaderBoardQualifier lbq : leaderBoard){
                        lbq.setRank(_rank++);
                        fillLeaderBoard(lbq.uuid, lbq.getRank(), lbq.getName(), lbq.getScore());
                    }
                } else {
                    Toast.makeText(TestLeaderBoardActivity.this, "error getting leader board", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error getting leader board: ", task.getException());
                }
            }
        });
    }

    private void fillLeaderBoard(String uuid, int rank, String name, int score) {
        View lbRow = null;
        lbRow = View.inflate(this, R.layout.activity_leader_board_xyz, null);
        try {
            Glide.with(getApplicationContext())
                    .load(mStorageReference.child("UserImage/"+uuid))
                    .into(((ImageView)lbRow.findViewById(R.id.LeaderBoardUserImage)));
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(),"something wrong to upload",Toast.LENGTH_SHORT).show();
        }
        ((TextView)lbRow.findViewById(R.id.LeaderBoardRank)).setText(""+rank);
        ((TextView)lbRow.findViewById(R.id.LeaderBoardName)).setText(""+name);
        ((TextView)lbRow.findViewById(R.id.LeaderBoardScore)).setText(""+score);
        LeaderBoard.addView(lbRow);
    }

    public void CheckAnswerSheetFromLB(View view) {
        Intent intent = new Intent(TestLeaderBoardActivity.this, TestAnswerSheetActivity.class);
        intent.putExtra("tid", getIntent().getStringExtra("tid"));
        //also pass questions...
        startActivity(intent);
    }
}

class LeaderBoardQualifier{
    public String uuid = "";
    public String name = "";
    public int score = 0;
    public int rank = 0;

    public LeaderBoardQualifier(String uuid, String name, int score, int rank) {
        this.uuid = uuid;
        this.name = name;
        this.score = score;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}