package com.icofound.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icofound.Activity.ExperienceActivity;
import com.icofound.Class.Constant;
import com.icofound.Class.Experience;
import com.icofound.R;

import java.util.ArrayList;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder> {

    Activity activity;
    boolean isViewOnly = false;
    ArrayList<Experience> experiences;

    public ExperienceAdapter(Activity activity, ArrayList<Experience> experiences, boolean isViewOnly) {
        this.activity = activity;
        this.experiences = experiences;
        this.isViewOnly = isViewOnly;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.experience_list, parent, false);
        return new ExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {

        holder.organization_name.setText(experiences.get(position).getOrgName());
        holder.job_title.setText(experiences.get(position).getTitleName());


        Constant.readMoreTextView(holder.roles , experiences.get(position).getWorkDesc());



        String endDate = experiences.get(position).getEndDate();

        if (endDate == null)
            endDate = "";

        if ( endDate.equalsIgnoreCase(""))
            endDate = "Ongoing";



        String startDate = experiences.get(position).getStartDate();

        if (startDate == null || startDate.equalsIgnoreCase("null"))
            startDate = "";

        holder.tvDate.setText(startDate + " - " + endDate);




        holder.more.setOnClickListener(view -> {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.exp_update_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView edit = dialog.findViewById(R.id.edit);
            TextView delete = dialog.findViewById(R.id.delete);
            TextView cancel = dialog.findViewById(R.id.cancel);

            edit.setOnClickListener(view1 -> {
                dialog.dismiss();
                ((ExperienceActivity)activity).updateExperience(experiences.get(position), position);

            });

            delete.setOnClickListener(view1 -> {
                dialog.dismiss();

                int newPosition = holder.getAdapterPosition();

                showUpdateDialog("Delete","Are You Sure You Want To Delete?",newPosition);


            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        });


        if (isViewOnly)
            holder.more.setVisibility(View.GONE);
        else
            holder.more.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return experiences.size();
    }

    public class ExperienceViewHolder extends RecyclerView.ViewHolder {

        TextView organization_name, job_title, roles , tvDate;
        ImageView more;

        public ExperienceViewHolder(@NonNull View itemView) {
            super(itemView);

            organization_name = itemView.findViewById(R.id.organization_name);
            job_title = itemView.findViewById(R.id.job_title);
            tvDate = itemView.findViewById(R.id.tvDate);
            roles = itemView.findViewById(R.id.roles);
            more = itemView.findViewById(R.id.more);

        }
    }


    private void showUpdateDialog(String title , String msg , int newPosition) {

        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.profile_update);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        TextView ok = dialog.findViewById(R.id.ok);
        ok.setText("Cancel");

        TextView btnGotoProfile = dialog.findViewById(R.id.btnGotoProfile);
        btnGotoProfile.setText("Ok");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();



            }
        });


        btnGotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                experiences.remove(newPosition);
                ((ExperienceActivity)activity).deleteExperience(experiences);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, experiences.size());
            }
        });

        dialog.show();
    }
}
