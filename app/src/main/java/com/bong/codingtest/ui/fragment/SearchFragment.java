package com.bong.codingtest.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.Apiservice;
import com.bong.codingtest.data.Item;
import com.bong.codingtest.data.Orgs;
import com.bong.codingtest.data.User;
import com.bong.codingtest.network.RetrofitMaker;
import com.bong.codingtest.ui.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener {
    protected CompositeDisposable disposables;
    private SearchAdapter searchAdapter;
    public SearchView searchView;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        Context context = view.getContext();
        progressBar = view.findViewById(R.id.progressBar);
        searchView = MainActivity.searchView;
        disposables = new CompositeDisposable();
        recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        textWatcher(searchView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @SuppressLint("CheckResult")
    private void getApi(String s) {
        progressBar.setVisibility(View.VISIBLE);
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Single<Item> item = apiservice.getUserRx(s);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Item>(){
                    @Override
                    public void onSuccess(Item item) {
                        userList = item.items;
                        adapterData(getContext(), userList);
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("e", "e = " + e);
                    }
                })
        );
    }


    public void textWatcher(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getApi(newText);
                return true;
            }
        });
    }

    private void adapterData(Context context, List<User> item){
        searchAdapter = new SearchAdapter(context, item);
        recyclerView.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (disposables != null) {
            disposables.clear();
            disposables.dispose();
        }
    }

    @Override
    public void OnItemClickListener(View v, int position, String s) {
        getOrgs(s);
    }

    private void getOrgs(String s) {
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Log.e("s", "s = " + s);
        progressBar.setVisibility(View.VISIBLE);
        Single<List<Orgs>> item = apiservice.getorgs(s);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Orgs>>() {
                    @Override
                    public void onSuccess(List<Orgs> orgs) {
                        if (orgs != null) {
                            adapterData(getContext(), userList);
                            progressBar.setVisibility(View.GONE);
                            for (int i = 0; i < orgs.size(); i++) {
                                Log.e("log", "log = " + orgs.get(i).getAvatar_url());
                            }
                        } else {
                            Log.e("log", "log = null");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                })
        );
    }
}