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
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Date;
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
    private String coupanCode;
    private String coupanDiscount, pdfName, pdfPrice, Discount;
    private boolean coupanActive = false;
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
        pdfPrice = intent.getStringExtra("price");
        Discount = intent.getStringExtra("discount");
        currentSection = intent.getStringExtra("curruntPdf");
        currentSubject = intent.getStringExtra("currentSubject");
        currentChapter = intent.getStringExtra("currentChapter");
        pdfName = intent.getStringExtra("titleName");
        coupanCode = intent.getStringExtra("coupanCode");
        coupanDiscount = intent.getStringExtra("coupanDiscount");

        // set dialog message
        dialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        //get Instance of layout and set data in main view
        final TextView title = findViewById(R.id.pdfTitle), Price = findViewById(R.id.pdfPrice),
                subPdfPrice = findViewById(R.id.sub_itempricex), subPdfDiscount = findViewById(R.id.sub_pdfDiscountPrice),
                finalPrice = findViewById(R.id.finalpdfPrice), coupanDiscountPrice = findViewById(R.id.coupnCodeDiscount),
                removeCoupanButton = findViewById(R.id.removeCoupanButton), sub_discountMsg = findViewById(R.id.sub_discountx),
                coupanCodeMsg = findViewById(R.id.coupanCodeMsg);

        final LinearLayout CoupanMsgLayout = findViewById(R.id.coupanMsg);

        final EditText text = findViewById(R.id.textCoupanCode);
        Button couponButton = findViewById(R.id.coupanClick);
        setPdfDetails(title, Price);
        setTransactionDetails(subPdfPrice, subPdfDiscount, finalPrice, coupanDiscountPrice, CoupanMsgLayout, sub_discountMsg, coupanCodeMsg);
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
                if (xNull(txnAmount).equals("")) {
                    dialog.dismiss();
                    Toast.makeText(PaytmPaymentpp.this, "Amount is mandatory", Toast.LENGTH_SHORT).show();
                } else if (iNull(txnAmount).trim().equalsIgnoreCase("0.0")) {
                    setAutoPayment();
                } else getToken();
            }
        });


        removeCoupanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                setTransactionDetails(subPdfPrice, subPdfDiscount, finalPrice, coupanDiscountPrice, CoupanMsgLayout, sub_discountMsg, coupanCodeMsg);
                dialog.dismiss();
            }
        });

        couponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!coupanActive) {
                    dialog.show();
                    String textCoupan = text.getText().toString().trim();
                    if (!textCoupan.equalsIgnoreCase("")) {
                        if (textCoupan.equalsIgnoreCase(coupanCode)) {
                            if (Double.parseDouble(coupanDiscount) > 0) {
                                setApplyCoupanDetails(subPdfPrice, subPdfDiscount, finalPrice, coupanDiscountPrice, CoupanMsgLayout, sub_discountMsg, coupanCodeMsg);
                            } else {
                                dialog.dismiss();
                                text.setError("Coupan not valid !");
                            }
                        } else {
                            dialog.dismiss();
                            text.setError("Coupan not valid !");
                        }
                    } else {
                        dialog.dismiss();
                        text.setError("please enter coupan code !");
                    }
                } else {
                    dialog.dismiss();
                    text.setError("Coupan already used ");
                }
                text.setText("");
            }
        });
    }

    private void setAutoPayment() {
        try {
            Bundle bundle;
            bundle = setAutoBundle();
            setTransactionDetails(bundle);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PaytmPaymentpp.this, "Exception in Auto data saving", Toast.LENGTH_SHORT).show();
        }
    }

    private Bundle setAutoBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("BANKNAME", "FREE");
        bundle.putString("BANKTXNID", "FREE" + coupanCode);
        bundle.putString("CHECKSUMHASH", "FREE" + coupanCode);
        bundle.putString("CURRENCY", "INR");
        bundle.putString("GATEWAYNAME", "FREE");
        bundle.putString("MID", xNull(Mid));
        bundle.putString("ORDERID", xNull(OrderId));
        bundle.putString("PAYMENTMODE", "FREE");
        bundle.putString("RESPCODE", "01");
        bundle.putString("RESPMSG", "Txn Success");
        bundle.putString("STATUS", "TXN_SUCCESS");
        bundle.putString("TXNAMOUNT", xNull(txnAmount));
        Date d = new Date();
        bundle.putString("TXNDATE", d.toString());
        bundle.putString("TXNID", "FREE" + xNull(OrderId) + xNull(Uid));
        return bundle;
    }

    private String iNull(String value) {
        if (value == null) return "0.0";
        else if(value.trim().equalsIgnoreCase("0")) return "0.0";
        else return value;
    }

    private String xNull(String value) {
        if (value == null) return "";
        else return value;
    }

    @SuppressLint("SetTextI18n")
    private void setApplyCoupanDetails(TextView subPdfPrice, TextView subPdfDiscount, TextView finalPrice, TextView coupanDiscountPrice, LinearLayout coupanMsgLayout, TextView sub_discountMsg, TextView coupanCodeMsg) {
        double discount1 = Double.parseDouble(getDiscountedPrice(pdfPrice, coupanDiscount));
        double discount2 = Double.parseDouble(getDiscountedPrice(pdfPrice, Discount));
        double price = Double.parseDouble(pdfPrice);
        if (!((discount1 + discount2) > price)) {
            txnAmount = String.valueOf(price - (discount1 + discount2));
            subPdfPrice.setText("\u20B9 " + pdfPrice);
            subPdfDiscount.setText("- \u20B9 " + discount2);
            coupanCodeMsg.setVisibility(View.VISIBLE);
            coupanDiscountPrice.setVisibility(View.VISIBLE);
            coupanMsgLayout.setVisibility(View.VISIBLE);
            sub_discountMsg.setText("Regular Discount @ " + Discount + "%");
            coupanCodeMsg.setText("Coupan Code Discount @ " + coupanDiscount + "%");
            coupanDiscountPrice.setText("- \u20B9 " + discount1);
            finalPrice.setText("\u20B9 " + txnAmount);
            coupanActive = true;
        } else {
            setTransactionDetails(subPdfPrice, subPdfDiscount, finalPrice, coupanDiscountPrice, coupanMsgLayout, sub_discountMsg, coupanCodeMsg);
            Toast.makeText(PaytmPaymentpp.this, "invalid coupan ! contact admin", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    @SuppressLint("SetTextI18n")
    private void setTransactionDetails(TextView subPdfPrice, TextView subPdfDiscount, TextView finalPrice, TextView coupanDiscountPrice, LinearLayout coupanMsgLayout, TextView sub_discountMsg, TextView coupanCodeMsg) {
        txnAmount = getFinalPrice(pdfPrice, Discount);
        subPdfPrice.setText("\u20B9 " + pdfPrice);
        subPdfDiscount.setText("- \u20B9 " + getDiscountedPrice(pdfPrice, Discount));
        coupanCodeMsg.setText("Coupan Code Discount @ 0%");
        coupanDiscountPrice.setText("- \u20B9 " + 0.0);
        finalPrice.setText("\u20B9 " + txnAmount);
        sub_discountMsg.setText("Regular Discount @ " + Discount + "%");
        coupanCodeMsg.setVisibility(View.GONE);
        coupanDiscountPrice.setVisibility(View.GONE);
        coupanMsgLayout.setVisibility(View.GONE);
        coupanActive = false;
        dialog.dismiss();
    }

    @SuppressLint("SetTextI18n")
    private void setPdfDetails(TextView title, TextView price) {
        title.setText(pdfName);
        price.setText("\u20B9 " + pdfPrice);
        price.setTextColor(Color.BLACK);
    }

    private String getDiscountedPrice(String pdfPrice, String discount) {
        return String.valueOf(Math.round((Double.parseDouble(pdfPrice) * Double.parseDouble(discount)) / 100));
    }

    private String getFinalPrice(String pdfPrice, String discount) {
        double price1 = Double.parseDouble(pdfPrice), discount1 = Double.parseDouble(getDiscountedPrice(pdfPrice, discount));
        return String.valueOf(price1 - discount1);
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
                            Toast.makeText(PaytmPaymentpp.this, "token status false", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PaytmPaymentpp.this, "Server Response failed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Log.e(TAG, "Error in token Response" + e.toString());
                    Toast.makeText(PaytmPaymentpp.this, "Error in token Response" + e.toString(), Toast.LENGTH_LONG).show();
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
        dialog.dismiss();
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it //  String host = "https://securegw.paytm.in/";
        String callBackUrl = host + "theia/paytmCallback?ORDER_ID=" + OrderId;
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
                Toast.makeText(PaytmPaymentpp.this, "network not available", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                dialog.dismiss();
                Log.e(TAG, "Clientauth " + s);
                Toast.makeText(PaytmPaymentpp.this, "Clientauth " + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void someUIErrorOccurred(String s) {
                dialog.dismiss();
                Log.e(TAG, " UI error " + s);
                Toast.makeText(PaytmPaymentpp.this, " UI error " + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                dialog.dismiss();
                Log.e(TAG, " error loading web " + s + "--" + s1);
                Toast.makeText(PaytmPaymentpp.this, " error loading web " + s + "--" + s1, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                dialog.dismiss();
                Log.e(TAG, "backPress ");
                Toast.makeText(PaytmPaymentpp.this, "backPress ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                dialog.dismiss();
                Log.e(TAG, " transaction cancel " + s);
                Toast.makeText(PaytmPaymentpp.this, " transaction cancel " + s, Toast.LENGTH_SHORT).show();
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
            dialog.show();
            Toast.makeText(this,""+bundle.getString("TXNID"),Toast.LENGTH_SHORT).show();
            DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid)
                    .collection("transaction").document("pp").collection("TXNID")
                    .document(Objects.requireNonNull(bundle.getString("TXNID")));
            Map<String, String> map = new HashMap<>();
            Set<String> d = bundle.keySet();
            for (String key : d) map.put(key, bundle.getString(key));
            map.put("currentSubject", currentSubject);
            map.put("currentChapter", currentChapter);
            map.put("curruntPdf", currentSection);
            if (coupanActive) {
                map.put("coupanCode", coupanCode);
                map.put("coupanDiscount", coupanDiscount);
            }
            reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (Objects.equals(bundle.getString("STATUS"), "TXN_SUCCESS"))
                        setSuccessProductDetails();
                    else {
                        Toast.makeText(PaytmPaymentpp.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaytmPaymentpp.this, "Transaction insertion failure", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PaytmPaymentpp.this, "Exception in data saving", Toast.LENGTH_SHORT).show();
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
                    map.put("currentSubject", currentSubject);
                    map.put("currentChapter", currentChapter);
                    if (coupanActive) {
                        map.put("coupanCode", coupanCode);
                        map.put("coupanDiscount", coupanDiscount);
                    }
                    map.put("status", "1");
                    reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            Toast.makeText(PaytmPaymentpp.this, "Transaction successfully done", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PaytmPaymentpp.this, "contact customer support", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaytmPaymentpp.this, "Failure product transaction", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PaytmPaymentpp.this, "Exception in setSuccessProductDetails", Toast.LENGTH_SHORT).show();
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