package com.bong.codingtest.data;

import java.util.List;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Apiservice {
    String USER_API_URL = "https://api.github.com/";

    @GET ("search/users?q=a")
    Single<Item> getUserRx(@Query("login") String login);

    @GET
    Single<List<Org>> getorgs(@Url String orgs);
}
