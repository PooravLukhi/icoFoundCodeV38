package com.icofound.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.icofound.Adapters.ChatlistAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.Utils;
import com.icofound.Model.Conversations;
import com.icofound.Model.User;
import com.icofound.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    LinearLayout nochat, chat_ui;
    TextView title;
    ImageView back, alluser;
    RoundedImageView profile;
    RecyclerView chat_list;
    User user;
    String loginuid;
    FirebaseFirestore firestore;
    List<DocumentSnapshot> marray = new ArrayList<>();
    ArrayList<Conversations> conversations_list = new ArrayList<>();
    ArrayList<Conversations> new_conversations_list = new ArrayList<>();
    ArrayList<User> userlist = new ArrayList<>();
    ArrayList<User> new_userlist = new ArrayList<>();
    ArrayList<String> blockuserids = new ArrayList<>();
    Utils utils;
    ListenerRegistration registration;

    public boolean isUnread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        utils = new Utils(ChatActivity.this);
        utils.intialise();

        chat_ui = findViewById(R.id.chat_ui);
        nochat = findViewById(R.id.nochat);
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        alluser = findViewById(R.id.alluser);
        profile = findViewById(R.id.profile);
        chat_list = findViewById(R.id.chat_list);
        firestore = FirebaseFirestore.getInstance();

        user = (User) getIntent().getSerializableExtra("user");
        loginuid = getIntent().getStringExtra("loginuid");

        if (user != null) {
            if (user.getBlockedUserIds() != null && user.getBlockedUserIds().size() > 0) {
                blockuserids = user.getBlockedUserIds();
            }

            Glide.with(ChatActivity.this).load(user.getProfilePicLink()).placeholder(R.drawable.ic_person).into(profile);

            getMessages();
        }

        nochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ChatActivity.this, AlluserActivity.class);
                i.putExtra("user", user);
                i.putExtra("loginuid", loginuid);
                i.putExtra("userList", userlist);
                startActivity(i);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, ProfileActivity.class);
                i.putExtra("frommain", false);
                i.putExtra("myprofile", true);
                i.putExtra("Uid", user.getId());
                startActivity(i);
            }
        });

        alluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, AlluserActivity.class);
                i.putExtra("user", user);
                i.putExtra("loginuid", loginuid);
                i.putExtra("userList", userlist);
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextPaint paint = title.getPaint();
        float width = paint.measureText("Settings");

        Shader textShader = new LinearGradient(0, 0, width, title.getTextSize(), new int[]{Color.parseColor("#9E73FE"), Color.parseColor("#838DFE"), Color.parseColor("#9E73FE"),}, null, Shader.TileMode.CLAMP);
        title.getPaint().setShader(textShader);
    }

    private void getMessages() {

        CollectionReference reference = firestore.collection(Constant.conversations);

        registration = reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                marray = new ArrayList<>();
                conversations_list = new ArrayList<>();
                new_conversations_list = new ArrayList<>();
                userlist = new ArrayList<>();
                new_userlist = new ArrayList<>();

                if (value.getDocuments() != null) {
                    marray = value.getDocuments();

                    for (int i = 0; i < marray.size(); i++) {
                        Conversations conversations = marray.get(i).toObject(Conversations.class);
                        conversations_list.add(conversations);
                    }

                    for (int i = 0; i < conversations_list.size(); i++) {

                        ArrayList<String> userids = new ArrayList<>();

                        userids = conversations_list.get(i).getUserIDs();

                        if (userids.contains(loginuid)) {
                            new_conversations_list.add(conversations_list.get(i));
                        }
                    }

                    firestore.collection(Constant.tableUsers).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments() != null) {

                                List<DocumentSnapshot> mArraylist = queryDocumentSnapshots.getDocuments();
                                for (int i = 0; i < mArraylist.size(); i++) {
                                    User user = new User();

                                    if (mArraylist.get(i).get(Constant.id) != null) {
                                        user.setId((String) mArraylist.get(i).get(Constant.id));
                                    }

                                    if (mArraylist.get(i).get(Constant.name) != null) {
                                        user.setName((String) mArraylist.get(i).get(Constant.name));
                                    }

                                    if (mArraylist.get(i).get(Constant.bio) != null) {
                                        user.setBio((String) mArraylist.get(i).get(Constant.bio));
                                    }

                                    if (mArraylist.get(i).get(Constant.bizValues) != null) {
                                        user.setBizValues((String) mArraylist.get(i).get(Constant.bizValues));
                                    }

                                    if (mArraylist.get(i).get(Constant.blockedUserIds) != null) {
                                        user.setBlockedUserIds((ArrayList<String>) mArraylist.get(i).get(Constant.blockedUserIds));
                                    }

                                    if (mArraylist.get(i).get(Constant.email) != null) {
                                        user.setEmail((String) mArraylist.get(i).get(Constant.email));
                                    }

                                    if (mArraylist.get(i).get(Constant.profilePicLink) != null) {
                                        user.setProfilePicLink((String) mArraylist.get(i).get(Constant.profilePicLink));
                                    }

                                    if (mArraylist.get(i).get(Constant.gender) != null) {
                                        user.setGender((String) mArraylist.get(i).get(Constant.gender));
                                    }

                                    if (mArraylist.get(i).get(Constant.showAge) != null) {
                                        user.setShowAge((boolean) mArraylist.get(i).get(Constant.showAge));
                                    }

                                    if (mArraylist.get(i).get(Constant.zipcode) != null) {
                                        user.setZipcode((String) mArraylist.get(i).get(Constant.zipcode));
                                    }

                                    if (mArraylist.get(i).get(Constant.location) != null) {
                                        user.setLocation((String) mArraylist.get(i).get(Constant.location));
                                    }

                                    if (mArraylist.get(i).get(Constant.ratingCount) != null) {
                                        if (mArraylist.get(i).get(Constant.ratingCount) instanceof Float) {
                                            user.setRatingCount((Float) mArraylist.get(i).get(Constant.ratingCount));

                                        }
                                        else if (mArraylist.get(i).get(Constant.ratingCount) instanceof Double) {
                                            user.setRatingCount((Double) mArraylist.get(i).get(Constant.ratingCount));

                                        }
                                        else {
                                            user.setRatingCount((Long) mArraylist.get(i).get(Constant.ratingCount));
                                        }
                                    }

                                    user.setMyProfession((String) mArraylist.get(i).get(Constant.myProfession));
                                    user.setSkillPreference((String) mArraylist.get(i).get(Constant.skillPreference));

                                    if (mArraylist.get(i).get(Constant.tableExperience) != null) {
                                        if (mArraylist.get(i).get(Constant.tableExperience) instanceof String) {

                                            ArrayList<Experience> experienceArrayList = new ArrayList<>();
                                            Experience experience = new Experience();
                                            experience.setWorkDesc((String) mArraylist.get(i).get(Constant.tableExperience));
                                            experience.setTitleName("");
                                            experience.setId("");
                                            experience.setOrgName("");
                                            experience.setStartDate("");
                                            experience.setEndDate("");
                                            experience.setCurrentWorkPlace(false);

                                            experienceArrayList.add(experience);

                                            user.setExperience(experienceArrayList);

                                        }
                                        else {
                                            user.setExperience((ArrayList<Experience>) mArraylist.get(i).get(Constant.tableExperience));
                                        }
                                    }

                                    if (mArraylist.get(i).get(Constant.tableProfessionPreference) != null) {
                                        if (mArraylist.get(i).get(Constant.tableProfessionPreference) instanceof String) {

                                            ProfessionPreference professionPreference = new ProfessionPreference();
                                            professionPreference.setProfessionPref((String) mArraylist.get(i).get(Constant.tableProfessionPreference));
                                            professionPreference.setSkillPref("");
                                            professionPreference.setId("");

                                            user.setProfessionPreference(professionPreference);
                                        }
                                        else {

                                            HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableProfessionPreference);

                                            ProfessionPreference professionPreference = new ProfessionPreference();

                                            for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                                String key = entry.getKey();
                                                String value1 = entry.getValue();

                                                if (key.equalsIgnoreCase(Constant.id)) {
                                                    professionPreference.setId(value1);
                                                }
                                                else if (key.equalsIgnoreCase(Constant.professionPref)) {
                                                    professionPreference.setProfessionPref(value1);
                                                }
                                                else if (key.equalsIgnoreCase(Constant.skillPref)) {
                                                    professionPreference.setSkillPref(value1);
                                                }
                                            }
                                            user.setProfessionPreference(professionPreference);
                                        }
                                    }

                                    if (mArraylist.get(i).get(Constant.tableCoreValues) != null) {
                                        if (mArraylist.get(i).get(Constant.tableCoreValues) instanceof String) {

                                            CoreValues coreValues = new CoreValues();
                                            coreValues.setId("");
                                            coreValues.setWorkValues("");
                                            coreValues.setWorkValues("");

                                            user.setCoreValues(coreValues);

                                        }
                                        else {
                                            HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableCoreValues);

                                            CoreValues coreValues = new CoreValues();

                                            for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                                String key = entry.getKey();
                                                String value1 = entry.getValue();

                                                if (key.equalsIgnoreCase(Constant.id)) {
                                                    coreValues.setId(value1);
                                                }
                                                else if (key.equalsIgnoreCase(Constant.inspiration)) {
                                                    coreValues.setInspiration(value1);
                                                }
                                                else if (key.equalsIgnoreCase(Constant.workCulture)) {
                                                    coreValues.setWorkCulture(value1);
                                                }
                                                else if (key.equalsIgnoreCase(Constant.workValues)) {
                                                    coreValues.setWorkValues(value1);
                                                }
                                            }
                                            user.setCoreValues(coreValues);
                                        }
                                    }

                                    if (mArraylist.get(i).get(Constant.mySkills) != null) {
                                        user.setMySkills((ArrayList<String>) mArraylist.get(i).get(Constant.mySkills));
                                    }

                                    if (mArraylist.get(i).get(Constant.inspire) != null) {
                                        user.setInspire((String) mArraylist.get(i).get(Constant.inspire));
                                    }

                                    if (mArraylist.get(i).get(Constant.values) != null) {
                                        user.setValues((String) mArraylist.get(i).get(Constant.values));
                                    }

                                    if (mArraylist.get(i).get(Constant.policyTermsAckDate) != null) {
                                        if (mArraylist.get(i).get(Constant.policyTermsAckDate) instanceof Double) {
                                            user.setPolicyTermsAckDate((Double) mArraylist.get(i).get(Constant.policyTermsAckDate));
                                        }
                                        else {
                                            user.setPolicyTermsAckDate(Double.valueOf((Long) mArraylist.get(i).get(Constant.policyTermsAckDate)));
                                        }
                                    }

                                    if (mArraylist.get(i).get(Constant.token) != null) {
                                        user.setToken((String) mArraylist.get(i).get(Constant.token));
                                    }

                                    userlist.add(user);
                                }

                                if (new_conversations_list.size() > 0) {
                                    nochat.setVisibility(View.GONE);
                                    chat_ui.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < new_conversations_list.size(); i++) {
                                        ArrayList<String> userids = new ArrayList<>();

                                        userids = new_conversations_list.get(i).getUserIDs();
                                        for (String id : userids) {

                                            if (!id.equalsIgnoreCase(loginuid)) {

                                                if (blockuserids.contains(id)) {
                                                    new_conversations_list.remove(i);
                                                }
                                            }
                                        }
                                    }


                                    if (new_conversations_list.size() > 0) {
                                        ChatlistAdapter adapter = new ChatlistAdapter(ChatActivity.this, new_conversations_list, loginuid, blockuserids, userlist);
                                        chat_list.setAdapter(adapter);
                                    }

                                }
                                else {
                                    nochat.setVisibility(View.VISIBLE);
                                    chat_ui.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }
}