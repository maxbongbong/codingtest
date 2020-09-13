package com.bong.codingtest.ui.fragment;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bong.codingtest.R;
import com.bong.codingtest.data.Org;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.horizontalViewHolder> {
    private Context context;
    private List<Org> orgsList;

    public HorizontalAdapter(List<Org> org, Context context) {
        orgsList = org;
        this.context = context;
    }

    @NonNull
    @Override
    public HorizontalAdapter.horizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.orgsimage, parent, false);
        return new horizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalAdapter.horizontalViewHolder holder, int position) {
        if (orgsList.get(position) != null) {
            holder.orgsProfile.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(orgsList.get(position).getAvatar_url())
                    .into(holder.orgsProfile);
        } else {
            holder.orgsProfile.setVisibility(View.GONE);
            toastMessage();
        }
    }

    @Override
    public int getItemCount() {
        return orgsList.size();
    }

    static class horizontalViewHolder extends RecyclerView.ViewHolder {
        CircleImageView orgsProfile;
        horizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            orgsProfile = itemView.findViewById(R.id.orgsProfile);
        }
    }
    private void toastMessage() {
        Toast toast = Toast.makeText(context, R.string.app_dataNull, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
