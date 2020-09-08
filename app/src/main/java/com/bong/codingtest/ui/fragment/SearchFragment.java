package com.bong.codingtest.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.Apiservice;
import com.bong.codingtest.data.User;
import com.bong.codingtest.network.RetrofitMaker;
import com.bong.codingtest.ui.main.MainActivity;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    protected CompositeDisposable disposables;
    private SearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        Context context = view.getContext();

        disposables = new CompositeDisposable();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
//        searchAdapter = new SearchAdapter(context, new User().getClass());
        SearchView searchView = MainActivity.searchView;
        textWatcher(searchView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        searchAdapter.notifyDataSetChanged();

    }

    public void callApi(String s){
        Apiservice apiservice = new RetrofitMaker().createService(getActivity(), Apiservice.class);
        Call<User> commentStr = apiservice.getUser(s);
        commentStr.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                boolean isSucessful = response.isSuccessful();
                if(isSucessful){
                    User user = response.body();
                    ArrayList<String> login = new ArrayList<>();
                    ArrayList<String> profile = new ArrayList<>();
                    ArrayList<Integer> score = new ArrayList<>();
                    login.add(user.login);
                    profile.add(user.avatar_url);
                    score.add(user.score);

                    Log.e("body", "body" + response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {}
        });
    }

    public void getApi(String s) {
        Apiservice apiservice = new RetrofitMaker().createService(getActivity(), Apiservice.class);
        ArrayList<Single<User>> temp = new ArrayList<>();
        ArrayList<String> login = new ArrayList<>();
        ArrayList<Integer> score = new ArrayList<>();
        temp.add(apiservice.getUserRx(s).map(user -> {
                    score.add(user.score);
                    login.add(user.login);

                    return user;
                })
        );

        final boolean add = disposables.add(Single.concat(temp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<User>() {
                    @Override
                    public void onNext(User user) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e("temp", "temp = " + temp.size());
//                        Log.e("login", "login = " + login.get(0));
//                        Log.e("score", "score = " + score.get(0));
                    }
                }));
    }



    public void textWatcher(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("text", "text = " + newText);
                getApi(newText);
                callApi(newText);
                return true;
            }
        });
    }
}