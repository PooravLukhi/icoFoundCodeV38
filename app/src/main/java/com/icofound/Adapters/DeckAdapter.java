package com.icofound.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daprlabs.cardstack.SwipeDeck;
import com.icofound.Activity.ProfileActivity;
import com.icofound.Model.User;
import com.icofound.R;

import java.util.ArrayList;
import java.util.stream.Collectors;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DeckAdapter extends BaseAdapter {

    Activity mainScreenActivity;
    ArrayList<User> userlist = new ArrayList<>();
    ImageView tv_profile;
    TextView tv_name, tv_own_proffession, tv_location, tv_own_skills, professionPref, skillPref, txtTitle,txtBio;
    MaterialRatingBar tv_ratingBar;
    ProgressBar progress;
    SwipeDeck cardStack;
    LinearLayout cardlayout;
    String loginuid;
    LinearLayout professionLayout, skillLayout;


    public DeckAdapter(FragmentActivity mainScreenActivity, ArrayList<User> userlist, SwipeDeck cardStack, String loginuid) {
        this.mainScreenActivity = mainScreenActivity;
        this.userlist = userlist;
        this.cardStack = cardStack;
        this.loginuid = loginuid;
    }

    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // on below line we are inflating our layout.
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);

        tv_profile = convertView.findViewById(R.id.tv_profile);
        tv_name = convertView.findViewById(R.id.tv_name);
        tv_own_proffession = convertView.findViewById(R.id.tv_own_proffession);
        tv_location = convertView.findViewById(R.id.tv_location);
        tv_own_skills = convertView.findViewById(R.id.tv_own_skills);
        tv_ratingBar = convertView.findViewById(R.id.tv_ratingBar);
        progress = convertView.findViewById(R.id.progress);
        cardlayout = convertView.findViewById(R.id.cardlayout);
        skillPref = convertView.findViewById(R.id.skillPref);
        professionPref = convertView.findViewById(R.id.professionPref);
        skillLayout = convertView.findViewById(R.id.skillLayout);
        professionLayout = convertView.findViewById(R.id.professionLayout);
        txtTitle = convertView.findViewById(R.id.txtTitle);
        txtBio = convertView.findViewById(R.id.txtBio);


        Glide.with(mainScreenActivity).load(userlist.get(position).getProfilePicLink()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                return false;
            }
        }).placeholder(R.drawable.ic_person).into(tv_profile);

        if (userlist.get(position).getName() != null) {
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText(userlist.get(position).getName());
        }else {
            tv_name.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(userlist.get(position).getProfessionPreference().getSkillPref()) &&
                TextUtils.isEmpty(userlist.get(position).getProfessionPreference().getProfessionPref())) {
            txtTitle.setVisibility(View.GONE);
            skillLayout.setVisibility(View.GONE);
            professionLayout.setVisibility(View.GONE);
        }else {
            txtTitle.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(userlist.get(position).getProfessionPreference().getSkillPref())) {
                skillLayout.setVisibility(View.GONE);
            } else {
                skillLayout.setVisibility(View.VISIBLE);
                skillPref.setText(userlist.get(position).getProfessionPreference().getSkillPref());
            }

            if (TextUtils.isEmpty(userlist.get(position).getProfessionPreference().getProfessionPref())) {
                professionLayout.setVisibility(View.GONE);
            } else {
                professionLayout.setVisibility(View.VISIBLE);
                professionPref.setText(userlist.get(position).getProfessionPreference().getProfessionPref());
            }
        }



        if (userlist.get(position).getMyProfession() != null) {
            tv_own_proffession.setVisibility(View.VISIBLE);
            tv_own_proffession.setText(userlist.get(position).getMyProfession() + ", ");
        }else {
            tv_own_proffession.setVisibility(View.GONE);
        }


        if (userlist.get(position).getLocation() != null) {
            tv_location.setVisibility(View.VISIBLE);
            tv_location.setText(userlist.get(position).getLocation());
        }else {
            tv_location.setVisibility(View.GONE);
        }


        if (userlist.get(position).getMySkills() != null) {
            tv_own_skills.setVisibility(View.VISIBLE);
            tv_own_skills.setText(userlist.get(position).getMySkills().stream().map(Object::toString)
                    .collect(Collectors.joining(", ")));
        }else {
            tv_own_skills.setVisibility(View.GONE);
        }


        if (userlist.get(position).getRatingCount() != null) {

            if (userlist.get(position).getRatingCount() instanceof Float) {
                tv_ratingBar.setRating((Float) userlist.get(position).getRatingCount());
            } else if (userlist.get(position).getRatingCount() instanceof  Double) {
                double d = (Double) userlist.get(position).getRatingCount();
                tv_ratingBar.setRating((float) d);
            } else {
                tv_ratingBar.setRating((Long) userlist.get(position).getRatingCount());
            }

        }

        if (userlist.get(position).getBio() != null){
            txtBio.setVisibility(View.VISIBLE);
            txtBio.setText(userlist.get(position).getBio());
        }else {
            txtBio.setVisibility(View.GONE);
        }


        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mainScreenActivity, ProfileActivity.class);
                i.putExtra("frommain", false);
                i.putExtra("myprofile", false);
                i.putExtra("Uid", userlist.get(position).getId());
                i.putExtra("loginuid", loginuid);
                mainScreenActivity.startActivity(i);

            }
        });

        return convertView;
    }

    public void filterList( ArrayList<User> filteredlist) {
        userlist = filteredlist;
        notifyDataSetChanged();
    }
}
