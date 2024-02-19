package com.icofound.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icofound.Class.Constant;
import com.icofound.Class.SocialMediaLinks;
import com.icofound.R;

import java.util.ArrayList;

public class SocialLinkShowAdapter extends RecyclerView.Adapter<SocialLinkShowAdapter.ExperienceViewHolder> {

    Activity activity;
    ArrayList<SocialMediaLinks> socialMedia = new ArrayList<>();

    public SocialLinkShowAdapter(Activity activity, ArrayList<SocialMediaLinks> socialMedias) {
        this.activity = activity;
        this.socialMedia.addAll(socialMedias);
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.social_link_show_list, parent, false);
        return new ExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {

        holder.tvName.setText(socialMedia.get(position).getName());

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.openUrlInBrowser(activity ,socialMedia.get(position).getSocialMediaLink());
            }
        });


    }

    @Override
    public int getItemCount() {
        return socialMedia.size();
    }

    public class ExperienceViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ExperienceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);

        }
    }


}
