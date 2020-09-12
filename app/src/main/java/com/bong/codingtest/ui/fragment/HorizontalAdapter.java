package com.bong.codingtest.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.horizontalViewHolder> {
    private Context context;
//    private List<Orgs> orgsList;
    private ArrayList<String> orgsList;
    private LayoutInflater mInflate;


    public HorizontalAdapter(ArrayList<String> orgs, Context context) {
        orgsList = orgs;
        this.context = context;
    }

    @NonNull
    @Override
    public HorizontalAdapter.horizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflate.inflate(R.layout.orgsimage, parent, false);
        return new horizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalAdapter.horizontalViewHolder holder, int position) {
        Glide.with(context)
                .load(orgsList.get(position))
                .into(holder.orgsProfile);
    }

    @Override
    public int getItemCount() {
        if (orgsList.size() != 0) {
            return orgsList.size();
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
