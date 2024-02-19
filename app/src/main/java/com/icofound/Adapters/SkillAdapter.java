package com.icofound.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.icofound.R;

import java.util.ArrayList;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder>  {

    Activity activity;
    ArrayList<String> skillList;

    public SkillAdapter(Activity activity, ArrayList<String> skillList) {

        this.activity = activity;
        this.skillList = skillList;

    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_layout_list, parent, false);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        holder.label.setText(skillList.get(position));
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

    public class SkillViewHolder extends RecyclerView.ViewHolder {

        TextView label;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.label);

        }
    }
}
