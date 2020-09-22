package com.bong.codingtest.ui.fragment;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.User;
import com.bong.codingtest.ui.main.MainActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<User> itemList;
    private OnItemClickListener mListener;
    private Context context;

    public interface OnItemClickListener {
        void ItemListener(View v, int position, String login);
    }

    public List<User> getItemList() {
        return itemList;
    }

    public SearchAdapter(Context context, List<User> items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.itemList = items;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = itemList.get(position);
        holder.login.setText(user.getLogin());
        holder.score.setText(String.valueOf(user.getScore()));
        Glide.with(holder.itemView.getContext())
                .load(user.getAvatar_url())
                .into(holder.userProfile);

        if (user.connectingToServer) {
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }

        if (user.orgList != null) {
            if (user.orgList.size() == 0) {
                holder.empty.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
                holder.recyclerView.setAdapter(null);
            } else {
                holder.empty.setVisibility(View.GONE);
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.recyclerView.setVisibility(View.VISIBLE);
                holder.recyclerView.setAdapter(new HorizontalAdapter(user.orgList, context));
            }
        } else {
            holder.empty.setVisibility(View.GONE);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setVisibility(View.GONE);
            holder.recyclerView.setAdapter(null);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        CircleImageView userProfile;
        TextView login, score, empty;
        RecyclerView recyclerView;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            userProfile = itemView.findViewById(R.id.profile);
            login = itemView.findViewById(R.id.login);
            score = itemView.findViewById(R.id.score);
            progressBar = itemView.findViewById(R.id.progress_horizontal);
            recyclerView = itemView.findViewById(R.id.orgsRecyclerView);
            empty = itemView.findViewById(R.id.empty);

            layout.setOnClickListener(v -> mListener.ItemListener(v, getAdapterPosition(), itemList.get(getAdapterPosition()).getLogin()));
        }
    }
}
