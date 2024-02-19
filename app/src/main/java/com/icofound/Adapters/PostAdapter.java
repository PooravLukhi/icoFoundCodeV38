package com.icofound.Adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.icofound.Model.Post;
import com.icofound.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Activity activity;
    Post postlist;

    public PostAdapter(Activity activity, Post postlist) {
        this.activity = activity;
        this.postlist = postlist;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.multipostlayout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (postlist.getPostImgLink() != null || !TextUtils.isEmpty(postlist.getPostImgLink())){
            holder.tv_post_image.setVisibility(View.VISIBLE);
            Glide.with(activity).load(postlist.getPostImgLink()).into(holder.tv_post_image);
        } else {
            holder.tv_post_image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView tv_post_image;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_post_image = itemView.findViewById(R.id.tv_post_image);
        }
    }
}
