package com.example.itifighter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
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

import com.example.itifighter.ui.MailApi.JavaMailAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity{
    private EditText etName,etEmail,etMobile,etState,etTrade;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView login;
    private Button register;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = findViewById(R.id.RegisterProgress);
        progressBar.setVisibility(View.INVISIBLE);
        login = findViewById(R.id.btLogin);
        register = findViewById(R.id.btReg);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMob);
        etState = findViewById(R.id.etState);
        etTrade = findViewById(R.id.etTrade);
        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //final String Did  = getDeviceId();
        //verifyDevice(Did);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,Login.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String name = etName.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String mobile = etMobile.getText().toString().trim();
                final String state = etState.getText().toString().trim();
                final String trade = etTrade.getText().toString().trim();
                final String pass = getAlphaNumericString();
                final String role = "student";
                if(validate(name,email,mobile,state,trade)){
                    setAct(false);
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                setAct(true);
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
                                branch.put("Role",role);
                                //branch.put("Did",Did);
                                reference.set(branch).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Success", "DocumentSnapshot added with ID: " + UserId);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        sendMessage(name,email,pass);
                                        showPopup(email);
                                       // setRegisterDevice(Did,UserId,email);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        setAct(true);
                                        Log.e("Failure", "DocumentSnapshot added failure with ID: " + UserId);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "something wrong try again..", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void setRegisterDevice(String did, String userId,String userEmail) {
        Map<String,String> branch = new HashMap<>();
        branch.put("User",userId);
        branch.put("Date",(new Date()).toString());
        branch.put("userEmail",userEmail);
        FirebaseFirestore.getInstance().collection("Device").document(did).set(branch);
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        int permission = ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_PHONE_STATE);
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
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},123);
                    }
                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                finish();
            }
        });
        AlertDialog alert1 = alert.create();
        // alert1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
        //  alert1.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        alert1.show();
    }

    private void verifyDevice(String did) {
        setAct(false);
        Toast.makeText(RegisterActivity.this,did,Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("Device").document(did)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot != null && documentSnapshot.exists()) GoOut("Device is Already Register with "+documentSnapshot.getString("userEmail"));
                    else{
                        setAct(true);progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"You are allowed to Register.",Toast.LENGTH_SHORT).show();
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                GoOut(e.getMessage());
            }
        });
    }
    void GoOut(String userEmail){
        Intent intent = new Intent(RegisterActivity.this,Login.class);
        intent.putExtra("MsgRegVer",userEmail);
        startActivity(intent);
    }

    private void setAct(boolean b) {
        etName.setEnabled(b);etEmail.setEnabled(b);
        etMobile.setEnabled(b);etState.setEnabled(b);
        etTrade.setEnabled(b);login.setEnabled(b);
        register.setEnabled(b);
    }

    private void sendMessage(String name, String email,String pass) {
        String message = "Hello "+name+",\n\n"+
                         "Thank you!\n" +
                            "Your registration was successful!\n"+
                            "this is your Login information\n"+
                            "Email id: "+email+"\n"+
                            "Password: "+pass+"\n"+
                            "Please Login and start a long journy with us\n\n"+
                            "Thanks & Regards\nTeam ITIFighter\nContact:9936272249";
        JavaMailAPI api = new JavaMailAPI(email,"Registration Successful: ITIFighter",message);
        api.execute();
    }

    private void showPopup(String email) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Registration Successful");
        alert.setMessage("You are Registered Successfully\n" +
                        "Your password has been sent to\n Your Email id:"+email)
                .setPositiveButton("Login", new DialogInterface.OnClickListener()                 {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void logout() {
        mFirebaseAuth.signOut();
        startActivity(new Intent(this,Login.class));
    }

    public Boolean validate(String name,String email,String mobile,String state,String trade){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@"+"(?:[a-zA-Z0-9-]+\\.)+[a-z"+"A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");
        Matcher m = p.matcher(mobile);
        if(name.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            etName.setError("Please enter Name");
            etName.requestFocus();
            return false;
        }
        else if(email.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            etEmail.setError("Please enter Email");
            etEmail.requestFocus();
            return false;
        }
        else if(!pat.matcher(email).matches()){
            progressBar.setVisibility(View.INVISIBLE);
            etEmail.setError("Please enter Valid Email id");
            etEmail.requestFocus();
            return false;
        }
        else if(mobile.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            etMobile.setError("Please enter mobile");
            etMobile.requestFocus();
            return false;
        }
        else if(!(m.find() && m.group().equals(mobile))){
            progressBar.setVisibility(View.INVISIBLE);
            etMobile.setError("Please enter Valid mobile number");
            etMobile.requestFocus();
            return false;
        }
        else if(state.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            etState.setError("Please enter state");
            etState.requestFocus();
            return false;
        }
        else if(trade.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            etTrade.setError("Please enter trade");
            etTrade.requestFocus();
            return false;
        }
        else return true;
    }


    private String getAlphaNumericString(){
        String AlphaNumericString   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

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