package com.bong.codingtest.ui.fragment;

import android.content.Context;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private List<User> itemList;
    private LayoutInflater mInflate;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void ItemListener(View v, int position);
    }

    public SearchAdapter(Context context, List<User> items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.mInflate = LayoutInflater.from(context);
        this.itemList = items;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_search, parent, false);
        viewHolder = new ViewHolder(view);
        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        CircleImageView userProfile;
        TextView login, score;
        RecyclerView recyclerView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            userProfile = itemView.findViewById(R.id.profile);
            login = itemView.findViewById(R.id.login);
            score = itemView.findViewById(R.id.score);
            recyclerView = itemView.findViewById(R.id.orgsRecyclerView);

            layout.setOnClickListener(v -> {
//                int pos = getAdapterPosition();
//                if(pos != RecyclerView.NO_POSITION){
//                    if (mListener != null) {
//                        mListener.ItemListener(v, pos);
////                        itemList.set(pos, itemList.get(pos).setStatus(false));
//                        notifyItemChanged(pos);
//                    }
//                }
                if (itemList.get(getAdapterPosition()).isStatus()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    itemList.get(getAdapterPosition()).setStatus(false);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    itemList.get(getAdapterPosition()).setStatus(true);
                }
            });
        }
    }
}
