package com.bong.codingtest.ui.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.User;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> {
    private Context context;
    private List<User> itemList;
    private LayoutInflater mInflate;
    private OnItemClickListener mListener;
    static boolean setEnabled = true;

    public interface OnItemClickListener{
        void ItemListener(View v, int position, boolean setEnabled);
    }

    public SearchAdapter(Context context, List<User> items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.mInflate = LayoutInflater.from(context);
        this.itemList = items;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SearchAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflate.inflate(R.layout.fragment_search, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.Holder holder, int position) {
        horizontalAdapter horizontalAdapter = new horizontalAdapter(SearchFragment.orgsList, context);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(horizontalAdapter);
        User user = itemList.get(position);
        holder.login.setText(user.getLogin());
        holder.score.setText(String.valueOf(user.getScore()));
        Glide.with(holder.itemView.getContext())
                .load(user.getAvatar_url())
                .into(holder.userProfile);

        holder.layout.setOnClickListener(v -> {
            Log.e("setEnabled", "boolean = " + setEnabled);
            if (SearchAdapter.setEnabled) {
                holder.recyclerView.setVisibility(View.VISIBLE);
                SearchAdapter.setEnabled = false;
            } else if (!SearchAdapter.setEnabled) {
                holder.recyclerView.setVisibility(View.GONE);
                SearchAdapter.setEnabled = true;
            }
        });
//        holder.layout.setOnClickListener(v -> {
//            mListener.ItemListener(v, position, setEnabled);
//            Log.e("position", "adapter position = " + position);
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        CircleImageView userProfile;
        TextView login, score;
        RecyclerView recyclerView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            userProfile = itemView.findViewById(R.id.profile);
            login = itemView.findViewById(R.id.login);
            score = itemView.findViewById(R.id.score);
            recyclerView = itemView.findViewById(R.id.orgsRecyclerView);
        }
    }
}
