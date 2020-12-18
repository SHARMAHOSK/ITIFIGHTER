package com.example.itifighter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class Login extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private Button login;
    private TextView register,showMessage;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.etEmail);
        password  = findViewById(R.id.etPassword);
        login = findViewById(R.id.btLogin);
        register = findViewById(R.id.btRegister);
        progressBar = findViewById(R.id.loginProgress);
        progressBar.setVisibility(View.INVISIBLE);
        showMessage = findViewById(R.id.ShowMessage);
        showMessage.setVisibility(View.INVISIBLE);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        //final String Did = getDeviceId();
        if(getIntent()!=null && getIntent().getStringExtra("MsgRegVer")!=null){
            showMessage.setText(getIntent().getStringExtra("MsgRegVer"));
            showMessage.setVisibility(View.VISIBLE);
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {startActivity(new Intent(Login.this, RegisterActivity.class));}
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage.setVisibility(View.INVISIBLE);
                String emailId = email.getText().toString();
                String pass = password.getText().toString();
                if(emailId.isEmpty()){
                    email.setError("Please enter email id");
                    email.requestFocus();
                }
                else if(pass.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    setAct(false);
                    mFirebaseAuth.signInWithEmailAndPassword(emailId, pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                showMessage.setText("invalid email or password");
                                showMessage.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                setAct(true);
                            }
                            else{
                                startActivity(new Intent(Login.this,MainDashboard.class));
                               /* db.collection("users").document(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if(snapshot != null && snapshot.exists() && snapshot.getString("Did")!=null && Objects.equals(snapshot.getString("Did"), Did)){
                                            startActivity(new Intent(Login.this,MainDashboard.class));
                                        }
                                        else{
                                            FirebaseAuth.getInstance().signOut();
                                            showMessage.setText("User not authenticated on ths device");
                                            showMessage.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            setAct(true);
                                        }
                                    }
                                }); //startActivity(new Intent(Login.this, MainDashboard.class));*/
                            }
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        int permission = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.READ_PHONE_STATE);
        if(permission  == PackageManager.PERMISSION_GRANTED)
            return ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        else{
            showGrant();
            return getDeviceId();
        }
    }

    private void showGrant() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Permission Required");
        alert.setMessage("Grant permission or exit ?\n")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener()                 {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.READ_PHONE_STATE},123);
                    }
                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                finish();
            }
        });
        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void setAct(boolean b){
        email.setEnabled(b);password.setEnabled(b);
        login.setEnabled(b);register.setEnabled(b);
    }


    @Override
    protected void onStart() {super.onStart();}

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) { finishAffinity();finish(); }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { doubleBackToExitPressedOnce=false; }
        }, 2000);
    }
}
