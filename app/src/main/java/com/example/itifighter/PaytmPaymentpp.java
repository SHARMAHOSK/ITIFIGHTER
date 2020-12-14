package com.example.itifighter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaytmPaymentpp extends AppCompatActivity {
    private final String Mid = "ahKvVJ36172797507439";
    private final String Uid = FirebaseAuth.getInstance().getUid();
    private String OrderId;
    private String Mobile;
    private String Email;
    private String txnAmount;
    private String firstName;
    private String currentSubject;
    private String currentChapter;
    private final String TAG = "PaytmPayment";
    private String currentSection;
    private ProgressDialog dialog;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_payment_pp);
        Intent intent = getIntent();
        // get Data from intent
        final String pdfPrice = intent.getStringExtra("price");
        final String Discount = intent.getStringExtra("discount");
        currentSection      = intent.getStringExtra("curruntPdf");
        currentSubject      = intent.getStringExtra("currentSubject");
        currentChapter      = intent.getStringExtra("currentChapter");
        String pdfName       = intent.getStringExtra("titleName");
        dialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        //get Instance of layout and set data in main view
        TextView title = findViewById(R.id.pdfTitle),Price = findViewById(R.id.pdfPrice);
        title.setText(pdfName);
        Price.setText("\u20B9 " + pdfPrice);
        Price.setTextColor(Color.BLACK);
        final TextView subPdfPrice = findViewById(R.id.sub_pdfPrice),subPdfDiscount = findViewById(R.id.sub_pdfDiscountPrice),finalPrice = findViewById(R.id.finalpdfPrice);
        subPdfPrice.setText("\u20B9 "+pdfPrice);
        subPdfDiscount.setText("- \u20B9 "+getDiscountedPrice(pdfPrice,Discount));
        txnAmount = getFinalPrice(pdfPrice,Discount);
        finalPrice.setText(txnAmount);
        Button cancel = findViewById(R.id.cancelx), payx = findViewById(R.id.payButtonpp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AreYouSureCancel();
            }
        });
        payx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if (txnAmount.equals("")) {
                    dialog.dismiss();
                    Toast.makeText(PaytmPaymentpp.this,"Amount is mandatory",Toast.LENGTH_SHORT).show();
                }
                else getToken();
            }
        });
    }

    private String getDiscountedPrice(String pdfPrice, String discount) {
        return String.valueOf((Double.parseDouble(pdfPrice)*Double.parseDouble(discount))/100);
    }

    private String getFinalPrice(String pdfPrice, String discount) {
        double price1 = Double.parseDouble(pdfPrice),discount1 = Double.parseDouble(getDiscountedPrice(pdfPrice,discount));
        return  String.valueOf(price1-discount1);
    }

    private void getToken() {
        ServiceWrapper serviceWrapper = new ServiceWrapper();
        Call<Token_Res> call = serviceWrapper.getTokenCall("12345", Mid, OrderId, txnAmount, Uid, Email, Mobile, firstName);
        call.enqueue(new Callback<Token_Res>() {
            @Override
            public void onResponse(@NonNull Call<Token_Res> call, @NonNull Response<Token_Res> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        if (!response.body().getBody().getTxnToken().equals("")) {
                            startPaytmPayment(response.body().getBody().getTxnToken());
                        } else {
                            dialog.dismiss();
                            Log.e(TAG, "token status false");
                            Toast.makeText(PaytmPaymentpp.this,"token status false",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(PaytmPaymentpp.this,"Server Response failed",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Log.e(TAG, "Error in token Response" + e.toString());
                    Toast.makeText(PaytmPaymentpp.this,"Error in token Response" + e.toString(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Token_Res> call, @NonNull Throwable t) {
                dialog.dismiss();
                Log.e(TAG, " response error " + t.toString());
            }
        });
    }

    private void startPaytmPayment(String txnToken) {
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it //  String host = "https://securegw.paytm.in/";
        String callBackUrl = host+"theia/paytmCallback?ORDER_ID=" + OrderId;
        PaytmOrder paytmOrder = new PaytmOrder(OrderId, Mid, txnToken, txnAmount, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {

            @Override
            public void onTransactionResponse(Bundle bundle) {
                setTransactionDetails(bundle);
            }

            @Override
            public void networkNotAvailable() {
                dialog.dismiss();
                Log.e(TAG, "network not available ");
                Toast.makeText(PaytmPaymentpp.this,"network not available",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                dialog.dismiss();
                Log.e(TAG, "Clientauth " + s);
                Toast.makeText(PaytmPaymentpp.this,"Clientauth " + s,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void someUIErrorOccurred(String s) {
                dialog.dismiss();
                Log.e(TAG, " UI error " + s);
                Toast.makeText(PaytmPaymentpp.this," UI error " + s,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                dialog.dismiss();
                Log.e(TAG, " error loading web " + s + "--" + s1);
                Toast.makeText(PaytmPaymentpp.this," error loading web " + s + "--" + s1,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                dialog.dismiss();
                Log.e(TAG, "backPress ");
                Toast.makeText(PaytmPaymentpp.this,"backPress ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                dialog.dismiss();
                Log.e(TAG, " transaction cancel " + s);
                Toast.makeText(PaytmPaymentpp.this," transaction cancel " + s,Toast.LENGTH_SHORT).show();
            }

        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, 0);
    }
    private void getDetails() {
        OrderId = Genrate();
        FirebaseFirestore.getInstance().collection("users").document(Uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        Email = value.getString("Email");
                        Mobile = value.getString("Mobile");
                        firstName = value.getString("Name");
                    }
                });
        dialog.dismiss();
    }
    private void AreYouSureCancel() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure to Cancel this Payment ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("No", null);
        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void setTransactionDetails(final Bundle bundle) {
        try {
            dialog.dismiss();
            DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid)
                    .collection("transaction").document("pp").collection("TXNID")
                    .document(Objects.requireNonNull(bundle.getString("TXNID")));
            Map<String,String> map = new HashMap<>();
            Set<String> d = bundle.keySet();
            for (String key : d) map.put(key, bundle.getString(key));
            map.put("currentSubject",currentSubject);
            map.put("currentChapter",currentChapter);
            map.put("curruntPdf",currentSection);
            reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(Objects.equals(bundle.getString("STATUS"), "TXN_SUCCESS")) setSuccessProductDetails();
                    else{ Toast.makeText(PaytmPaymentpp.this,"Transaction failed",Toast.LENGTH_SHORT).show(); }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaytmPaymentpp.this,"Transaction insertion failure",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(PaytmPaymentpp.this,"Exception in data saving",Toast.LENGTH_SHORT).show();
        }
    }
    private void setSuccessProductDetails() {
        try {
            final DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid)
                    .collection("Products").document("pp").collection("ProductId").document(currentSection);
            final Map<String, String> map = new HashMap<>();
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    map.put("currentSubject",currentSubject);
                    map.put("currentChapter",currentChapter);
                    map.put("status","1");
                    reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PaytmPaymentpp.this,"Transaction successfully done",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PaytmPaymentpp.this,"contact customer support",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaytmPaymentpp.this,"Failure product transaction",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(PaytmPaymentpp.this,"Exception in setSuccessProductDetails",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int activityRequestCode = 0;
        if (requestCode == activityRequestCode) {
            Bundle bundle = data.getExtras();
            assert bundle != null;
            for (String key : bundle.keySet()) {
                Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
            Log.e(TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, " payment failed");
        }
    }

    public String Genrate() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("ddMMyyhhmmss");
        String date = df.format(c.getTime());
        Random rand = new Random();
        int min = 1000, max = 9999;
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return date + randomNum;
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();
        getDetails();
    }
    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        AreYouSureCancel();
    }
}