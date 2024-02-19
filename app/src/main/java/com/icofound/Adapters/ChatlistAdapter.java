package com.icofound.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.icofound.Activity.ChatActivity;
import com.icofound.Activity.MessageActivity;
import com.icofound.Model.Conversations;
import com.icofound.Model.User;
import com.icofound.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.ChatlistViewHolder> {

  Activity activity;
  ArrayList<Conversations> new_conversations_list;
  String loginuid;
  ArrayList<String> blockuserids;
  ArrayList<User> userlist;

  public ChatlistAdapter(ChatActivity chatActivity, ArrayList<Conversations> new_conversations_list, String loginuid, ArrayList<String> blockuserids, ArrayList<User> userlist) {

    this.activity = chatActivity;
    this.new_conversations_list = new_conversations_list;
    this.loginuid = loginuid;
    this.blockuserids = blockuserids;
    this.userlist = userlist;
  }

  @NonNull
  @Override
  public ChatlistAdapter.ChatlistViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list, parent, false);
    return new ChatlistViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ChatlistAdapter.ChatlistViewHolder holder, int position) {
    // getCount
    boolean isMatch = false;
    Map<String, Boolean> isread = new HashMap<>();
    isread = new_conversations_list.get(position).getIsRead();
    int count = 0;
    for (Map.Entry<String, Boolean> entry : isread.entrySet()) {

      if (entry.getKey().equalsIgnoreCase(loginuid)) {
        if (!entry.getValue()) {
          isMatch = true;
          break;
        }
      }
    }
    for (Map.Entry<String, Boolean> entry : isread.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(loginuid)) {
        if (!entry.getValue()) {
          count++;
        }
      }
    }
    holder.icUnread.setText(count+"");

    if (isMatch) {
      holder.icUnread.setVisibility(View.VISIBLE);
    } else {
      holder.icUnread.setVisibility(View.GONE);
    }

    ArrayList<String> userids = new ArrayList<>();

    userids = new_conversations_list.get(position).getUserIDs();
    ArrayList<User> new_userlist = new ArrayList<>();

    for (String id : userids) {

      if (!id.equalsIgnoreCase(loginuid)) {

        if (!blockuserids.contains(id)) {
          for (int i = 0; i < userlist.size(); i++) {
            if (id.equalsIgnoreCase(userlist.get(i).getId())) {
              new_userlist.add(userlist.get(i));
            }
          }
        }
      }
    }

    for (int i = 0; i < new_userlist.size(); i++) {

        if (!loginuid.equalsIgnoreCase(new_userlist.get(i).getId())){
            Glide.with(activity)
                    .load(new_userlist.get(i).getProfilePicLink())
                    .placeholder(R.drawable.ic_person)
                    .into(holder.profile_pic);

            holder.username.setText(new_userlist.get(i).getName());
            break;
        }
    }



    holder.last_msg.setText(new_conversations_list.get(position).getLastMessage());

    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(new_conversations_list.get(position).getTimestamp() * 1000L);

    holder.time.setText(DateFormat.format("hh:mm aa", cal).toString());

    holder.itemView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            ArrayList<String> userids = new ArrayList<>();

            userids = new_conversations_list.get(position).getUserIDs();
            ArrayList<User> new_userlist = new ArrayList<>();
            for (String id : userids) {

              if (!id.equalsIgnoreCase(loginuid)) {

                if (!blockuserids.contains(id)) {
                  for (int i = 0; i < userlist.size(); i++) {
                    if (id.equalsIgnoreCase(userlist.get(i).getId())) {
                      new_userlist.add(userlist.get(i));
                    }
                  }
                }
              }
            }

            String receiverId = null;

            for (int i = 0; i < new_userlist.size(); i++) {

              if (!loginuid.equalsIgnoreCase(new_userlist.get(i).getId())){
                receiverId = new_userlist.get(i).getId();
                break;
              }
            }

            Intent i = new Intent(activity, MessageActivity.class);
            i.putExtra("loginuid", loginuid);
            i.putExtra("receiverid", receiverId);
            activity.startActivity(i);
          }
        });
  }

  @Override
  public int getItemCount() {
    return new_conversations_list.size();
  }

  public class ChatlistViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profile_pic;
    LinearLayout message_layout;
    TextView username, last_msg, time;
    TextView icUnread;

    public ChatlistViewHolder(@NonNull View itemView) {
      super(itemView);
      profile_pic = itemView.findViewById(R.id.profile_pic);
      message_layout = itemView.findViewById(R.id.message_layout);
      username = itemView.findViewById(R.id.username);
      last_msg = itemView.findViewById(R.id.last_msg);
      time = itemView.findViewById(R.id.time);
      icUnread = itemView.findViewById(R.id.icUnread);
    }
  }
}
