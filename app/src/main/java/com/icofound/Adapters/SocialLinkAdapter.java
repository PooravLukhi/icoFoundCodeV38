package com.icofound.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icofound.Class.Constant;
import com.icofound.Class.SocialMediaLinks;
import com.icofound.R;

import java.util.ArrayList;

public class SocialLinkAdapter extends RecyclerView.Adapter<SocialLinkAdapter.ExperienceViewHolder> {

    Activity activity;
    boolean isShow;
    ArrayList<SocialMediaLinks> socialMedia = new ArrayList<>();
    ShowEditSocialMediaDialog showEditSocialMediaDialog;

    public SocialLinkAdapter(Activity activity, boolean isShow , ArrayList<SocialMediaLinks> socialMedias, ShowEditSocialMediaDialog showEditSocialMediaDialogg) {
        this.activity = activity;
        this.isShow = isShow;
        this.socialMedia.addAll(socialMedias);
        this.socialMedia.add(new SocialMediaLinks("", "", ""));
        showEditSocialMediaDialog = showEditSocialMediaDialogg;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.social_link_list, parent, false);
        return new ExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {


        if (isShow){
            holder.imgSymbol.setImageResource(R.drawable.ic_link);
        }
        else{
            holder.imgSymbol.setImageResource(R.drawable.ic_pencil);

        }


        if (socialMedia.get(position).getId().equalsIgnoreCase("")) {
            holder.layAddLink.setVisibility(View.VISIBLE);
            holder.layMain.setVisibility(View.GONE);

            holder.layAddLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditSocialMediaDialog.onAddLink();
                }
            });

        } else {
            holder.layAddLink.setVisibility(View.GONE);
            holder.layMain.setVisibility(View.VISIBLE);
            holder.tvName.setText(socialMedia.get(position).getName());


            holder.layMain.setOnClickListener(view -> {


                if (isShow)
                    Constant.openUrlInBrowser(activity,socialMedia.get(position).getSocialMediaLink());
                else {
                    Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.exp_update_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setCancelable(false);


                    TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                    tvTitle.setText("Edit/Delete Media Links");

                    TextView edit = dialog.findViewById(R.id.edit);
                    TextView delete = dialog.findViewById(R.id.delete);
                    TextView cancel = dialog.findViewById(R.id.cancel);

                    edit.setOnClickListener(view1 -> {
                        dialog.dismiss();
                        showEditSocialMediaDialog.onOpenDialog(socialMedia.get(position), position, "edit");

                    });

                    delete.setOnClickListener(view1 -> {
                        dialog.dismiss();

                        int newPosition = holder.getAdapterPosition();

                        showEditSocialMediaDialog.onOpenDialog(socialMedia.get(position), newPosition, "delete");


                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return socialMedia.size();
    }

    public class ExperienceViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView imgSymbol;
        LinearLayout layMain, layAddLink;

        public ExperienceViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSymbol = itemView.findViewById(R.id.imgSymbol);
            tvName = itemView.findViewById(R.id.tvName);
            layMain = itemView.findViewById(R.id.layMain);
            layAddLink = itemView.findViewById(R.id.layAddLink);

        }
    }

    public interface ShowEditSocialMediaDialog {
        void onOpenDialog(SocialMediaLinks socialMediaLinks, int pos, String type);

        void onAddLink();
    }


}
