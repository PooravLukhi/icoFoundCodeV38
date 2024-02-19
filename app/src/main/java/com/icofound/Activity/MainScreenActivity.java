package com.icofound.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.Utils;
import com.icofound.Fragments.HomeFragment;
import com.icofound.Fragments.ProfileFragment;
import com.icofound.Model.User;
import com.icofound.Fragments.NetworkFragment;
import com.icofound.Notification.NotificationType;
import com.icofound.R;
import com.icofound.Fragments.StoriesFragment;
import com.icofound.TinyDB;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainScreenActivity extends AppCompatActivity {
    boolean isEditProfile = false;

    BottomNavigationView bottomNavigationView;
    ImageView profile;
    public static DrawerLayout drawer;
    TextView home, setting, view_profile, logout, username, contactus;
    String loginuid;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    User user1;
    RoundedImageView imageView;
    ProgressBar progress;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TinyDB tinydb;
    String token;
    public BottomNavigationItemView itemView;
    public BottomNavigationItemView itemView1;
    CircleImageView profile_badge_pic;
    CircleImageView buisness_profile_badge_pic;
    public static ArrayList<User> userList = new ArrayList<>();
    List<DocumentSnapshot> mArraylist = new ArrayList<>();
    public ListenerRegistration registration1, registration5, registration6, registration8;

    Utils utils;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        utils = new Utils(MainScreenActivity.this);
        utils.intialise();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        profile = findViewById(R.id.profile);
        drawer = findViewById(R.id.drawer);
        home = findViewById(R.id.nv_home);
        setting = findViewById(R.id.setting);
        contactus = findViewById(R.id.contactus);
        view_profile = findViewById(R.id.view_profile);
        logout = findViewById(R.id.logout);
        username = findViewById(R.id.username);
        imageView = findViewById(R.id.imageView);
        progress = findViewById(R.id.progress);

        firestore = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("filter", MODE_PRIVATE);
        editor = preferences.edit();
        tinydb = new TinyDB(MainScreenActivity.this);

        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(3);
        itemView1 = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(0);
        View orderView = LayoutInflater.from(this).inflate(R.layout.profile_badge, itemView, true);
        profile_badge_pic = orderView.findViewById(R.id.profile_badge_pic);
        Glide.with(MainScreenActivity.this).load(R.drawable.ic_person).into(profile_badge_pic);
        View orderView1 = LayoutInflater.from(this).inflate(R.layout.buisness_profile_badge, itemView1, true);
        buisness_profile_badge_pic = orderView1.findViewById(R.id.profile_badge_pic);

        progressDialog = new ProgressDialog(MainScreenActivity.this);

        getAllUserlist();

        loginuid = getIntent().getStringExtra("loginuid");

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                // Get new FCM registration token
                token = task.getResult();

                System.out.println("...................Token........." + token);

                firestore.collection(Constant.tableUsers).document(loginuid).update(Constant.token, token);

            }
        });

        firestore.collection(Constant.tableUsers).document(loginuid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assert documentSnapshot != null;
                if (documentSnapshot.getData() != null) {
                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    User user = new User();

                    if (mArraylist.get(Constant.id) != null) {
                        user.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get(Constant.name) != null) {
                        user.setName((String) mArraylist.get(Constant.name));
                    }

                    if (mArraylist.get(Constant.bio) != null) {
                        user.setBio((String) mArraylist.get(Constant.bio));
                    }

                    if (mArraylist.get(Constant.bizValues) != null) {
                        user.setBizValues((String) mArraylist.get(Constant.bizValues));
                    }

                    if (mArraylist.get(Constant.blockedUserIds) != null) {
                        user.setBlockedUserIds((ArrayList<String>) mArraylist.get(Constant.blockedUserIds));
                    }

                    if (mArraylist.get(Constant.email) != null) {
                        user.setEmail((String) mArraylist.get(Constant.email));
                    }

                    if (mArraylist.get(Constant.profilePicLink) != null) {
                        user.setProfilePicLink((String) mArraylist.get(Constant.profilePicLink));
                    }

                    if (mArraylist.get(Constant.gender) != null) {
                        user.setGender((String) mArraylist.get(Constant.gender));
                    }

                    if (mArraylist.get(Constant.showAge) != null) {
                        user.setShowAge((boolean) mArraylist.get(Constant.showAge));
                    }

                    if (mArraylist.get(Constant.zipcode) != null) {
                        user.setZipcode((String) mArraylist.get(Constant.zipcode));
                    }

                    if (mArraylist.get(Constant.location) != null) {
                        user.setLocation((String) mArraylist.get(Constant.location));
                    }

                    if (mArraylist.get(Constant.ratingCount) != null) {
                        if (mArraylist.get(Constant.ratingCount) instanceof Float) {
                            user.setRatingCount((Float) mArraylist.get(Constant.ratingCount));

                        } else if (mArraylist.get(Constant.ratingCount) instanceof Double) {
                            user.setRatingCount((Double) mArraylist.get(Constant.ratingCount));
                        } else {
                            user.setRatingCount((Long) mArraylist.get(Constant.ratingCount));
                        }
                    }


                    user.setMyProfession((String) mArraylist.get(Constant.myProfession));
                    user.setSkillPreference((String) mArraylist.get(Constant.skillPreference));

                    if (mArraylist.get(Constant.tableExperience) != null) {
                        if (mArraylist.get(Constant.tableExperience) instanceof String) {

                            ArrayList<Experience> experienceArrayList = new ArrayList<>();
                            Experience experience = new Experience();
                            experience.setWorkDesc((String) mArraylist.get(Constant.tableExperience));
                            experience.setTitleName("");
                            experience.setId("");
                            experience.setOrgName("");
                            experience.setStartDate("");
                            experience.setEndDate("");
                            experience.setCurrentWorkPlace(false);

                            experienceArrayList.add(experience);

                            user.setExperience(experienceArrayList);


                        } else {

                            ArrayList<HashMap<String, String>> array = (ArrayList<HashMap<String, String>>) mArraylist.get(Constant.tableExperience);
                            ArrayList<Experience> experienceArrayList = new ArrayList<>();

                            for (int i = 0; i < array.size(); i++) {
                                HashMap<String, String> hashmap = array.get(i);

                                Experience experience = new Experience();

                                for (Map.Entry<String, String> entry : hashmap.entrySet()) {
                                    String key = entry.getKey();
                                    String value1 = "";
                                    boolean value2 = false;
                                    try {
                                        value1 = entry.getValue();
                                    } catch (Exception e) {

                                    }
                                    if (key.equalsIgnoreCase(Constant.id)) {
                                        experience.setId(value1);
                                    } else if (key.equalsIgnoreCase(Constant.orgName)) {
                                        experience.setOrgName(value1);
                                    } else if (key.equalsIgnoreCase(Constant.titleName)) {
                                        experience.setTitleName(value1);
                                    } else if (key.equalsIgnoreCase(Constant.workDesc)) {
                                        experience.setWorkDesc(value1);
                                    } else if (key.equalsIgnoreCase(Constant.startDate)) {
                                        experience.setStartDate(value1);
                                    } else if (key.equalsIgnoreCase(Constant.endDate)) {
                                        experience.setEndDate(value1);
                                    } else if (key.equalsIgnoreCase(Constant.isCurrentWorkPlace)) {
                                        experience.setCurrentWorkPlace(value2);
                                    }
                                }

                                experienceArrayList.add(experience);
                            }


                            user.setExperience(experienceArrayList);
                        }

                    }

                    if (mArraylist.get(Constant.tableProfessionPreference) != null) {
                        if (mArraylist.get(Constant.tableProfessionPreference) instanceof String) {

                            ProfessionPreference professionPreference = new ProfessionPreference();
                            professionPreference.setProfessionPref((String) mArraylist.get(Constant.tableProfessionPreference));
                            professionPreference.setSkillPref("");
                            professionPreference.setId("");

                            user.setProfessionPreference(professionPreference);
                        } else {

                            HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(Constant.tableProfessionPreference);

                            ProfessionPreference professionPreference = new ProfessionPreference();

                            for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                String key = entry.getKey();
                                String value1 = entry.getValue();

                                if (key.equalsIgnoreCase(Constant.id)) {
                                    professionPreference.setId(value1);
                                } else if (key.equalsIgnoreCase(Constant.professionPref)) {
                                    professionPreference.setProfessionPref(value1);
                                } else if (key.equalsIgnoreCase(Constant.skillPref)) {
                                    professionPreference.setSkillPref(value1);
                                }

                            }
                            user.setProfessionPreference(professionPreference);
                        }
                    }

                    if (mArraylist.get(Constant.tableCoreValues) != null) {
                        if (mArraylist.get(Constant.tableCoreValues) instanceof String) {
                            CoreValues coreValues = new CoreValues();
                            coreValues.setId("");
                            coreValues.setWorkValues("");
                            coreValues.setWorkValues("");

                            user.setCoreValues(coreValues);

                        } else {

                            HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(Constant.tableCoreValues);

                            CoreValues coreValues = new CoreValues();

                            for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                String key = entry.getKey();
                                String value1 = entry.getValue();

                                if (key.equalsIgnoreCase(Constant.id)) {
                                    coreValues.setId(value1);
                                } else if (key.equalsIgnoreCase(Constant.inspiration)) {
                                    coreValues.setInspiration(value1);
                                } else if (key.equalsIgnoreCase(Constant.workCulture)) {
                                    coreValues.setWorkCulture(value1);
                                } else if (key.equalsIgnoreCase(Constant.workValues)) {
                                    coreValues.setWorkValues(value1);
                                }

                            }

                            user.setCoreValues(coreValues);

                        }
                    }

                    if (mArraylist.get(Constant.mySkills) != null) {
                        user.setMySkills((ArrayList<String>) mArraylist.get(Constant.mySkills));
                    }

                    if (mArraylist.get(Constant.inspire) != null) {
                        user.setInspire((String) mArraylist.get(Constant.inspire));
                    }

                    if (mArraylist.get(Constant.values) != null) {
                        user.setValues((String) mArraylist.get(Constant.values));
                    }


                    if (mArraylist.get(Constant.policyTermsAckDate) != null) {
                        if (mArraylist.get(Constant.policyTermsAckDate) instanceof Double) {
                            user.setPolicyTermsAckDate((Double) mArraylist.get(Constant.policyTermsAckDate));
                        } else {
                            user.setPolicyTermsAckDate(Double.valueOf((Long) mArraylist.get(Constant.policyTermsAckDate)));
                        }

                    }

                    if (mArraylist.get(Constant.token) != null) {
                        user.setToken((String) mArraylist.get(Constant.token));
                    }

                    username.setText(user.getName());
                    Glide.with(MainScreenActivity.this).load(user.getProfilePicLink()).listener(new RequestListener<Drawable>() {
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
                    }).placeholder(R.drawable.ic_person).into(imageView);

                    Glide.with(MainScreenActivity.this).load(user.getProfilePicLink()).placeholder(R.drawable.ic_person).into(profile_badge_pic);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainScreenActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent i = new Intent(MainScreenActivity.this, ProfileActivity.class);
                i.putExtra("frommain", true);
                i.putExtra("myprofile", false);
                i.putExtra("Uid", loginuid);
                startActivity(i);
                finish();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                bottomNavigationView.setSelectedItemId(R.id.home);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainScreenActivity.this, SettingActivity.class);
                intent.putExtra("user", user1);
                startActivity(intent);
                finish();
            }
        });

        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent i = new Intent(MainScreenActivity.this, ContactUsActivity.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    profile_badge_pic.setBorderWidth(0);

                    FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(buisness_profile_badge_pic.getLayoutParams());
                    params1.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(params1);
                    params.setMargins(0, 0, 0, 18);
                    buisness_profile_badge_pic.setLayoutParams(params);

                    FrameLayout.LayoutParams params14 = new FrameLayout.LayoutParams(profile_badge_pic.getLayoutParams());
                    params14.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params15 = new FrameLayout.LayoutParams(params14);
                    params15.setMargins(0, 0, 0, 0);
                    profile_badge_pic.setLayoutParams(params15);


                    HomeFragment fragment = new HomeFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("loginuid", loginuid);
                    fragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().replace(R.id.linear, fragment, "HomeFragment").commitAllowingStateLoss();

                    return true;

                case R.id.network:
                    profile_badge_pic.setBorderWidth(0);

                    FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(buisness_profile_badge_pic.getLayoutParams());
                    params2.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(params2);
                    params3.setMargins(0, 0, 0, 0);
                    buisness_profile_badge_pic.setLayoutParams(params3);

                    FrameLayout.LayoutParams params12 = new FrameLayout.LayoutParams(profile_badge_pic.getLayoutParams());
                    params12.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params13 = new FrameLayout.LayoutParams(params12);
                    params13.setMargins(0, 0, 0, 0);
                    profile_badge_pic.setLayoutParams(params13);

                    NetworkFragment fragment1 = new NetworkFragment();

                    Bundle bundle1 = new Bundle();
                    bundle1.putString("loginuid", loginuid);
                    fragment1.setArguments(bundle1);

                    getSupportFragmentManager().beginTransaction().replace(R.id.linear, fragment1, "NetworkFragment").commit();
                    return true;

                case R.id.stories:
                    profile_badge_pic.setBorderWidth(0);

                    FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(buisness_profile_badge_pic.getLayoutParams());
                    params4.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params5 = new FrameLayout.LayoutParams(params4);
                    params5.setMargins(0, 0, 0, 0);
                    buisness_profile_badge_pic.setLayoutParams(params5);

                    FrameLayout.LayoutParams params10 = new FrameLayout.LayoutParams(profile_badge_pic.getLayoutParams());
                    params10.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params11 = new FrameLayout.LayoutParams(params10);
                    params11.setMargins(0, 0, 0, 0);
                    profile_badge_pic.setLayoutParams(params11);

                    StoriesFragment fragment2 = new StoriesFragment();

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("loginuid", loginuid);
                    fragment2.setArguments(bundle2);

                    getSupportFragmentManager().beginTransaction().replace(R.id.linear, fragment2, "StoriesFragment").commit();
                    return true;

                case R.id.currentprofile:

                    profile_badge_pic.setBorderWidth(0);
                    profile_badge_pic.setBorderColor(getResources().getColor(R.color.selectionblue));

                    FrameLayout.LayoutParams params6 = new FrameLayout.LayoutParams(buisness_profile_badge_pic.getLayoutParams());
                    params6.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params7 = new FrameLayout.LayoutParams(params6);
                    params7.setMargins(0, 0, 0, 0);
                    buisness_profile_badge_pic.setLayoutParams(params7);

                    FrameLayout.LayoutParams params8 = new FrameLayout.LayoutParams(profile_badge_pic.getLayoutParams());
                    params8.gravity = Gravity.CENTER;
                    FrameLayout.LayoutParams params9 = new FrameLayout.LayoutParams(params8);
                    params9.setMargins(0, 0, 0, 18);
                    profile_badge_pic.setLayoutParams(params9);

                    ProfileFragment fragment3 = new ProfileFragment();

                    Bundle bundle3 = new Bundle();

                    if (isEditProfile) {
                        bundle3.putBoolean("editProfile", true);
                        bundle3.putBoolean("frommain", false);
                    }
                    else{
                        bundle3.putBoolean("editProfile", false);
                        bundle3.putBoolean("frommain", true);
                    }
                    bundle3.putBoolean("myprofile", false);
                    bundle3.putString("Uid", loginuid);
                    fragment3.setArguments(bundle3);

                    getSupportFragmentManager().beginTransaction().replace(R.id.linear, fragment3, "ProfileFragment").commit();

                    return true;

                default:
                    return false;
            }
        });




        //---vishal kalkani (LOG_006)
        if (getIntent() != null && getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("login")){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_profile_update);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView tvEditProfile = dialog.findViewById(R.id.tvEditProfile);
            TextView tvSkip = dialog.findViewById(R.id.tvSkip);

            tvEditProfile.setOnClickListener(view1 -> {
                dialog.dismiss();
                isEditProfile = true;
                bottomNavigationView.setSelectedItemId(R.id.currentprofile);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isEditProfile = false;
                    }
                },3000);


            });

            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }

    }

    private void getAllUserlist() {

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        firestore.collection(Constant.tableUsers).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task != null && task.getResult() != null && task.isSuccessful() ) {
                    userList = new ArrayList<>();

                    mArraylist = task.getResult().getDocuments();
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
                            } else if (mArraylist.get(i).get(Constant.ratingCount) instanceof Double) {

                                user.setRatingCount((Double) mArraylist.get(i).get(Constant.ratingCount));
                            } else {
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


                            } else {

                                ArrayList<HashMap<String, String>> array = (ArrayList<HashMap<String, String>>) mArraylist.get(i).get(Constant.tableExperience);
                                ArrayList<Experience> experienceArrayList = new ArrayList<>();

                                for (int i1 = 0; i1 < array.size(); i1++) {
                                    HashMap<String, String> hashmap = array.get(i1);

                                    Experience experience = new Experience();

                                    for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                        String key = entry.getKey();
                                        String value1 = "";
                                        boolean value2 = false;
                                        try {
                                            value1 = entry.getValue();
                                        } catch (Exception e) {

                                        }
                                        if (key.equalsIgnoreCase(Constant.id)) {
                                            experience.setId(value1);
                                        } else if (key.equalsIgnoreCase(Constant.orgName)) {
                                            experience.setOrgName(value1);
                                        } else if (key.equalsIgnoreCase(Constant.titleName)) {
                                            experience.setTitleName(value1);
                                        } else if (key.equalsIgnoreCase(Constant.workDesc)) {
                                            experience.setWorkDesc(value1);
                                        } else if (key.equalsIgnoreCase(Constant.startDate)) {
                                            experience.setStartDate(value1);
                                        } else if (key.equalsIgnoreCase(Constant.endDate)) {
                                            experience.setEndDate(value1);
                                        } else if (key.equalsIgnoreCase(Constant.isCurrentWorkPlace)) {
                                            experience.setCurrentWorkPlace(value2);
                                        }
                                    }

                                    experienceArrayList.add(experience);
                                }


                                user.setExperience(experienceArrayList);

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
                            } else {

                                HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableProfessionPreference);

                                ProfessionPreference professionPreference = new ProfessionPreference();

                                for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                    String key = entry.getKey();
                                    String value1 = entry.getValue();

                                    if (key.equalsIgnoreCase(Constant.id)) {
                                        professionPreference.setId(value1);
                                    } else if (key.equalsIgnoreCase(Constant.professionPref)) {
                                        professionPreference.setProfessionPref(value1);
                                    } else if (key.equalsIgnoreCase(Constant.skillPref)) {
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
                                coreValues.setInspiration("");
                                coreValues.setWorkValues("");
                                coreValues.setWorkValues("");

                                user.setCoreValues(coreValues);

                            } else if (mArraylist.get(i).get(Constant.tableCoreValues) instanceof ArrayList) {


                            } else {

                                HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableCoreValues);

                                CoreValues coreValues = new CoreValues();

                                for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                    String key = entry.getKey();
                                    String value1 = entry.getValue();

                                    if (key.equalsIgnoreCase(Constant.id)) {
                                        coreValues.setId(value1);
                                    } else if (key.equalsIgnoreCase(Constant.inspiration)) {
                                        coreValues.setInspiration(value1);
                                    } else if (key.equalsIgnoreCase(Constant.workCulture)) {
                                        coreValues.setWorkCulture(value1);
                                    } else if (key.equalsIgnoreCase(Constant.workValues)) {
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
                            } else {
                                user.setPolicyTermsAckDate(Double.valueOf((Long) mArraylist.get(i).get(Constant.policyTermsAckDate)));
                            }

                        }

                        if (mArraylist.get(i).get(Constant.token) != null) {
                            user.setToken((String) mArraylist.get(i).get(Constant.token));
                        }

                        userList.add(user);
                    }

                    progressDialog.dismiss();
                    progressDialog.cancel();

                    bottomNavigationView.setSelectedItemId(R.id.home);

                }



            }
        });
/*
        registration1 = firestore.collection(Constant.tableUsers).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    return;
                }


                userList = new ArrayList<>();

                if (value.getDocuments() != null) {

                    mArraylist = value.getDocuments();

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
                            } else if (mArraylist.get(i).get(Constant.ratingCount) instanceof Double) {

                                user.setRatingCount((Double) mArraylist.get(i).get(Constant.ratingCount));
                            } else {
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


                            } else {

                                ArrayList<HashMap<String, String>> array = (ArrayList<HashMap<String, String>>) mArraylist.get(i).get(Constant.tableExperience);
                                ArrayList<Experience> experienceArrayList = new ArrayList<>();

                                for (int i1 = 0; i1 < array.size(); i1++) {
                                    HashMap<String, String> hashmap = array.get(i1);

                                    Experience experience = new Experience();

                                    for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                        String key = entry.getKey();
                                        String value1 = "";
                                        boolean value2 = false;
                                        try {
                                            value1 = entry.getValue();
                                        } catch (Exception e) {

                                        }
                                        if (key.equalsIgnoreCase(Constant.id)) {
                                            experience.setId(value1);
                                        } else if (key.equalsIgnoreCase(Constant.orgName)) {
                                            experience.setOrgName(value1);
                                        } else if (key.equalsIgnoreCase(Constant.titleName)) {
                                            experience.setTitleName(value1);
                                        } else if (key.equalsIgnoreCase(Constant.workDesc)) {
                                            experience.setWorkDesc(value1);
                                        } else if (key.equalsIgnoreCase(Constant.startDate)) {
                                            experience.setStartDate(value1);
                                        } else if (key.equalsIgnoreCase(Constant.endDate)) {
                                            experience.setEndDate(value1);
                                        } else if (key.equalsIgnoreCase(Constant.isCurrentWorkPlace)) {
                                            experience.setCurrentWorkPlace(value2);
                                        }
                                    }

                                    experienceArrayList.add(experience);
                                }


                                user.setExperience(experienceArrayList);

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
                            } else {

                                HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableProfessionPreference);

                                ProfessionPreference professionPreference = new ProfessionPreference();

                                for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                    String key = entry.getKey();
                                    String value1 = entry.getValue();

                                    if (key.equalsIgnoreCase(Constant.id)) {
                                        professionPreference.setId(value1);
                                    } else if (key.equalsIgnoreCase(Constant.professionPref)) {
                                        professionPreference.setProfessionPref(value1);
                                    } else if (key.equalsIgnoreCase(Constant.skillPref)) {
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
                                coreValues.setInspiration("");
                                coreValues.setWorkValues("");
                                coreValues.setWorkValues("");

                                user.setCoreValues(coreValues);

                            } else if (mArraylist.get(i).get(Constant.tableCoreValues) instanceof ArrayList) {


                            } else {

                                HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableCoreValues);

                                CoreValues coreValues = new CoreValues();

                                for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                    String key = entry.getKey();
                                    String value1 = entry.getValue();

                                    if (key.equalsIgnoreCase(Constant.id)) {
                                        coreValues.setId(value1);
                                    } else if (key.equalsIgnoreCase(Constant.inspiration)) {
                                        coreValues.setInspiration(value1);
                                    } else if (key.equalsIgnoreCase(Constant.workCulture)) {
                                        coreValues.setWorkCulture(value1);
                                    } else if (key.equalsIgnoreCase(Constant.workValues)) {
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
                            } else {
                                user.setPolicyTermsAckDate(Double.valueOf((Long) mArraylist.get(i).get(Constant.policyTermsAckDate)));
                            }

                        }

                        if (mArraylist.get(i).get(Constant.token) != null) {
                            user.setToken((String) mArraylist.get(i).get(Constant.token));
                        }

                        userList.add(user);
                    }

                    progressDialog.dismiss();
                    progressDialog.cancel();

                    bottomNavigationView.setSelectedItemId(R.id.home);
                }
            }
        });
*/



    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(MainScreenActivity.this);
        dialog.setContentView(R.layout.custom_logout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView ok = dialog.findViewById(R.id.logout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                editor.clear().commit();

                tinydb.putListString("filterskills", new ArrayList<>());
                tinydb.putListString("selectedskills", new ArrayList<>());

                firestore.collection(Constant.tableUsers).document(loginuid).update(Constant.token, "");


                progressDialog.setTitle(getString(R.string.app_name));
                progressDialog.setMessage("Log out...");
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseAuth.getInstance().signOut();
                        progressDialog.dismiss();
                        Intent intent = new Intent(MainScreenActivity.this, SignupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);

            }
        });

        TextView cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {

            String data = intent.getStringExtra("requestType");
            String conversetionId = intent.getStringExtra("conversationID");
            String EventId = intent.getStringExtra("eventId");
            String userId = intent.getStringExtra("userId");

            if (data != null) {

                if (data.equalsIgnoreCase(NotificationType.request.toString()) || data.equalsIgnoreCase(NotificationType.accept.toString())) {
                    bottomNavigationView.setSelectedItemId(R.id.network);
                } else if (data.equalsIgnoreCase(NotificationType.message.toString())) {

                    if (userId != null) {

                        Intent i = new Intent(MainScreenActivity.this, MessageActivity.class);
                        i.putExtra("loginuid", loginuid);
                        i.putExtra("receiverid", userId);
                        startActivity(i);
                    }

                } else if (data.equalsIgnoreCase(NotificationType.postupdate.toString())) {
                    if (EventId != null) {

                        Intent intent1 = new Intent(MainScreenActivity.this, CommentActivity.class);
                        intent1.putExtra("user", loginuid);
                        intent1.putExtra("post", EventId);
                        startActivity(intent1);

                    }
                }
            }
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.clear().commit();

        if (registration1 != null) {
            registration1.remove();
        }

        if (registration5 != null) {
            registration5.remove();
        }

        if (registration6 != null) {
            registration6.remove();
        }

        if (registration8 != null) {
            registration8.remove();
        }
    }
}