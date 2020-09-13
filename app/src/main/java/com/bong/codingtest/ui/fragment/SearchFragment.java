package com.bong.codingtest.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.Apiservice;
import com.bong.codingtest.data.Item;
import com.bong.codingtest.data.Org;
import com.bong.codingtest.data.User;
import com.bong.codingtest.network.RetrofitMaker;
import com.bong.codingtest.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener {
    protected CompositeDisposable disposables;
    private ProgressBar progressBar;
    private RecyclerView recyclerView1;
    private List<User> userList;
    private SearchAdapter searchAdapter;
    private List<Org> test;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_search, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        disposables = new CompositeDisposable();
        recyclerView1 = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView1.setLayoutManager(mLayoutManager);
        recyclerView1.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = MainActivity.searchView;
        textWatcher(searchView);
    }

    @SuppressLint("CheckResult")
    private void getApi(String s) {
        progressBar.setVisibility(View.VISIBLE);
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Single<Item> item = apiservice.getUserRx(s);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Item>() {
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
                getApi(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void adapterData(Context context, List<User> item) {
        this.searchAdapter = new SearchAdapter(context, item, this);
        recyclerView1.setAdapter(searchAdapter);
    }

    @Override
    public void ItemListener(View v, int position, String login) {
        getOrgs("users/" + login + "/orgs", position);
    }

    private void getOrgs(String s, int position) {
        if (searchAdapter.getItemList().get(position).orgList != null) {
            return ;
        }
        searchAdapter.getItemList().get(position).connectingToServer = true;

        searchAdapter.notifyItemChanged(position);
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Single<List<Org>> item = apiservice.getorgs(s);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Org>>() {

                    @Override
                    public void onSuccess(List<Org> orgsList) {
                        if (searchAdapter.getItemList().get(position) != null) {
                            Log.e("url", "url = " + searchAdapter.getItemList().get(position).orgList);
                            searchAdapter.getItemList().get(position).connectingToServer = false;
                            searchAdapter.getItemList().get(position).orgList = orgsList;
                            searchAdapter.notifyItemChanged(position);
                        } else {
                            searchAdapter.getItemList().get(position).connectingToServer = false;
                            searchAdapter.notifyItemChanged(position);
                            toastMessage();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        searchAdapter.getItemList().get(position).connectingToServer = false;
                        Log.e("e", "e = " + e);
                    }
                })
        );
    }

    private void toastMessage() {
        Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.app_dataNull), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (disposables != null) {
            disposables.clear();
            disposables.dispose();
        }
    }
}