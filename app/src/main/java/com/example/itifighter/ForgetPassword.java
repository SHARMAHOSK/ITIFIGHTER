package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        final EditText email = findViewById(R.id.forget_email);
        final TextView emailSuccess = findViewById(R.id.emailSuccess);
        Button forget = findViewById(R.id.forget);
        Button login = findViewById(R.id.login);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailSuccess.setText("");
                final String validEmail = email.getText().toString();
                if(validEmail.trim().isEmpty()){
                    email.setError("Enter Email id");
                }
                else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(validEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        emailSuccess.setText("Your Password has been sent to your Email id");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            emailSuccess.setText(e.getMessage());
                        }
                    });
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailSuccess.setText("");
                startActivity(new Intent(ForgetPassword.this,Login.class));
            }
        });
    }
}