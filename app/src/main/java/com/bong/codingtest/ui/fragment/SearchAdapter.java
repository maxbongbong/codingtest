package com.bong.codingtest.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.Apiservice;
import com.bong.codingtest.data.Orgs;
import com.bong.codingtest.data.User;
import com.bong.codingtest.network.RetrofitMaker;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private List<User> itemList;
    private List<Orgs> orgsList;
    private OnItemClickListener mListener;
    private LayoutInflater mInflate;
    protected CompositeDisposable disposables;
    public String orgsUrl;

    public interface OnItemClickListener{
        void OnItemClickListener(View v, int position, String s);
    }

    public SearchAdapter(Context context, List<User> items) {
        this.context = context;
        this.mInflate = LayoutInflater.from(context);
        this.itemList = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position).getItemViewType() == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        disposables = new CompositeDisposable();
        if (viewType == 1) {
            View view = mInflate.inflate(R.layout.fragment_search, parent, false);
            return new AHolder(view);
        } else {
            View view = mInflate.inflate(R.layout.orgsimage, parent, false);
            return new BHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = itemList.get(position);
//        Orgs orgs = orgsList.get(position);
        if (holder instanceof AHolder) {
            userTextView(((AHolder) holder).login, ((AHolder)holder).score, position);
            Glide.with(holder.itemView.getContext())
                    .load(user.getAvatar_url())
                    .into(((AHolder) holder).userProfile);
        } else {
//            Glide.with(holder.itemView.getContext())
//                    .load(orgs.getAvatar_url())
//                    .into(((BHolder)holder).orgsProfile);
        }
//        LinearLayout selectLayout = holder.itemView.findViewById(R.id.layout);
//        selectLayout.setOnClickListener(v ->  mListener.OnItemClickListener(v, position, orgsUrl));
    }

    private void userTextView(TextView login, TextView score, int position){
        login.setText(itemList.get(position).getLogin());
        score.setText(String.valueOf(itemList.get(position).getScore()));
    }

    private void getOrgs(String s) {
        Apiservice apiservice = new RetrofitMaker().createService(context, Apiservice.class);
        Log.e("s", "s = " + s);
        Single<List<Orgs>> item = apiservice.getorgs(s);
        disposables.add(item
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Orgs>>() {
                    @Override
                    public void onSuccess(List<Orgs> orgs) {
                        if (orgs != null) {

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

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class AHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        CircleImageView userProfile;
        TextView login, score;
        public AHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            userProfile = itemView.findViewById(R.id.profile);
            login = itemView.findViewById(R.id.login);
            score = itemView.findViewById(R.id.score);

            layout.setOnClickListener(v -> {
                Log.e("position", "position = " + getAdapterPosition());
                orgsUrl = "users/" + login.getText().toString() + "/orgs";
                getOrgs("users/" + login.getText().toString() + "/orgs");
            });
        }
    }

   static public class BHolder extends RecyclerView.ViewHolder {
        CircleImageView orgsProfile;
        public BHolder(@NonNull View itemView) {
            super(itemView);
            orgsProfile = itemView.findViewById(R.id.orgsProfile);
        }
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
