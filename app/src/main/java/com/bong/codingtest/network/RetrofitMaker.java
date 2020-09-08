package com.bong.codingtest.network;

import android.content.Context;

import com.bong.codingtest.data.Apiservice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Modifier;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMaker {
    private String authToken;

    public RetrofitMaker(String authToken) {
        this.authToken = authToken;
    }

    private static Gson getGson() {
        return new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).create();
    }

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Apiservice.USER_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(getGson()));

    private OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("header", authToken).build();

                    return chain.proceed(newRequest);
                }
            }).build();
    Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl(Apiservice.USER_API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();
    public <S> S createService(final Context context, Class<S> serviceClass, OkHttpClient client) {
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}