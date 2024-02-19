package com.icofound.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.icofound.Activity.ProfileActivity;
import com.icofound.Model.User;
import com.icofound.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ConnectionViewHolder> {

    Activity activity;
    ArrayList<User> connectionlist = new ArrayList<>();
    String loginuid;

    public ConnectionAdapter(FragmentActivity activity, ArrayList<User> connectionlist, String loginuid) {
        this.activity = activity;
        this.connectionlist = connectionlist;
        this.loginuid = loginuid;
    }

    @NonNull
    @Override
    public ConnectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connection_list, parent, false);
        ConnectionAdapter.ConnectionViewHolder viewHolder = new ConnectionAdapter.ConnectionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectionViewHolder holder, int position) {

        holder.connection_layout.setVisibility(View.VISIBLE);

        Glide.with(activity).load(connectionlist.get(position).getProfilePicLink()).placeholder(R.drawable.ic_person).into(holder.profile_pic);

        holder.name.setText(connectionlist.get(position).getName());

        holder.own_proffession_.setText(connectionlist.get(position).getMyProfession());

        holder.connection_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ProfileActivity.class);
                i.putExtra("frommain",false);
                i.putExtra("myprofile",false);
                i.putExtra("Uid",connectionlist.get(holder.getAdapterPosition()).getId());
                i.putExtra("loginuid", loginuid);
                activity.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return connectionlist.size();
    }

    public class ConnectionViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_pic;
        LinearLayout connection_layout;
        TextView own_proffession_,name;

        public ConnectionViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            connection_layout = itemView.findViewById(R.id.connection_layout);
            own_proffession_ = itemView.findViewById(R.id.own_proffession_);
            name = itemView.findViewById(R.id.name);
        }
    }
}
