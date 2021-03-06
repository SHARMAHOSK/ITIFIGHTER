package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

    @SuppressLint("ClickableViewAccessibility")
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

        final TextView cbt = findViewById(R.id.ContinueBTNLBT);
        cbt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= cbt.getRight() - cbt.getTotalPaddingRight()) {
                        // your action for drawable click event
                        finish();
                        return true;
                    }
                }
                return true;
            }
        });

        mDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    leaderBoard = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        leaderBoard.add(new LeaderBoardQualifier(document.getId(),
                                document.getString("name"),
                                Integer.parseInt((document.getString("Score")).trim()), -1));
                    }
                    Collections.sort(leaderBoard, new Comparator<LeaderBoardQualifier>() {
                        @Override
                        public int compare(LeaderBoardQualifier o1, LeaderBoardQualifier o2) {
                            Integer s1 = o1.getScore();
                            Integer s2 = o2.getScore();
                            return s2.compareTo(s1);
                        }
                    });
                    int _rank = 1;
                    for(LeaderBoardQualifier lbq : leaderBoard){
                        lbq.setRank(_rank++);
                        fillLeaderBoard(lbq.uuid, lbq.getRankString(), lbq.getName(), lbq.getScore());
                    }
                } else {
                    Toast.makeText(TestLeaderBoardActivity.this, "error getting leader board", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error getting leader board: ", task.getException());
                }
            }
        });
    }

    private void fillLeaderBoard(String uuid, String rank, String name, int score) {
        View lbRow = null;
        lbRow = View.inflate(this, R.layout.activity_leader_board_xyz, null);
        try {
            Glide.with(getApplicationContext())
                    .load(mStorageReference.child("UserImage/"+uuid))
                    .placeholder(R.drawable.user)
                    .circleCrop()
                    .into(((ImageView)lbRow.findViewById(R.id.LeaderBoardUserImage)));
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(),"something wrong to upload",Toast.LENGTH_SHORT).show();
        }
        TextView _rank = lbRow.findViewById(R.id.LeaderBoardRank);
        switch(rank){
            /*case "1":
                _rank.setText("");
                _rank.setBackgroundResource(R.drawable.cup);
                break;
            case "2":
                _rank.setText("");
                _rank.setBackgroundResource(R.drawable.second);
                break;
            case "3":
                _rank.setText("");
                _rank.setBackgroundResource(R.drawable.third);
                break;*/
            default:
                _rank.setText(rank);
        }
        ((TextView)lbRow.findViewById(R.id.LeaderBoardName)).setText(""+name);
        ((TextView)lbRow.findViewById(R.id.LeaderBoardScore)).setText(""+score);
        //((TextView)lbRow.findViewById(R.id.LeaderBoardSection)).setText();
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

    public String getRankString(){
        /*if(rank < 4){
            return rank+"";
        }*/
        int cc = rank%10;
        return ""+rank+(cc == 1 ? "st" : cc == 2 ? "nd" : cc == 3 ? "rd" : "th");
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}