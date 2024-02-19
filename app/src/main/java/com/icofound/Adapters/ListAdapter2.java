package com.icofound.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icofound.R;
import com.icofound.TinyDB;

import java.util.ArrayList;

public class ListAdapter2 extends RecyclerView.Adapter<ListAdapter2.ListViewHolder>{

    Activity activity;
    String[] skills;
    ArrayList<String> selectedskills = new ArrayList<>();
    TinyDB tinyDB;
    boolean fromfilter;

    public ListAdapter2(Activity requireActivity, String[] skills, TinyDB tinydb, boolean fromfilter) {
        this.activity = requireActivity;
        this.skills = skills;
        this.tinyDB = tinydb;
        this.fromfilter = fromfilter;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.skill_layout,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.skill_name.setText(skills[position]);

        if (fromfilter){
            selectedskills = tinyDB.getListString("filterskills");
        }else {
            selectedskills = tinyDB.getListString("selectedskills");
        }


        if (selectedskills.size() > 0){

            for (int i = 0; i < selectedskills.size(); i++) {

                if (selectedskills.get(i).equalsIgnoreCase(skills[position])){
                    holder.ic_check.setVisibility(View.VISIBLE);
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.ic_check.getVisibility() == View.VISIBLE){

                    holder.ic_check.setVisibility(View.INVISIBLE);

                    for (int i = 0; i < selectedskills.size(); i++) {
                        if (selectedskills.get(i).equalsIgnoreCase(skills[position])){
                            selectedskills.remove(i);
                        }
                    }

                }else if (holder.ic_check.getVisibility() == View.INVISIBLE){

                    holder.ic_check.setVisibility(View.VISIBLE);

                    selectedskills.add(skills[position]);
                }

                if (fromfilter){
                    tinyDB.putListString("filterskills",selectedskills);
                }else {
                    tinyDB.putListString("selectedskills",selectedskills);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return skills.length;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView skill_name;
        ImageView ic_check;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            skill_name = itemView.findViewById(R.id.skill_name);
            ic_check = itemView.findViewById(R.id.ic_check);
        }
    }
}
