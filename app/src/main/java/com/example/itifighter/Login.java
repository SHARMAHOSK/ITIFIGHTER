package com.example.itifighter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.etEmail);
        password  = findViewById(R.id.etPassword);
        Button login = findViewById(R.id.btLogin);
        TextView register = findViewById(R.id.btRegister);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if( mFirebaseAuth.getCurrentUser() != null ){
                    Toast.makeText(Login.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, MainDashboard.class));
                    finish();
                }
                else{
                    Toast.makeText(Login.this,"Please Login..",Toast.LENGTH_SHORT).show();
                }
            }
        };

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
                                startActivity(new Intent(Login.this, MainDashboard.class));
                                finish();
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
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
