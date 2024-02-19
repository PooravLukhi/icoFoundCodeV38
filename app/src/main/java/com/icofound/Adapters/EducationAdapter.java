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

import com.icofound.Activity.EducationActivity;
import com.icofound.Class.Constant;
import com.icofound.Class.Education;
import com.icofound.R;

import java.util.ArrayList;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ExperienceViewHolder> {

    Activity activity;
    ArrayList<Education> educations;
    boolean isViewOnly;

    public EducationAdapter(Activity activity, ArrayList<Education> Educations, boolean isViewOnly) {
        this.activity = activity;
        this.educations = Educations;
        this.isViewOnly = isViewOnly;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.education_list, parent, false);
        return new ExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {

        holder.tvDegree.setText(educations.get(position).getDegree().trim());
        holder.tvCourseName.setText(educations.get(position).getCourseName().trim());


        Constant.readMoreTextView(holder.tvCourseDescription , educations.get(position).getCourseDescription().trim());
//        holder.tvCourseDescription.setText(educations.get(position).getCourseDescription());
        holder.tvCourseYear.setText( educations.get(position).getCourseYear().trim());
        holder.tvSchoolName.setText( educations.get(position).getSchoolName().trim());

        holder.more.setOnClickListener(view -> {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.exp_update_dialog);

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView edit = dialog.findViewById(R.id.edit);
            TextView delete = dialog.findViewById(R.id.delete);
            TextView cancel = dialog.findViewById(R.id.cancel);
            TextView tvTitle = dialog.findViewById(R.id.tvTitle);

            tvTitle.setText("Edit/Delete Educational Qualifications");


            edit.setOnClickListener(view1 -> {
                dialog.dismiss();
                ((EducationActivity) activity).updateEducation(educations.get(position), position);

            });

            delete.setOnClickListener(view1 -> {
                dialog.dismiss();

                int newPosition = holder.getAdapterPosition();

                educations.remove(newPosition);
                ((EducationActivity) activity).deleteEducation(educations);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, educations.size());

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
        return educations.size();
    }

    public class ExperienceViewHolder extends RecyclerView.ViewHolder {

        TextView tvDegree, tvCourseName, tvCourseDescription, tvCourseYear , tvSchoolName;
        ImageView more;

        public ExperienceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDegree = itemView.findViewById(R.id.tvDegree);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCourseDescription = itemView.findViewById(R.id.tvCourseDescription);
            tvCourseYear = itemView.findViewById(R.id.tvCourseYear);
            tvSchoolName = itemView.findViewById(R.id.tvSchoolName);
            more = itemView.findViewById(R.id.more);

        }
    }
}
