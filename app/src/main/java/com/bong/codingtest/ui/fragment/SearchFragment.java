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
import com.bong.codingtest.data.Orgs;
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
    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView1;
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        Context context = view.getContext();
        progressBar = view.findViewById(R.id.progressBar);
        searchView = MainActivity.searchView;
        disposables = new CompositeDisposable();
        recyclerView1 = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView1.setLayoutManager(mLayoutManager);
        textWatcher(searchView);
        recyclerView1.setHasFixedSize(true);
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
//                getApi(newText);
                return false;
            }
        });
    }

    private void adapterData(Context context, List<User> item) {
        SearchAdapter searchAdapter = new SearchAdapter(context, item, this);
        recyclerView1.setAdapter(searchAdapter);
    }

    @Override
    public void ItemListener(View v, int position, String login) {
        Log.e("position1", "position1 = " + position);
        getOrgs("users/" + login + "/orgs");
    }

    private void getOrgs(String s) {
        RecyclerView recyclerView2 = this.getView().findViewById(R.id.orgsRecyclerView);
        ProgressBar progressBar = this.getView().findViewById(R.id.progress_horizontal);
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<String> orgsAvatarList = new ArrayList<>();
        HorizontalAdapter horizontalAdapter = new HorizontalAdapter(orgsAvatarList, getContext());
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Single<List<Orgs>> item = apiservice.getorgs(s);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Orgs>>() {

                    @Override
                    public void onSuccess(List<Orgs> orgsList) {
                        for (int i = 0; i < orgsList.size(); i++) {
                            orgsAvatarList.add(orgsList.get(i).getAvatar_url());
                        }
                        recyclerView2.setHasFixedSize(true);
                        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        if (orgsAvatarList.size() == 0) {
                            recyclerView2.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            toastMessage();
                            Log.e("orgs", "size = " + orgsAvatarList.size());
                            Log.e("complete", "complete");
                        } else {
                            recyclerView2.setAdapter(horizontalAdapter);
                            progressBar.setVisibility(View.GONE);
                            recyclerView2.setVisibility(View.VISIBLE);
                        }
                        horizontalAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("e", "e = " + e);
                    }
                })
        );
    }

    public void toastMessage(){
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