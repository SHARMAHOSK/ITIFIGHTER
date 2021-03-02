
package com.example.itifighter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

 public class PaytmPayment extends AppCompatActivity {
    private final String Mid = "ahKvVJ36172797507439";
    private final String Uid = FirebaseAuth.getInstance().getUid();
    private String OrderId, Mobile, Email, firstName;
    private String FinalMonth = "", selectedPrice = "", txnAmount = "",
            coupanDiscount = "", regularDiscount = "", taxAmount = "0.0",
            coupanCODE= "";
    private String currentSubject, currentChapter, currentSection;
    private final String TAG = "PaytmPayment";
    private ProgressDialog dialog;
    private boolean coupanActive = false, coupanACTIVE = false;
    private TextView sub_itempricex, sub_taxpricex,
            sub_coupanCodePrice, sub_discountpricex,
            finalpricex, coupanCodeMsg, sub_discountMsg;
    private LinearLayout coupanMsgLayout;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_payment);
        Intent intent = getIntent();
        dialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        // get Data from intent
        final String Month1 = xNull(intent.getStringExtra("month1"));
        final String Month2 = xNull(intent.getStringExtra("month2"));
        final String Month3 = xNull(intent.getStringExtra("month3"));
        final String price1 = xNull(intent.getStringExtra("price1"));
        final String price2 = xNull(intent.getStringExtra("price2"));
        final String price3 = xNull(intent.getStringExtra("price3"));
        final String Disc01 = xNull(intent.getStringExtra("discount1"));
        final String Disc02 = xNull(intent.getStringExtra("discount2"));
        final String Disc03 = xNull(intent.getStringExtra("discount3"));
        currentSection = xNull(intent.getStringExtra("currentSection"));
        currentSubject = xNull(intent.getStringExtra("currentSubject"));
        currentChapter = xNull(intent.getStringExtra("currentChapter"));
        String title = xNull(intent.getStringExtra("titleName"));
        int CountTest = iNull(intent.getIntExtra("countTest", 0));
        final String coupanCODE = xNull(intent.getStringExtra("couponCODE"));
        final String couponACTIVE = xNull(intent.getStringExtra("couponACTIVE"));
        final String coupanDiscount = xNull(intent.getStringExtra("coupanDiscount"));
        coupanACTIVE = xNull(couponACTIVE).toLowerCase().trim().equalsIgnoreCase("true");
        this.coupanDiscount = xNull(coupanDiscount);
        this.coupanCODE = xNull(coupanCODE);


        //------get Instance of layout and set data in Header view----------------------------------------------//

        TextView headerTitle = findViewById(R.id.testxy_chapter_titlex),
                headerTestCount = findViewById(R.id.testxy_desc_textx),
                headerBatch = findViewById(R.id.testxytbatchx),
                headerPrice = findViewById(R.id.testxytpricex);
        ImageView headerImage = findViewById(R.id.testxy_image_viewx);
        coupanMsgLayout = findViewById(R.id.coupanMsg);


        headerTitle.setText(title);
        headerTestCount.setText(CountTest + " Tests");
        headerBatch.setText(Month1 + " Months Available with " + Disc01 + "% Discount");
        headerPrice.setText("\u20B9 " + price1 + " " + coupanCODE + " " + coupanDiscount + " " + coupanACTIVE);
        Glide.with(this)
                .load(R.drawable.quizlogo)
                .into(headerImage);

        //------get Instance of layout and set data in Header view----------------------------------------------//

        //--------fill Details in options-----------------------------------------------------------------------//

        //set month in option----------//
        final TextView monthX1 = findViewById(R.id.monthX1),
                monthX2 = findViewById(R.id.monthX2),
                monthX3 = findViewById(R.id.monthX3);
        monthX1.setText(Month1 + " Months");
        monthX2.setText(Month2 + " Months");
        monthX3.setText(Month3 + " Months");

        //set Discount in option---------//
        TextView descX1 = findViewById(R.id.descX1),
                descX2 = findViewById(R.id.descX2),
                descX3 = findViewById(R.id.descX3);
        descX1.setText("Unlock all test @ " + Disc01 + "% \nDiscount");
        descX2.setText("Unlock all test @ " + Disc02 + "% \nDiscount");
        descX3.setText("Unlock all test @ " + Disc03 + "% \nDiscount");

        //set Price in option--------------//
        TextView priceX02 = findViewById(R.id.priceX02),
                priceX12 = findViewById(R.id.priceX12),
                priceX22 = findViewById(R.id.priceX22);
        priceX02.setText("\u20B9 " + price1);
        priceX12.setText("\u20B9 " + price2);
        priceX22.setText("\u20B9 " + price3);
        priceX02.setPaintFlags(priceX02.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        priceX12.setPaintFlags(priceX12.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        priceX22.setPaintFlags(priceX22.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //set Discounted Final price in option---------//
        TextView priceX01 = findViewById(R.id.priceX01),
                priceX11 = findViewById(R.id.priceX11),
                priceX21 = findViewById(R.id.priceX21);

        assert price1 != null && Disc01 != null;
        final String discount1 = discountedPrice(price1, Disc01);
        final String finalPrice1 = finalPrice(price1, discount1);
        priceX01.setText("\u20B9 " + finalPrice1);

        assert price2 != null && Disc02 != null;
        final String discount2 = discountedPrice(price2, Disc02);
        final String finalPrice2 = finalPrice(price2, discount2);
        priceX11.setText("\u20B9 " + finalPrice2);

        assert price3 != null && Disc03 != null;
        final String discount3 = discountedPrice(price3, Disc03);
        final String finalPrice3 = finalPrice(price3, discount3);
        priceX21.setText("\u20B9 " + finalPrice3);

        //--------fill Details in options-----------------------------------------------------------------------//

        //--------------------set Payment Information --------------------------------------------------------------//
        sub_itempricex = findViewById(R.id.sub_itempricex);
        sub_taxpricex = findViewById(R.id.sub_taxpricex);
        sub_coupanCodePrice = findViewById(R.id.coupnCodeDiscount);
        sub_discountpricex = findViewById(R.id.sub_discountpricex);
        finalpricex = findViewById(R.id.finalpricex);
        coupanCodeMsg = findViewById(R.id.coupanCodeMsg);
        sub_discountMsg = findViewById(R.id.sub_discountx);


        final RadioButton Ra1 = findViewById(R.id.ra1),
                Ra2 = findViewById(R.id.ra2),
                Ra3 = findViewById(R.id.ra3);
        final ConstraintLayout o1 = findViewById(R.id.optionX1),
                o2 = findViewById(R.id.optionX2),
                o3 = findViewById(R.id.optionX3);
        Button cancel = findViewById(R.id.cancelx),
                payx = findViewById(R.id.payx);

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
                if(txnAmount.trim().isEmpty() || OrderId.trim().isEmpty()){
                    Toast.makeText(PaytmPayment.this,"order have some error \n contact admin!",Toast.LENGTH_SHORT).show();
                }
                else if(vNull(txnAmount.trim())<1) {
                    setAutoPayment();
                }else{
                    getToken();
                }
            }
        });

        TextView removeCoupanButton = findViewById(R.id.removeCoupanButton);
        Button couponButton = findViewById(R.id.coupanClick);
        final EditText text = findViewById(R.id.textCoupanCode);

        removeCoupanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                coupanActive = false;
                setPaymentInformation();
                dialog.dismiss();
            }
        });

        couponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!coupanActive) {
                    dialog.show();
                    String textCoupan = text.getText().toString().trim();
                    if (!textCoupan.trim().isEmpty()) {
                        if (textCoupan.equalsIgnoreCase(coupanCODE) && (vNull(coupanDiscount) > 0)) {
                                if (coupanACTIVE) {
                                    setApplyCoupanDetails();
                                } else {
                                    dialog.dismiss();
                                    text.setError("Coupan is not Active ");
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


        Ra1.setChecked(true);
        Ra2.setChecked(false);
        Ra3.setChecked(false);
        clear(price1, Disc01, Month1);
        setPaymentInformation();

        o1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ra1.setChecked(true);
                Ra2.setChecked(false);
                Ra3.setChecked(false);
                clear(price1, Disc01, Month1);
                setPaymentInformation();
            }
        });
        o2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ra1.setChecked(false);
                Ra2.setChecked(true);
                Ra3.setChecked(false);
                clear(price2, Disc02, Month2);
                setPaymentInformation();
            }
        });
        o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ra1.setChecked(false);
                Ra2.setChecked(false);
                Ra3.setChecked(true);
                clear(price3, Disc03, Month3);
                setPaymentInformation();
            }
        });
        Ra1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ra1.setChecked(true);
                Ra2.setChecked(false);
                Ra3.setChecked(false);
                clear(price1, Disc01, Month1);
                setPaymentInformation();
            }
        });
        Ra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ra1.setChecked(false);
                Ra2.setChecked(true);
                Ra3.setChecked(false);
                clear(price2, Disc02, Month2);
                setPaymentInformation();
            }
        });
        Ra3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ra1.setChecked(false);
                Ra2.setChecked(false);
                Ra3.setChecked(true);
                clear(price3, Disc03, Month3);
                setPaymentInformation();
            }
        });
    }

    private void clear(String price, String discount, String month) {
        selectedPrice = xNull(price);
        regularDiscount = xNull(discount);
        FinalMonth = xNull(month);
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

                            Log.e(TAG, "token status false");
                            Toast.makeText(PaytmPayment.this, "token status false", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {

                    Log.e(TAG, "Error in token Response" + e.toString());
                    Toast.makeText(PaytmPayment.this, "Error in token Response" + e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token_Res> call, @NonNull Throwable t) {

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
                dialog.show();
                setTransactionDetails(bundle);
            }

            @Override
            public void networkNotAvailable() {
                dialog.dismiss();
                Log.e(TAG, "network not available ");
                Toast.makeText(PaytmPayment.this, "network not available", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                dialog.dismiss();
                Log.e(TAG, "Clientauth " + s);
                Toast.makeText(PaytmPayment.this, "Clientauth " + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void someUIErrorOccurred(String s) {
                dialog.dismiss();
                Log.e(TAG, " UI error " + s);
                Toast.makeText(PaytmPayment.this, " UI error " + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                dialog.dismiss();
                Log.e(TAG, " error loading web " + s + "--" + s1);
                Toast.makeText(PaytmPayment.this, " error loading web " + s + "--" + s1, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                dialog.dismiss();
                Log.e(TAG, "backPress ");
                Toast.makeText(PaytmPayment.this, "backPress ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                dialog.dismiss();
                Log.e(TAG, " transaction cancel " + s);
                Toast.makeText(PaytmPayment.this, " transaction cancel " + s, Toast.LENGTH_SHORT).show();
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
            DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid)
                    .collection("transaction").document(currentSection).collection("TXNID")
                    .document(Objects.requireNonNull(bundle.getString("TXNID")));
            final String TXNDATE = bundle.getString("TXNDATE");
            Map<String, String> map = new HashMap<>();
            Set<String> d = bundle.keySet();
            for (String key : d) map.put(key, bundle.getString(key));
            map.put("currentSubject", currentSubject);
            map.put("currentChapter", currentChapter);
            reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (Objects.equals(bundle.getString("STATUS"), "TXN_SUCCESS"))
                        setSuccessProductDetails(TXNDATE);
                    else {
                        Toast.makeText(PaytmPayment.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaytmPayment.this, "Transaction insertion failure", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PaytmPayment.this, "Exception in data saving", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSuccessProductDetails(final String TXNDATE) {
        try {
            final DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid)
                    .collection("Products").document(currentSection).collection("ProductId").document(currentChapter);
            final Map<String, String> map = new HashMap<>();
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String oldDate = documentSnapshot.getString("ExpiryDate");
                    if (oldDate != null && getDateAfter(oldDate, TXNDATE))
                        map.put("ExpiryDate", getExpiryDate(oldDate));
                    else {
                        map.put("ExpiryDate", getExpiryDate(TXNDATE));
                    }
                    map.put("currentSubject", currentSubject);
                    map.put("currentChapter", currentChapter);
                    map.put("status", "1");
                    reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            Toast.makeText(PaytmPayment.this, "Transaction successfully done", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PaytmPayment.this, "contact customer support", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaytmPayment.this, "Failure product transaction", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PaytmPayment.this, "Exception in setSuccessProductDetails", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setPaymentInformation() {
        String regularDiscountedPrice = discountedPrice(selectedPrice, regularDiscount);
        String finalPrice = finalPrice(selectedPrice, regularDiscountedPrice);
        sub_itempricex.setText("\u20B9 " + selectedPrice);
        sub_taxpricex.setText("+ \u20B9 " + taxAmount);
        sub_discountpricex.setText("- \u20B9 " + regularDiscountedPrice);
        sub_discountMsg.setText("Regular Discount @ " + regularDiscount + "%");
        if (coupanActive) {
            setApplyCoupanDetails();
        } else {
            setFinalPayment(finalPrice, "", false);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setApplyCoupanDetails() {
        try {
            double discount1 = vNull(discountedPrice(selectedPrice, coupanDiscount));
            double discount2 = vNull(discountedPrice(selectedPrice, regularDiscount));
            double itemPrice = vNull(selectedPrice);
            String finalPrice = finalPrice(vNull(itemPrice), vNull(discount1 + discount2));
            if (vNull(finalPrice) < 0) {
                coupanActive = false;
                setPaymentInformation();
                Toast.makeText(PaytmPayment.this, "invalid coupan ! contact admin", Toast.LENGTH_SHORT).show();
            } else {
                setFinalPayment(finalPrice, vNull(discount1), true);
                coupanActive = true;
            }
        } catch (Exception e) {
            Toast.makeText(PaytmPayment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            dialog.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFinalPayment(String finalPrice, String discount, boolean withCoupan) {
        try {
            txnAmount = xNull(finalPrice);
            finalpricex.setText("\u20B9 " + txnAmount);
            if (withCoupan) {
                coupanCodeMsg.setText("Coupan Code Discount @ " + coupanDiscount + "%");
                sub_coupanCodePrice.setText("- \u20B9 " + discount);
            }
            coupanCodeMsg.setVisibility(withCoupan ? View.VISIBLE : View.GONE);
            sub_coupanCodePrice.setVisibility(withCoupan ? View.VISIBLE : View.GONE);
            coupanMsgLayout.setVisibility(withCoupan ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            Toast.makeText(PaytmPayment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean getDateAfter(String oldDate1, String txndate) {
        boolean result = false;
        try {
            SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d1 = s1.parse(oldDate1), d2 = s1.parse(txndate);
            assert d1 != null;
            if (d1.after(d2)) result = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getExpiryDate(String txndate) {
        try {
            Calendar c = Calendar.getInstance();
            //yyyy-MM-dd hh:mm:ss.0
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");
            c.setTime(Objects.requireNonNull(sdf.parse(txndate)));
            c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(FinalMonth) * 30);
            txndate = sdf.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txndate;
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

    public String finalPrice(String price, String discount) {
        return vNull(vNull(price) - vNull(discount));
    }

    public String discountedPrice(String price, String discount) {
        return vNull(vNull(price) * vNull(discount) * 0.01);
    }

    public String xNull(String str) {
        if (str != null) return str;
        else return "";
    }

    public boolean bNull(String str) {
        if (str == null) return false;
        else return !str.trim().isEmpty();
    }

    public Double dNull(Double dbl) {
        if (dbl == null) return 0.0;
        else if (dbl < 1.0) return 0.0;
        else return dbl;
    }

    public Double vNull(String str) {
        double num = 0.0;
        if (bNull(str)) {
            try {
                num = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                num = 0.0;
            }
        }
        return num;
    }

    public String vNull(double str) {
        String num = "0.0";
        if (dNull(str) > 0) {
            try {
                num = String.valueOf(str);
            } catch (Exception e) {
                num = "0.0";
            }
        }
        return num;
    }

    public int iNull(int value) {
        if (value < 1) return 0;
        else return value;
    }

    public int iNull(String value) {
        int num = 0;
        if (bNull(xNull(value))) {
            try {
                num = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                num = 0;
            }
        }
        return iNull(num);
    }

    public String aNull(int value) {
        String num = "0";
        if (iNull(value) > 0) {
            try {
                num = String.valueOf(value);
            } catch (Exception e) {
                num = "0";
            }
        }
        return num;
    }


    public long lNull(long value) {
        if (value < 1) return 0;
        else return value;
    }


    private void setAutoPayment() {
        if(vNull(txnAmount.trim())<1){
            try {
                Bundle bundle;
                bundle = setAutoBundle();
                setTransactionDetails(bundle);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(PaytmPayment.this, "Exception in Auto data saving", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bundle setAutoBundle() {
        Bundle bundle = new Bundle();
        if(vNull(txnAmount.trim())<1){
            try{
                bundle.putString("BANKNAME", "FREE");
                bundle.putString("BANKTXNID", "FREE" + coupanCODE);
                bundle.putString("CHECKSUMHASH", "FREE" + coupanCODE);
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
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");
                String date = sdf.format(d);
                bundle.putString("TXNDATE", xNull(date));
                bundle.putString("TXNID", "FREE" + xNull(OrderId) + xNull(Uid));
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return bundle;
    }


    @Override
    protected void onStart() {
        super.onStart();
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