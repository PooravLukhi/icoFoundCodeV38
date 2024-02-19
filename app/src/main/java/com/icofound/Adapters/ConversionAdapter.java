package com.icofound.Adapters;

import static com.icofound.Adapters.CommentAdapter.printDifference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.icofound.Activity.MessageActivity;
import com.icofound.Fragments.HomeFragment;
import com.icofound.Model.Messages;
import com.icofound.Model.User;
import com.icofound.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder> {

    Activity activity;
    String loginuid, Uid;
    ArrayList<Messages> messageslist;
    String receiver_profile;

    public ConversionAdapter(MessageActivity messageActivity, String loginuid, String uid, ArrayList<Messages> messageslist, String receiver_profile) {

        this.activity = messageActivity;
        this.loginuid = loginuid;
        this.Uid = uid;
        this.messageslist = messageslist;
        this.receiver_profile = receiver_profile;
    }


    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_item, parent, false);
        return  new ConversionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, @SuppressLint("RecyclerView") int position) {



        if (messageslist.get(position).getContentType() == 0){
            holder.sending_img_layout.setVisibility(View.GONE);
            holder.receiving_img_layout.setVisibility(View.GONE);
            holder.mapLayoutSend.setVisibility(View.GONE);
            holder.mapLayoutReceive.setVisibility(View.GONE);

            if (messageslist.get(position).getOwnerID().equalsIgnoreCase(loginuid)){

                holder.sending_layout.setVisibility(View.VISIBLE);
                holder.receiving_layout.setVisibility(View.GONE);
                holder.send_message.setText(messageslist.get(position).getMessage());




                try {
                    holder.send_message_time.setText(printDifference(messageslist.get(position).getTimestamp()*1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    User user;
//                    cameraurl = user.getProfilePicLink();

                    Glide.with(activity).load(HomeFragment.user.getProfilePicLink()).placeholder(R.drawable.ic_person).into(holder.txt_profile_login);

                } catch (Exception e) {

                }



            }else if (messageslist.get(position).getOwnerID().equalsIgnoreCase(Uid)){
                holder.receiving_layout.setVisibility(View.VISIBLE);
                holder.sending_layout.setVisibility(View.GONE);
                holder.receive_message.setText(messageslist.get(position).getMessage());

                try {
                    holder.receive_message_time.setText(printDifference(messageslist.get(position).getTimestamp()*1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Glide.with(activity).load(receiver_profile).placeholder(R.drawable.ic_person).into(holder.txt_profile);
            }
        }
        else if (messageslist.get(position).getContentType() == 1){

            holder.sending_layout.setVisibility(View.GONE);
            holder.receiving_layout.setVisibility(View.GONE);
            holder.mapLayoutSend.setVisibility(View.GONE);
            holder.mapLayoutReceive.setVisibility(View.GONE);

            if (messageslist.get(position).getOwnerID().equalsIgnoreCase(loginuid)){

                holder.sending_img_layout.setVisibility(View.VISIBLE);
                holder.receiving_img_layout.setVisibility(View.GONE);
                Glide.with(activity).load(messageslist.get(position).getProfilePicLink()).into(holder.send_photo);


            }else if (messageslist.get(position).getOwnerID().equalsIgnoreCase(Uid)){
                holder.receiving_img_layout.setVisibility(View.VISIBLE);
                holder.sending_img_layout.setVisibility(View.GONE);
                Glide.with(activity).load(messageslist.get(position).getProfilePicLink()).into(holder.receive_photo);
                Glide.with(activity).load(receiver_profile).placeholder(R.drawable.ic_person).into(holder.img_profile);

            }
        }
        else if (messageslist.get(position).getContentType() == 2) {

            holder.sending_layout.setVisibility(View.GONE);
            holder.receiving_layout.setVisibility(View.GONE);
            holder.sending_img_layout.setVisibility(View.GONE);
            holder.receiving_img_layout.setVisibility(View.GONE);

            holder.mapLayoutReceive.setVisibility(View.GONE);

            if (messageslist.get(position).getOwnerID().equalsIgnoreCase(loginuid)) {

                holder.mapLayoutSend.setVisibility(View.VISIBLE);
                holder.mapLayoutReceive.setVisibility(View.GONE);
                Glide.with(activity).load(messageslist.get(position).getProfilePicLink()).into(holder.webViewSend);

            }
            else if (messageslist.get(position).getOwnerID().equalsIgnoreCase(Uid)) {

                holder.mapLayoutSend.setVisibility(View.GONE);
                holder.mapLayoutReceive.setVisibility(View.VISIBLE);
                Glide.with(activity).load(messageslist.get(position).getProfilePicLink()).into(holder.receive_map);
                Glide.with(activity).load(receiver_profile).placeholder(R.drawable.ic_person).into(holder.loc_profile);
            }

            holder.mapLayoutSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String content = messageslist.get(position).getContent();
                    String[]  latlong = content.split(":");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);


                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + latitude + "," + longitude));
                    i.setPackage("com.google.android.apps.maps");
                    v.getContext().startActivity(i);
                }
            });

            holder.mapLayoutReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String content = messageslist.get(position).getContent();
                    String[]  latlong = content.split(":");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + latitude + "," + longitude));
                    i.setPackage("com.google.android.apps.maps");
                    v.getContext().startActivity(i);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messageslist.size();
    }

    public class ConversionViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView send_photo, receive_photo, webViewSend, receive_map;
        CircleImageView txt_profile,txt_profile_login, img_profile, loc_profile;
        LinearLayout sending_layout,mapLayoutReceive,receiving_img_layout,receiving_layout,sending_img_layout,mapLayoutSend;
        TextView send_message , send_message_time,receive_message , receive_message_time;

        public ConversionViewHolder(@NonNull View itemView) {
            super(itemView);
            send_photo = itemView.findViewById(R.id.send_photo);
            receive_photo = itemView.findViewById(R.id.receive_photo);
            webViewSend = itemView.findViewById(R.id.webViewSend);
            receive_map = itemView.findViewById(R.id.receive_map);
            txt_profile = itemView.findViewById(R.id.txt_profile);
            txt_profile_login = itemView.findViewById(R.id.txt_profile_login);
            img_profile = itemView.findViewById(R.id.img_profile);
            loc_profile = itemView.findViewById(R.id.loc_profile);
            sending_layout = itemView.findViewById(R.id.sending_layout);
            send_message = itemView.findViewById(R.id.send_message);
            send_message_time = itemView.findViewById(R.id.send_message_time);
            mapLayoutReceive = itemView.findViewById(R.id.mapLayoutReceive);
            receiving_img_layout = itemView.findViewById(R.id.receiving_img_layout);
            receiving_layout = itemView.findViewById(R.id.receiving_layout);
            sending_img_layout = itemView.findViewById(R.id.sending_img_layout);
            mapLayoutSend = itemView.findViewById(R.id.mapLayoutSend);
            receive_message = itemView.findViewById(R.id.receive_message);
            receive_message_time = itemView.findViewById(R.id.receive_message_time);
        }
    }
}
