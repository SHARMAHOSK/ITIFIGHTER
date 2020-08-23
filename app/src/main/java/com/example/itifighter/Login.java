package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itifighterAdmin.admin_section_list;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.etEmail);
        password  = findViewById(R.id.etPassword);
        Button login = findViewById(R.id.btLogin);
        TextView register = findViewById(R.id.btRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, RegisterActivity.class));
                finish();
            }
        });

       login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailId = email.getText().toString();
                String pass = password.getText().toString();
                if(emailId.isEmpty()){
                    email.setError("Please enter email id ");
                    email.requestFocus();
                }
                else if(pass.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else{ mFirebaseAuth.signInWithEmailAndPassword(emailId, pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, "invalid email or password !", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                db.collection("users").document(""+mFirebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            //Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }
                                        if (snapshot != null && snapshot.exists()) {
                                            //Log.d(TAG, "Current data: " + snapshot.getData());
                                            Toast.makeText(Login.this, "welcome "+ snapshot.get("Role"), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this, snapshot.get("Role").toString().contains("admin") ? admin_section_list.class : MainDashboard.class));
                                            finish();
                                        } else {
                                            //zLog.d(TAG, "Current data: null");
                                        }
                                    }
                                });
                                //startActivity(new Intent(Login.this, MainDashboard.class));
                                //finish();
                            }
                        }
                    });
                }
            }
        });
       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(Login.this,RegisterActivity.class));
           }
       });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
}
