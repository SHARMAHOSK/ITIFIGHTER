package com.example.itifighter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import paytm.assist.easypay.easypay.BuildConfig;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceWrapper {

    private ServiceInterface mServiceInterface;

    public ServiceWrapper() {
        mServiceInterface = getRetrofit().create(ServiceInterface.class);
    }

    public Retrofit getRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient mOkHttpClient = null;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(1201, TimeUnit.SECONDS);
        builder.readTimeout(901, TimeUnit.SECONDS);

      if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        mOkHttpClient = builder.build();
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl("https://noobworm.000webhostapp.com/PaytmPayment/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(mOkHttpClient)
                .build();
    }

    public Call<Token_Res> getTokenCall(String code, String mid, String order_id, String amount,String CustId,String Email,String Mobile,String firstName) {
        return mServiceInterface.generateTokenCall(
                convertPlainString(code), convertPlainString(mid), convertPlainString(order_id)
                , convertPlainString(amount), convertPlainString(CustId),convertPlainString(Email),convertPlainString(Mobile),convertPlainString(firstName));
    }
    // convert aa param into plain text
    public RequestBody convertPlainString(String data){
        return RequestBody.create(MediaType.parse("text/plain"), data);
    }

}

