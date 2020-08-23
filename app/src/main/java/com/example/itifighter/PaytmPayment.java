package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
	private String Mid = "ahKvVJ36172797507439", Uid = FirebaseAuth.getInstance().getUid(), OrderId, Mobile, Email, txnAmount, firstName, FinalMonth, currentSubject, currentChapter, TAG = "PaytmPayment", currentSection;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paytm_payment);
		Intent intent = getIntent();
		// get Data from intent
		final String Month1 = intent.getStringExtra("month1");
		final String Month2 = intent.getStringExtra("month2");
		final String Month3 = intent.getStringExtra("month3");
		final String price1 = intent.getStringExtra("price1");
		final String price2 = intent.getStringExtra("price2");
		final String price3 = intent.getStringExtra("price3");
		final String Disc01 = intent.getStringExtra("discount1");
		final String Disc02 = intent.getStringExtra("discount2");
		final String Disc03 = intent.getStringExtra("discount3");
		currentSection      = intent.getStringExtra("currentSection");
		currentSubject      = intent.getStringExtra("currentSubject");
		currentChapter      = intent.getStringExtra("currentChapter");
		String title0       = intent.getStringExtra("titleName");
		String Count0       = intent.getStringExtra("countTest");


		//get Instance of layout and set data in main view
		TextView title1 = findViewById(R.id.testxy_chapter_titlex),Count1 = findViewById(R.id.testxy_desc_textx),
				Batch0  = findViewById(R.id.testxytbatchx) ,Price0 = findViewById(R.id.testxytpricex);
						title1.setText(title0);
						Count1.setText(Count0 + " Tests");
						Batch0.setText(Month1 + " Months Available with " + Disc01 + "% Discount");
						Price0.setText("\u20B9 " + price1);
						Batch0.setTextColor(Color.BLACK);
						Count1.setTextColor(Color.BLACK);
						Price0.setTextColor(Color.BLACK);

		//set  Months in option
		final TextView monthX1 = findViewById(R.id.monthX1), monthX2 = findViewById(R.id.monthX2), monthX3 = findViewById(R.id.monthX3);
						monthX1.setText(Month1 + " Months");
						monthX2.setText(Month2 + " Months");
						monthX3.setText(Month3 + " Months");

		//set Discount in option
		TextView descX1 = findViewById(R.id.descX1), descX2 = findViewById(R.id.descX2), descX3 = findViewById(R.id.descX3);
						descX1.setText("Unlock all test @ " + Disc01 + "% \nDiscount");
						descX2.setText("Unlock all test @ " + Disc02 + "% \nDiscount");
						descX3.setText("Unlock all test @ " + Disc03 + "% \nDiscount");

		//set Price in option
		TextView priceX02 = findViewById(R.id.priceX02), priceX12 = findViewById(R.id.priceX12), priceX22 = findViewById(R.id.priceX22);
						priceX02.setText("\u20B9 " + price1);
						priceX12.setText("\u20B9 " + price2);
						priceX22.setText("\u20B9 " + price3);
						priceX02.setPaintFlags(priceX02.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
						priceX12.setPaintFlags(priceX12.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
						priceX22.setPaintFlags(priceX22.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

		//set Discounted Final price in option
		TextView priceX01 = findViewById(R.id.priceX01), priceX11 = findViewById(R.id.priceX11), priceX21 = findViewById(R.id.priceX21);
		assert price1 != null && Disc01 != null;
        final double  Discount1= (Integer.parseInt(price1) * Integer.parseInt(Disc01) * 0.01);
		final String FinalPrice1 = String.valueOf(Integer.parseInt(price1) - Discount1);
		priceX01.setText("\u20B9 " + FinalPrice1);
		assert price2 != null && Disc02 != null;
		final double  Discount2= (Integer.parseInt(price2) * Integer.parseInt(Disc02) * 0.01);
		final String FinalPrice2 = String.valueOf(Integer.parseInt(price2) - Discount2);
		priceX11.setText("\u20B9 " + FinalPrice2);
		assert price3 != null && Disc03 != null;
		final double  Discount3= (Integer.parseInt(price3) * Integer.parseInt(Disc03) * 0.01);
		final String FinalPrice3 = String.valueOf(Integer.parseInt(price3) - Discount3);
		priceX21.setText("\u20B9 " + FinalPrice3);

		final TextView sub_itempricex = findViewById(R.id.sub_itempricex), sub_taxpricex = findViewById(R.id.sub_taxpricex),                                    sub_discountpricex = findViewById(R.id.sub_discountpricex), finalpricex = findViewById(R.id.finalpricex);
					   sub_taxpricex.setText("\u20B9 0.0");
		final RadioButton Ra1 = findViewById(R.id.ra1), Ra2 = findViewById(R.id.ra2), Ra3 = findViewById(R.id.ra3);
		final ConstraintLayout o1=findViewById(R.id.optionX1),o2=findViewById(R.id.optionX2),o3=findViewById(R.id.optionX3);
		Button cancel = findViewById(R.id.cancelx), payx = findViewById(R.id.payx);
               cancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       AreYouSureCancel();
                   }
               });
               payx.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (txnAmount.equals("")) {
                           android.widget.Toast.makeText(PaytmPayment.this, "", Toast.LENGTH_SHORT).show();
                       } else getToken();
                   }
               });
               Ra1.setChecked(true);
               Ra2.setChecked(false);
               Ra3.setChecked(false);
               sub_itempricex.setText("\u20B9 " + price1);
               sub_discountpricex.setText("\u20B9 " + Discount1);
               finalpricex.setText("\u20B9 " + FinalPrice1);
               txnAmount = FinalPrice1;
               FinalMonth = Month1;
               o1.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Ra1.setChecked(true);
                       Ra2.setChecked(false);
                       Ra3.setChecked(false);
                       sub_itempricex.setText("\u20B9 " + price1);
                       sub_discountpricex.setText("\u20B9 " + Discount1);
                       finalpricex.setText("\u20B9 " + FinalPrice1);
                       txnAmount = FinalPrice1;
                       FinalMonth = Month1;
                   }
               });
               o2.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Ra1.setChecked(false);
                       Ra2.setChecked(true);
                       Ra3.setChecked(false);
                       sub_itempricex.setText("\u20B9 " + price2);
                       sub_discountpricex.setText("\u20B9 " + Discount2);
                       finalpricex.setText("\u20B9 " + FinalPrice2);
                       txnAmount = FinalPrice2;
                       FinalMonth = Month2;
                   }
               });
               o3.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Ra1.setChecked(false);
                       Ra2.setChecked(false);
                       Ra3.setChecked(true);
                       sub_itempricex.setText("\u20B9 " + price3);
                       sub_discountpricex.setText("\u20B9 " + Discount3);
                       finalpricex.setText("\u20B9 " + FinalPrice3);
                       txnAmount = FinalPrice3;
                       FinalMonth = Month3;
                   }
               });
               Ra1.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Ra1.setChecked(true);
                       Ra2.setChecked(false);
                       Ra3.setChecked(false);
                       sub_itempricex.setText("\u20B9 " + price1);
                       sub_discountpricex.setText("\u20B9 " + Discount1);
                       finalpricex.setText("\u20B9 " + FinalPrice1);
                       txnAmount = FinalPrice1;
                       FinalMonth = Month1;
                   }
               });
                Ra2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Ra1.setChecked(false);
                        Ra2.setChecked(true);
                        Ra3.setChecked(false);
                        sub_itempricex.setText("\u20B9 " + price2);
                        sub_discountpricex.setText("\u20B9 " + Discount2);
                        finalpricex.setText("\u20B9 " + FinalPrice2);
                        txnAmount = FinalPrice2;
                        FinalMonth = Month2;
                    }
                });
                Ra3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Ra1.setChecked(false);
                        Ra2.setChecked(false);
                        Ra3.setChecked(true);
                        sub_itempricex.setText("\u20B9 " + price3);
                        sub_discountpricex.setText("\u20B9 " + Discount3);
                        finalpricex.setText("\u20B9 " + FinalPrice3);
                        txnAmount = FinalPrice3;
                        FinalMonth = Month3;
                    }
                });

        // Set Image in main view
		StorageReference mmFirebaseStorageRef = FirebaseStorage.getInstance().getReference()
                .child("menu/section/"+currentSection + "/chapter/");
		                        if (title0 != null) {
		                            if (title0.trim().length() > 0) {
		                                Glide.with(this)
                                                .load(mmFirebaseStorageRef.child(title0 + ".png"))
                                                .into((ImageView) findViewById(R.id.testxy_image_viewx));
		                            }
		                        }
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
                            Toast.makeText(PaytmPayment.this,"token status false",Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {

                    Log.e(TAG, "Error in token Response" + e.toString());
                    Toast.makeText(PaytmPayment.this,"Error in token Response" + e.toString(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Token_Res> call, @NonNull Throwable t) {

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
                Log.e(TAG, "network not available ");
                Toast.makeText(PaytmPayment.this,"network not available",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth " + s);
                Toast.makeText(PaytmPayment.this,"Clientauth " + s,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error " + s);
                Toast.makeText(PaytmPayment.this," UI error " + s,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web " + s + "--" + s1);
                Toast.makeText(PaytmPayment.this," error loading web " + s + "--" + s1,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
                Toast.makeText(PaytmPayment.this,"backPress ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel " + s);
                Toast.makeText(PaytmPayment.this," transaction cancel " + s,Toast.LENGTH_SHORT).show();
            }

            /*@Override
			public void onErrorProceed(String s) {
				Log.e(TAG, " onErrorProcess " + s);
			}*/

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
                    .collection("transaction").document(currentSection).collection(currentSubject)
                    .document(currentChapter).collection("transaction")
                    .document(Objects.requireNonNull(bundle.getString("TXNID")));
            final String TXNDATE = bundle.getString("TXNDATE");
            Map<String,String> map = new HashMap<>();
            Set<String> d = bundle.keySet();
            for (String key : d) map.put(key, bundle.getString(key));
            reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(Objects.equals(bundle.getString("STATUS"), "TXN_SUCCESS")) setSuccessProductDetails(TXNDATE);
                    else {

                        Toast.makeText(PaytmPayment.this,"Transaction failed",Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(PaytmPayment.this,"Transaction insertion failure",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){

            e.printStackTrace();
            Toast.makeText(PaytmPayment.this,"Exception in data saving",Toast.LENGTH_SHORT).show();
        }
	}
	private void setSuccessProductDetails(final String TXNDATE) {
		try {
			final DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid)
                    .collection("Products").document(currentSection)
                    .collection(currentSubject).document(currentChapter);
            final Map<String, String> map = new HashMap<>();
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String oldDate = documentSnapshot.getString("ExpiryDate");
                    if(oldDate!=null && getDateAfter(oldDate,TXNDATE)) map.put("ExpiryDate",getExpiryDate(oldDate));
                    else map.put("ExpiryDate",getExpiryDate(TXNDATE));
                    reference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PaytmPayment.this,"Transaction successfully done",Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(PaytmPayment.this,"contact customer support",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(PaytmPayment.this,"Failure product transaction",Toast.LENGTH_SHORT).show();
                }
            });
		}
		catch(Exception e){

			e.printStackTrace();
			Toast.makeText(PaytmPayment.this,"Exception in setSuccessProductDetails",Toast.LENGTH_SHORT).show();
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