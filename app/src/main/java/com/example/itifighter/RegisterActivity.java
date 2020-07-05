package com.example.itifighter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity{
    private EditText etName,etEmail,etMobile,etState,etTrade,etPassword;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView login = findViewById(R.id.btLogin);
        Button register = findViewById(R.id.btReg);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMob);
        etState = findViewById(R.id.etState);
        etTrade = findViewById(R.id.etTrade);
        etPassword = findViewById(R.id.etPassword);
        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,Login.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = etName.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String mobile = etMobile.getText().toString().trim();
                final String state = etState.getText().toString().trim();
                final String trade = etTrade.getText().toString().trim();
                final String pass = etPassword.getText().toString();
                if(validate(name,email,mobile,state,trade,pass)){
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this,"Something error ! try again",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                final String UserId = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
                                Map<String,String> branch = new HashMap<>();
                                DocumentReference reference = db.collection("users").document(UserId);
                                branch.put("Name", name);
                                branch.put("Email",email);
                                branch.put("Mobile",mobile);
                                branch.put("State",state);
                                branch.put("Trade",trade);
                                reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Success", "DocumentSnapshot added with ID: " + UserId);
                                        startActivity(new Intent(RegisterActivity.this, MainDashboard.class));
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });


    }

    public Boolean validate(String name,String email,String mobile,String state,String trade, String pass){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@"+"(?:[a-zA-Z0-9-]+\\.)+[a-z"+"A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");
        Matcher m = p.matcher(mobile);
        if(name.isEmpty()){
            etName.setError("Please enter Name");
            etName.requestFocus();
            return false;
        }
        else if(email.isEmpty()){
            etEmail.setError("Please enter Email");
            etEmail.requestFocus();
            return false;
        }
        else if(!pat.matcher(email).matches()){
            etEmail.setError("Please enter Valid Email id");
            etEmail.requestFocus();
            return false;
        }
        else if(mobile.isEmpty()){
            etMobile.setError("Please enter mobile");
            etMobile.requestFocus();
            return false;
        }
        else if(!(m.find() && m.group().equals(mobile))){
            etMobile.setError("Please enter Valid mobile number");
            etMobile.requestFocus();
            return false;
        }
        else if(state.isEmpty()){
            etState.setError("Please enter state");
            etState.requestFocus();
            return false;
        }
        else if(trade.isEmpty()){
            etTrade.setError("Please enter trade");
            etTrade.requestFocus();
            return false;
        }
        else if(pass.isEmpty()){
            etPassword.setError("Please enter Password");
            etPassword.requestFocus();
            return false;
        }
        else if(pass.length() < 6){
            etPassword.setError("Password length must be greater or equal to 6");
            etPassword.requestFocus();
            return false;
        }
        else return true;
    }
}