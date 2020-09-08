package com.bong.codingtest.data;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Apiservice {
    String USER_API_URL = "https://api.github.com/";

    @GET ("search/users?q=")
    Call<User> getUser(@Query("login") String login);

    @GET ("search/users?q=")
    Single<User> getUserRx(@Query("login") String login);
}
