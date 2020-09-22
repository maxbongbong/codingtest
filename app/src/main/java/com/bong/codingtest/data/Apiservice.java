package com.bong.codingtest.data;

import java.util.List;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Apiservice {
    String USER_API_URL = "https://api.github.com/";

    @GET ("search/users?q=a")
    Single<Response<Item>> getUserRx(@Query("page") String key);

    @GET
    Single<Response<Item>> getItemRx(@Url String key);

    @GET
    Single<List<Org>> getOrgs(@Url String orgs);
}
