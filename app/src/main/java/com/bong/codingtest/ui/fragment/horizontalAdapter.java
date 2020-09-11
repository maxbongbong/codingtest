package com.bong.codingtest.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.Orgs;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class horizontalAdapter extends RecyclerView.Adapter<horizontalAdapter.horizontalViewHolder> {
    private Context context;
    private LayoutInflater mInflate;


    public horizontalAdapter(List<Orgs> orgs, Context context) {
        this.context = context;
        SearchFragment.orgsList = orgs;
    }

    @NonNull
    @Override
    public horizontalAdapter.horizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflate.inflate(R.layout.orgsimage, parent, false);
        return new horizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull horizontalAdapter.horizontalViewHolder holder, int position) {
//        Glide.with(context)
//                .load(SearchFragment.orgsList.get(position).getAvatar_url())
//                .into(holder.orgsProfile);
    }

    @Override
    public int getItemCount() {
        if (SearchFragment.orgsList.size() != 0) {
            return SearchFragment.orgsList.size();
        } else {
            return 3;
        }
    }

    public static class horizontalViewHolder extends RecyclerView.ViewHolder {
        CircleImageView orgsProfile;

        public horizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            orgsProfile = itemView.findViewById(R.id.orgsProfile);
        }
    }
}
