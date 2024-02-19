package com.icofound.Adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.icofound.Model.SkillData;
import com.icofound.R;

import java.util.List;

public class SkillAdp extends RecyclerView.Adapter<SkillAdp.ViewHolder> {
    private List<SkillData> listdata;
    OnItemClick onItemClick;

    // RecyclerView recyclerView;
    public SkillAdp(List<SkillData> listdata, OnItemClick onItemClicks) {
        this.listdata = listdata;
        onItemClick = onItemClicks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.skill_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SkillData skillData = listdata.get(position);

        if (listdata.get(position).getTitle().equalsIgnoreCase("")) {
            holder.layTitle.setVisibility(View.GONE);
            holder.laySubTitle.setVisibility(View.VISIBLE);
        } else {
            holder.layTitle.setVisibility(View.VISIBLE);
            holder.laySubTitle.setVisibility(View.GONE);
        }


        holder.tvTitle.setText(listdata.get(position).getTitle());
        holder.tvSubTitle.setText(listdata.get(position).getSubTitle());


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(view.getContext(), "click on item: " + skillData.getDescription(), Toast.LENGTH_LONG).show();

                onItemClick.onItemClick(v, position);

            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvSubTitle;
        public LinearLayout layTitle, laySubTitle, relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            layTitle = itemView.findViewById(R.id.layTitle);
            laySubTitle = itemView.findViewById(R.id.laySubTitle);
        }
    }

    public interface OnItemClick {
        void onItemClick(View view, int pos);
    }
}