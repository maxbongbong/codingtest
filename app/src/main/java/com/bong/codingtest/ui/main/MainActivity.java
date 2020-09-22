package com.bong.codingtest.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.bong.codingtest.R;
import com.bong.codingtest.data.Apiservice;
import com.bong.codingtest.data.User;
import com.bong.codingtest.network.RetrofitMaker;
import com.bong.codingtest.ui.fragment.SearchFragment;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {
    public static SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment searchFragment;
        searchFragment = new SearchFragment();
        changeFragment(searchFragment);
        searchView = findViewById(R.id.searchView);
    }

    /**
     * Fragment 띄우기
     * */
    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment).commit();
    }
}
