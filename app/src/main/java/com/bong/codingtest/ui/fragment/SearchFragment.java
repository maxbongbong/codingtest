package com.bong.codingtest.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener {
    protected CompositeDisposable disposables;
    private ProgressBar progressBar, pageNationBar;
    private RecyclerView itemListView;
    private List<User> userList;
    private SearchAdapter searchAdapter;
    private boolean moreItemState = false;
    private String str;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_search, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        pageNationBar = view.findViewById(R.id.pageNationBar);
        disposables = new CompositeDisposable();
        itemListView = view.findViewById(R.id.itemListView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        itemListView.setLayoutManager(mLayoutManager);
        itemListView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = MainActivity.searchView;
        textWatcher(searchView);
        initScrollListener();
    }

    /**
     * getApi에서 받은 header Link 값을 이용.
     * */
    private void pageKey(Response<Item> itemResponse) {
        str = itemResponse.headers().get("Link");
        List<String> obj = Arrays.asList(str.split(","));
        for (int i = 0; i < obj.size(); i++) {
            if (obj.get(i).contains("next")) {
                str = substringBetween(obj.get(i), "<", ">");
            }
        }
    }

    /**
     * recyclerView 마지막 item에 도달 할때, headerLink 값으로 item 더 가져오기.
     * */
    private void loadMoreItem() {
        pageNationBar.setVisibility(View.VISIBLE);
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Single<Response<Item>> item = apiservice.getItemRx(str);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<Item>>() {
                    @Override
                    public void onSuccess(Response<Item> itemResponse) {
                        boolean isSuccessful = itemResponse.isSuccessful();
                        if (isSuccessful) {
                            pageKey(itemResponse);
                            userList.addAll(itemResponse.body().items);
                            searchAdapter.notifyDataSetChanged();
                            pageNationBar.setVisibility(View.GONE);
                            searchAdapter.notifyItemInserted(itemResponse.body().items.size());
                        } else {
                            Log.e("Error", "Response Failed");
                        }
                        moreItemState = false;
                        pageNationBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    /**
     * searchView 입력 누를시, api 호출
     * */
    @SuppressLint("CheckResult")
    private void getApi() {
        progressBar.setVisibility(View.VISIBLE);
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Single<Response<Item>> item = apiservice.getUserRx("1");

        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<Item>>() {
                    @Override
                    public void onSuccess(Response<Item> itemResponse) {
                        boolean isSuccessful = itemResponse.isSuccessful();
                        if (isSuccessful) {
                            userList = itemResponse.body().items;
                            adapterData(getContext(), userList);
                            pageKey(itemResponse);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.e("Error", "Response Failed");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                })
        );
    }

    /**
     * recyclerView ScrollListener
     * */
    private void initScrollListener() {
        itemListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Handler handler = new Handler();
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    if (!moreItemState) {
                        handler.postDelayed(new moreItemHandler(), 200);
                        moreItemState = true;
                    }
                }
            }
        });
    }

    private void getOrgs(String s, int position) {
        if (searchAdapter.getItemList().get(position).orgList != null) {
            return;
        }
        searchAdapter.getItemList().get(position).connectingToServer = true;

        searchAdapter.notifyItemChanged(position);
        Apiservice apiservice = new RetrofitMaker().createService(getContext(), Apiservice.class);
        Single<List<Org>> item = apiservice.getOrgs(s);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Org>>() {

                    @Override
                    public void onSuccess(List<Org> orgsList) {
                        if (searchAdapter.getItemList().get(position) != null) {
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

    /**
     * searchAdapter에 layout 클릭 이벤트
     * */
    @Override
    public void ItemListener(View v, int position, String login) {
        getOrgs("users/" + login + "/orgs", position);
    }

    /**
     * searchAdapter에 데이터 set
     * */
    private void adapterData(Context context, List<User> item) {
        this.searchAdapter = new SearchAdapter(context, item, this);
        itemListView.setAdapter(searchAdapter);
    }

    /**
     * handler
     * */
    private class moreItemHandler implements Runnable {
        @Override
        public void run() {
            loadMoreItem();
        }
    }

    /**
     * 문자열 사이에 문자열 구하는 메소드
     * */
    private String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * searchView TextWatcher
     * */
    private void textWatcher(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyBoard();
                getApi();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * 소프트 키보드 숨기기.
     * */
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(MainActivity.searchView.getWindowToken(), 0);
    }

    /**
     * toast Message
     * */
    private void toastMessage() {
        Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.app_dataNull), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * CompositeDisposable 객체 해지
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposables != null) {
            disposables.clear();
            disposables.dispose();
        }
    }
}