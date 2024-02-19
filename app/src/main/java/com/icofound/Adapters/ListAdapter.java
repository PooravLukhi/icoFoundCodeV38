package com.icofound.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icofound.Model.SkillData;
import com.icofound.R;
import com.icofound.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    Activity activity;
    List<SkillData> skills;
    ArrayList<String> selectedskills = new ArrayList<>();
    String skillRequired = "";
    TinyDB tinyDB;
    boolean fromfilter;
    boolean isSkillPref;

    public ListAdapter(Activity requireActivity, List<SkillData> skills, TinyDB tinydb, boolean fromfilter, boolean isSkillPref) {
        this.activity = requireActivity;
        this.skills = skills;
        this.tinyDB = tinydb;
        this.fromfilter = fromfilter;
        this.isSkillPref = isSkillPref;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.skill_layout, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (skills.get(position).getTitle().equalsIgnoreCase("")) {
            holder.layMain.setBackgroundColor(activity.getColor(R.color.white));
            holder.skill_name.setText(skills.get(position).getSubTitle());
        } else {
            holder.skill_name.setText(skills.get(position).getTitle());
            holder.layMain.setBackgroundColor(activity.getColor(R.color.titleBg));
        }


        if (isSkillPref){
            skillRequired = tinyDB.getString("skillPref");

            if (skillRequired.equalsIgnoreCase(skills.get(position).getSubTitle())) {
                holder.ic_check.setVisibility(View.VISIBLE);
            }else{
                holder.ic_check.setVisibility(View.INVISIBLE);
            }

        }
        else {
            if (fromfilter) {
                selectedskills = tinyDB.getListString("filterskills");
            } else {
                selectedskills = tinyDB.getListString("selectedskills");
            }

            if (selectedskills.size() > 0) {

                for (int i = 0; i < selectedskills.size(); i++) {

                    if (selectedskills.get(i).equalsIgnoreCase(skills.get(position).getSubTitle())) {
                        holder.ic_check.setVisibility(View.VISIBLE);
                        break;
                    }else{
                        holder.ic_check.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSkillPref){
                    skillRequired = skills.get(position).getTitle();

                    if (holder.ic_check.getVisibility() == View.VISIBLE) {
                        holder.ic_check.setVisibility(View.INVISIBLE);
                        skillRequired = "";
                    }
                    else{
                        holder.ic_check.setVisibility(View.VISIBLE);
                        skillRequired = skills.get(position).getSubTitle();
                    }

                    tinyDB.putString("skillPref", skillRequired);


                    notifyDataSetChanged();

                }
                else{
                    if (holder.ic_check.getVisibility() == View.VISIBLE) {

                        holder.ic_check.setVisibility(View.INVISIBLE);

                        for (int i = 0; i < selectedskills.size(); i++) {
                            if (selectedskills.get(i).equalsIgnoreCase(skills.get(position).getSubTitle())) {
                                selectedskills.remove(i);
                            }
                        }

                    } else if (holder.ic_check.getVisibility() == View.INVISIBLE) {
                        if (skills.get(position).getTitle().equalsIgnoreCase("")) {
                            holder.ic_check.setVisibility(View.VISIBLE);

                            selectedskills.add(skills.get(position).getSubTitle());
                        }
                    }

                    if (fromfilter) {
                        tinyDB.putListString("filterskills", selectedskills);
                    } else {
                        tinyDB.putListString("selectedskills", selectedskills);
                    }

                }



            }
        });

    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView skill_name;
        ImageView ic_check;
        LinearLayout layMain;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            skill_name = itemView.findViewById(R.id.skill_name);
            ic_check = itemView.findViewById(R.id.ic_check);
            layMain = itemView.findViewById(R.id.layMain);
        }
    }
}
