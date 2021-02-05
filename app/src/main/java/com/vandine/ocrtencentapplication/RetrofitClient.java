package com.vandine.ocrtencentapplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static OkHttpClient mOkHttpClient;
    private static final Long TIME_OUT = 15000L;       //15秒

    private static Retrofit mRetrofit ;

    private static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
//                    //拦截重复调用接口
//                    .addInterceptor(new MyHttpInterceptor().setLevel(MyHttpInterceptor.Level.BODY))
                    .build();
        }
        return mOkHttpClient;
    }

    private static Retrofit initRetrofit(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.131:9090/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
        return mRetrofit;
    }

    public static ApiService getAccessToken(){
        return initRetrofit().create(ApiService.class);
    }
    public static ApiService getOrcResult(){
        return initRetrofit().create(ApiService.class);
    }


}
