package com.bong.codingtest.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import io.reactivex.subscribers.DisposableSubscriber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener{
    protected CompositeDisposable disposables;
    public SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<User> userList;
    static public List<Orgs> orgsList = new ArrayList<>();
    private List<String> orgsUrl = new ArrayList<>();

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
        textWatcher(searchView);
        recyclerView.setHasFixedSize(true);
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

                        for(int i = 0; i < userList.size(); i++){
//                            orgsUrl.add("users/" + userList.get(i).getLogin() + "/orgs");
                            getOrgs("users/" + userList.get(i).getLogin() + "/orgs");
                        }
//                        for(int i = 0; i < userList.size(); i++){
//                            Log.e("orgurl", "orgUrl = " + orgsUrl.get(i));
//                            getOrgs(orgsUrl.get(i));
//                        }
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
        SearchAdapter searchAdapter = new SearchAdapter(context, item, this);
        recyclerView.setAdapter(searchAdapter);
//        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void ItemListener(View v, int position) {
        LinearLayout layout = this.getView().findViewById(R.id.layout);
        RecyclerView recyclerView = this.getView().findViewById(R.id.orgsRecyclerView);
        layout.setOnClickListener(v1 -> {
            if (userList.get(position).isStatus()) {
                recyclerView.setVisibility(View.VISIBLE);
                userList.get(position).setStatus(false);
            } else if (!userList.get(position).isStatus()) {
                recyclerView.setVisibility(View.GONE);
                userList.get(position).setStatus(true);
            }
        });
    }

    private void getOrgs(String s) {
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        ArrayList<Single<List<Orgs>>> temp = new ArrayList<>();
        ArrayList<List<Orgs>> test = new ArrayList<>();
//        Single<List<Orgs>> item = apiservice.getorgs(s);
        for (int i = 0; i < userList.size(); i++) {
            temp.add(apiservice.getorgs(s).map(orgsList1 -> {
//                orgsList = orgsList1;
                return orgsList1;
            }));
        }
        Log.e("temp", "size = " + temp.size());
        final boolean add = disposables.add(Single.concat(temp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber <List<Orgs>>() {
                    @Override
                    public void onNext(List<Orgs> orgsList) {
//                        for(int i = 0; i < orgsList.size(); i++) {
//                            Log.e("orglist", "orgList = " + orgsList.get(i));
//                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("complete", "complete");
                    }
                }));


//        disposables.add(item
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<Orgs>>() {
//                    @Override
//                    public void onSuccess(List<Orgs> orgs) {
//                        if (orgs != null) {
//                            orgsList = orgs;
//                            for (int i = 0; i < orgs.size(); i++) {
//                                Log.e("log", "log = " + orgsList.get(i).getAvatar_url());
//                                orgsList.get(i).getAvatar_url();
//                            }
//                        } else {
//                            Log.e("log", "log = null");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//                })
//        );
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