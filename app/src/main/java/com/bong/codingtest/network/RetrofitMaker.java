package com.bong.codingtest.network;

import android.content.Context;
import android.util.Log;

import com.bong.codingtest.BuildConfig;
import com.bong.codingtest.data.Apiservice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMaker {
    private OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    Dispatcher dispatcher;
    private static final int timeout_read = 60;
    private static final int timeout_connect = 60;
    private static final int timeout_write = 60;

    private static Retrofit getRetrofit(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(Apiservice.USER_API_URL).build();
        }
        return retrofit;
    }

    private static Gson getGson() {
        return new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).create();
    }

    private Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Apiservice.USER_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(getGson()));

    public <S> S createService(final Context context, Class<S> serviceClass) {
        Retrofit retrofit = builder.client(getHttpClient(context.getApplicationContext())).build();
        return retrofit.create(serviceClass);
    }

    private OkHttpClient getHttpClient(final Context context) {
        if (okHttpClient == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            //httpClientBuilder.protocols(getProtocols());
            httpClientBuilder.connectTimeout(timeout_connect, TimeUnit.HOURS);
            httpClientBuilder.readTimeout(timeout_read, TimeUnit.HOURS);
            httpClientBuilder.writeTimeout(timeout_write, TimeUnit.HOURS);

//            if (true) {
            if (BuildConfig.DEBUG) {
                //httpClientBuilder.addInterceptor(new MockInterceptor(context));
                //httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.e("PRETTYLOGGER", "message:" + message))
                        .setLevel(HttpLoggingInterceptor.Level.BASIC)
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS);

//                HttpLoggingInterceptor logging = new HttpLoggingInterc
                logging.level(HttpLoggingInterceptor.Level.BODY);
                httpClientBuilder.addInterceptor(logging);
            }
//            httpClientBuilder.connectionPool(new ConnectionPool(1, 5, TimeUnit.MINUTES));

            dispatcher = new Dispatcher();
            httpClientBuilder.dispatcher(dispatcher);
//            httpClientBuilder.addInterceptor(chain -> {
//                Request request = chain.request().newBuilder().addHeader("q", "a").build();
//                return chain.proceed(request);
//            });
            okHttpClient = httpClientBuilder.build();
        }
        return okHttpClient;
    }
}