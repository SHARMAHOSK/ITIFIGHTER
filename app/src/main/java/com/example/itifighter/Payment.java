package com.example.itifighter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;

import org.json.JSONObject;

public class Payment extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int orderamount;
    private EditText order_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        final Button startpayment = findViewById(R.id.start_transaction);
        final EditText amt = findViewById(R.id.payprice);
        order_id= findViewById(R.id.orderid);
        EditText cust_id = findViewById(R.id.custid);
        Toast.makeText(this,amt.getText().toString(),Toast.LENGTH_SHORT).show();
        if(orderamount<100)orderamount+=100;
        Checkout.clearUserData(getApplicationContext());
        Checkout.preload(getApplicationContext());
        startpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(amt.getText().toString().equals(""))
                {
                    Toast.makeText(Payment.this, "Amount is empty", Toast.LENGTH_LONG).show();
                }else if(order_id.getText().toString().equals("")) {
                    Toast.makeText(Payment.this, "Order id is empty", Toast.LENGTH_LONG).show();
                }
                else {
                    Order order = getOrderId();
                    if(order == null) {
                        System.out.println(order);
                       // Toast.makeText(getParent(),null,Toast.LENGTH_LONG).show();
                    }
                    else{
                        System.out.println(order);
                        //Toast.makeText(getParent(),order.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }

            private Order getOrderId() {
                    Order order = null;
                try {
                    RazorpayClient razorpay = new RazorpayClient("rzp_test_4vIeFWPQsg4jEu","JryQrxo0puDzBRNQiPUPJ2Ki");
                    JSONObject orderRequest = new JSONObject();
                    orderRequest.put("amount",orderamount);
                    orderRequest.put("currency", "INR");
                    orderRequest.put("receipt",order_id);
                    orderRequest.put("payment_capture",false);
                    order = razorpay.Orders.create(orderRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return order;
            }
        });
    }
    public void startPayment(){
        final Activity activity = this;
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", "ITI Fighter");
            options.put("description","namaskaaaar");
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            //options.put("order_id", "order_9A33XWu170gUtm");
            options.put("amount","100");
            JSONObject preFill = new JSONObject();
            preFill.put("email", "sk19091997sk@gmail.com");
            preFill.put("contact", "919651324939");
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment shubham: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        finally {
            Checkout.clearUserData(getApplicationContext());
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        // payment successfull pay_DGU19rDsInjcF2
        Log.e(TAG, " payment successfull "+ s);
        Toast.makeText(this, "Payment successfully done! " +s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e(TAG,  "error code "+ i +" -- Payment failed "+s);
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }

    }
}

