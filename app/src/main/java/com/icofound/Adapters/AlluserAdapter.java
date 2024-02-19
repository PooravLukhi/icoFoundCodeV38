package com.icofound.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.icofound.Activity.AlluserActivity;
import com.icofound.Activity.MessageActivity;
import com.icofound.Model.User;
import com.icofound.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class AlluserAdapter extends RecyclerView.Adapter<AlluserAdapter.AlluserViewHolder> {

    Activity activity;
    ArrayList<User> new_userlist;
    String loginuid;

    public AlluserAdapter(AlluserActivity alluserActivity, ArrayList<User> new_userlist, String loginuid) {
        this.activity = alluserActivity;
        this.new_userlist = new_userlist;
        this.loginuid = loginuid;
    }

    @NonNull
    @Override
    public AlluserAdapter.AlluserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alluser_layout, parent, false);
        return new AlluserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlluserAdapter.AlluserViewHolder holder, int position) {

        Glide.with(activity).load(new_userlist.get(position).getProfilePicLink()).into(holder.imageView);
        holder.name.setText(new_userlist.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, MessageActivity.class);
                i.putExtra("loginuid",loginuid);
                i.putExtra("receiverid",new_userlist.get(holder.getAdapterPosition()).getId());
                activity.startActivity(i);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return new_userlist.size();
    }

    public class AlluserViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView imageView;
        TextView name;

        public AlluserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
        }
    }
}
