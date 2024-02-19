package com.icofound.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.icofound.Activity.CommentActivity;
import com.icofound.Activity.SendReportActivity;
import com.icofound.Model.Comment;
import com.icofound.Model.User;
import com.icofound.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    Activity activity;
    ArrayList<Comment> new_filter_commentList;
    User currentuser;
    User postuser;
    FirebaseFirestore firestore;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public CommentAdapter(CommentActivity commentActivity, ArrayList<Comment> new_filter_commentList, User currentuser, User postuser) {
        this.activity = commentActivity;
        this.new_filter_commentList = new_filter_commentList;
        this.currentuser = currentuser;
        this.postuser = postuser;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_layout,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(activity).load(new_filter_commentList.get(position).getSenderphoto()).placeholder(R.drawable.ic_person).into(holder.comment_user_img);
        holder.comment_user_name.setText(new_filter_commentList.get(position).getSendername());

            if (currentuser.getId().equalsIgnoreCase(postuser.getId())) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.report.setVisibility(View.GONE);
            } else {
                if (currentuser.getId().equalsIgnoreCase(new_filter_commentList.get(position).getSenderUId())){
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.report.setVisibility(View.GONE);
                }else {
                    holder.delete.setVisibility(View.GONE);
                    holder.report.setVisibility(View.VISIBLE);
                }
            }

        holder.comments.setText(new_filter_commentList.get(position).getComment());

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Calendar cal1 = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(new_filter_commentList.get(position).getTimestamp() * 1000L);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

        try {
            Date date1 = simpleDateFormat.parse(DateFormat.format("dd/M/yyyy hh:mm:ss", cal).toString());
            Date date2 = simpleDateFormat.parse(DateFormat.format("dd/M/yyyy hh:mm:ss", cal1).toString());

            holder.time.setText(printDifference(date1, date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.delete_post);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView cancel = dialog.findViewById(R.id.cancel);
                TextView delete = dialog.findViewById(R.id.delete);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ((CommentActivity) activity).deletecomment(new_filter_commentList.get(holder.getAdapterPosition()).getId());
                    }
                });

                dialog.show();
            }
        });

        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.custom_report);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView texts = dialog.findViewById(R.id.texts);
                texts.setText("Do you want to report this post?");

                TextView cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                TextView report = dialog.findViewById(R.id.report);
                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(activity, SendReportActivity.class);
                        i.putExtra("commentId",new_filter_commentList.get(holder.getAdapterPosition()).getId());
                        activity.startActivity(i);

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return new_filter_commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView comment_user_img;
        TextView comment_user_name,comments,time;
        ImageView report,delete;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            comment_user_img = itemView.findViewById(R.id.comment_user_img);
            comment_user_name = itemView.findViewById(R.id.comment_user_name);
            comments = itemView.findViewById(R.id.comments);
            time = itemView.findViewById(R.id.time);
            report = itemView.findViewById(R.id.report);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : "+ endDate);
//        System.out.println("different : " + different);


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        if (elapsedDays != 0){
            if (elapsedDays > 1){

                SimpleDateFormat spf=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
                Date newDate= null;
                try {
                    newDate = spf.parse(startDate.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf= new SimpleDateFormat("MMM dd yyyy");

                return spf.format(newDate);
            }else {
                return Math.abs(elapsedDays) + " day ago";
            }
        }else if (elapsedHours != 0){
            return Math.abs(elapsedHours) + " hour ago";
        }else if (elapsedMinutes != 0){
            return Math.abs(elapsedMinutes) + " min ago";
        }else if (elapsedSeconds != 0){
            return Math.abs(elapsedSeconds) + " sec ago";
        }else {
            return "0 sec ago";
        }
    }    public static String printDifference(long startDate) {
        final long diff = System.currentTimeMillis() - startDate;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
