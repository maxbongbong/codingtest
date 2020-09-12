package com.bong.codingtest.network;

import android.content.Context;
import android.util.Log;

import com.bong.codingtest.data.Apiservice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.BuildConfig;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMaker {
    private OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    Dispatcher dispatcher;
    private static final Long timeout_read = 60L;
    private static final Long timeout_connect = 60L;
    private static final Long timeout_write = 180L;

    private static Retrofit getRetrofit() {
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
            httpClientBuilder.connectTimeout(timeout_connect, TimeUnit.HOURS);
            httpClientBuilder.readTimeout(timeout_read, TimeUnit.HOURS);
            httpClientBuilder.writeTimeout(timeout_write, TimeUnit.HOURS);

            if (BuildConfig.DEBUG) {
                httpClientBuilder.addInterceptor(chain -> {
                    // Rate limit을 늘리기 위해서 수동으로 생성한 Github Personal Access Token
                    Request request = chain.request().newBuilder().addHeader("Authorization", "token " + com.bong.codingtest.BuildConfig.GITHUB_TK).build();
                    return chain.proceed(request);
                });

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.e("PRETTYLOGGER", "message:" + message))
                        .setLevel(HttpLoggingInterceptor.Level.BASIC)
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS);

                logging.level(HttpLoggingInterceptor.Level.BODY);
                httpClientBuilder.addInterceptor(logging);
            }

            dispatcher = new Dispatcher();
            httpClientBuilder.dispatcher(dispatcher);
            okHttpClient = httpClientBuilder.build();
        }
        return okHttpClient;
    }
}



