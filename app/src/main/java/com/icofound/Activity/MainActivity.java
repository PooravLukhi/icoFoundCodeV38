package com.icofound.Activity;

import static com.icofound.Class.Utils.skillDataList;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.icofound.AESUtils;
import com.icofound.BuildConfig;
import com.icofound.Class.Utils;
import com.icofound.Model.SkillData;
import com.icofound.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String loginuid;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    String version_fromserver;
    boolean appUnderMaintainance = false;
    Utils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils = new Utils(MainActivity.this);
        utils.intialise();
        initSkill();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mFirebaseRemoteConfig
                .fetch(0)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Log.d("FETCH", "Success");
                                    // Once the config is successfully fetched it must be activated before newly
                                    // fetched values are returned.
                                    mFirebaseRemoteConfig
                                            .activate()
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<Boolean>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Boolean> task) {
                                                            checkvalue();
                                                        }
                                                    });

                                } else {
                                    System.out.println("$$$$$$$$$$$ " + task.getException().getLocalizedMessage());
                                    Toast.makeText(
                                                    MainActivity.this,
                                                    "" + task.getException().getLocalizedMessage(),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
    }

    private void checkvalue() {
        version_fromserver = mFirebaseRemoteConfig.getString("androidMinimumRequiredVersion");
        appUnderMaintainance = mFirebaseRemoteConfig.getBoolean("androidAppUnderMaintainance");

        if (TextUtils.isEmpty(version_fromserver)) {

            final Handler handler = new Handler();
            final Runnable doNextActivity =
                    new Runnable() {
                        @Override
                        public void run() {
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            if (currentUser == null) {
                                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                                startActivity(intent);
                            } else {
                                loginuid = currentUser.getUid();
                                Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
                                intent.putExtra("loginuid", loginuid);
                                startActivity(intent);
                            }
                            finish();
                        }
                    };
            new Thread() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    handler.post(doNextActivity);
                }
            }.start();

        } else {

            double versionNum = Double.parseDouble(BuildConfig.VERSION_NAME);
            double firebaseVersion = Double.parseDouble((version_fromserver));

            if (versionNum >= firebaseVersion && !appUnderMaintainance) {

                final Handler handler = new Handler();
                final Runnable doNextActivity =
                        new Runnable() {
                            @Override
                            public void run() {
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                if (currentUser == null) {
                                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                                    startActivity(intent);
                                } else {
                                    loginuid = currentUser.getUid();
                                    Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
                                    intent.putExtra("loginuid", loginuid);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        };
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1000);
                        handler.post(doNextActivity);
                    }
                }.start();

            } else {

                if (appUnderMaintainance) {

                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.update_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setCancelable(false);
                    TextView msg = dialog.findViewById(R.id.msg);
                    TextView ok = dialog.findViewById(R.id.open);

                    msg.setText("We're currently making improvements to enhance your app experience.  Our team is working diligently to complete the maintenance as quickly as possible.");

                    ok.setText("Retry");

                    ok.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mFirebaseRemoteConfig
                                            .fetch(0)
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                Log.d("FETCH", "Success");
                                                                // Once the config is successfully fetched it must be activated
                                                                // before newly fetched values are returned.
                                                                mFirebaseRemoteConfig
                                                                        .activate()
                                                                        .addOnCompleteListener(
                                                                                new OnCompleteListener<Boolean>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Boolean> task) {
                                                                                        checkvalue();
                                                                                    }
                                                                                });
                                                            }
                                                        }
                                                    });
                                }
                            });

                    dialog.show();

                } else {

                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.update_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setCancelable(false);

                    TextView open = dialog.findViewById(R.id.open);

                    open.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String appPackageName =
                                            getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(
                                                new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(
                                                new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                "https://play.google.com/store/apps/details?id="
                                                                        + appPackageName)));
                                    }
                                }
                            });

                    dialog.show();
                }
            }
        }
    }


    private void initSkill(){
        skillDataList = new ArrayList<>();

        skillDataList.add(new SkillData("Programming Language", ""));
        skillDataList.add(new SkillData("", "JavaScript"));
        skillDataList.add(new SkillData("", "Python"));
        skillDataList.add(new SkillData("", "C++"));
        skillDataList.add(new SkillData("", "C#"));
        skillDataList.add(new SkillData("", "Ruby"));
        skillDataList.add(new SkillData("", "PHP"));
        skillDataList.add(new SkillData("", "Swift"));
        skillDataList.add(new SkillData("", "Go"));
        skillDataList.add(new SkillData("", "Kotlin"));
        skillDataList.add(new SkillData("", "TypeScript"));
        skillDataList.add(new SkillData("", "Rust"));
        skillDataList.add(new SkillData("", "MATLAB"));
        skillDataList.add(new SkillData("", "R"));
        skillDataList.add(new SkillData("", "Shell scripting"));
        skillDataList.add(new SkillData("Web Development", ""));
        skillDataList.add(new SkillData("", "HTML"));
        skillDataList.add(new SkillData("", "CSS"));
        skillDataList.add(new SkillData("", "React"));
        skillDataList.add(new SkillData("", "Angular"));
        skillDataList.add(new SkillData("", "Vue.js"));
        skillDataList.add(new SkillData("", "Node.js"));
        skillDataList.add(new SkillData("Database", ""));
        skillDataList.add(new SkillData("", "SQL"));
        skillDataList.add(new SkillData("", "MySQL"));
        skillDataList.add(new SkillData("", "PostgreSQL"));
        skillDataList.add(new SkillData("", "MongoDB"));
        skillDataList.add(new SkillData("", "Oracle"));
        skillDataList.add(new SkillData("", "Firebase"));
        skillDataList.add(new SkillData("", "AWS"));
        skillDataList.add(new SkillData("Design", ""));
        skillDataList.add(new SkillData("", "Graphic Design"));
        skillDataList.add(new SkillData("", "UI/UX Design"));
        skillDataList.add(new SkillData("", "Illustration"));
        skillDataList.add(new SkillData("", "Motion Graphics"));
        skillDataList.add(new SkillData("Data Analyst", ""));
        skillDataList.add(new SkillData("", "Data Mining"));
        skillDataList.add(new SkillData("", "Statistical Analysis"));
        skillDataList.add(new SkillData("", "Machine Learning"));
        skillDataList.add(new SkillData("", "Data Visualization"));
        skillDataList.add(new SkillData("Project Management", ""));
        skillDataList.add(new SkillData("", "Traditional Project Management"));
        skillDataList.add(new SkillData("", "Agile Methodology"));
        skillDataList.add(new SkillData("", "Scrum"));
        skillDataList.add(new SkillData("", "Kanban"));
        skillDataList.add(new SkillData("", "Risk Management"));
        skillDataList.add(new SkillData("", "Critical Path Method (CPM)"));
        skillDataList.add(new SkillData("Artificial Intelligence", ""));
        skillDataList.add(new SkillData("", "Machine Learning"));
        skillDataList.add(new SkillData("", "Deep Learning"));
        skillDataList.add(new SkillData("", "Natural Language Processing"));
        skillDataList.add(new SkillData("", "Computer Vision"));
        skillDataList.add(new SkillData("", "Robotics and Automation"));
        skillDataList.add(new SkillData("", "Expert Systems"));
        skillDataList.add(new SkillData("", "Reinforcement Learning"));
        skillDataList.add(new SkillData("", "Generative Adversarial Networks"));
        skillDataList.add(new SkillData("Marketing", ""));
        skillDataList.add(new SkillData("", "Digital Marketing"));
        skillDataList.add(new SkillData("", "Content Marketing"));
        skillDataList.add(new SkillData("", "Social Media Marketing"));
        skillDataList.add(new SkillData("", "Search Engine Optimization (SEO)"));
        skillDataList.add(new SkillData("", "Search Engine Marketing (SEM)"));
        skillDataList.add(new SkillData("", "Email Marketing"));
        skillDataList.add(new SkillData("", "Influencer Marketing"));
        skillDataList.add(new SkillData("", "Video Marketing"));
        skillDataList.add(new SkillData("", "Mobile Marketing"));
        skillDataList.add(new SkillData("", "Marketing Analytics"));
        skillDataList.add(new SkillData("", "Brand Management"));
        skillDataList.add(new SkillData("", "Public Relations (PR)"));
        skillDataList.add(new SkillData("", "Event Marketing"));
        skillDataList.add(new SkillData("", "Direct Marketing"));
        skillDataList.add(new SkillData("", "Guerrilla Marketing"));
        skillDataList.add(new SkillData("", "Affiliate Marketing"));
        skillDataList.add(new SkillData("", "Customer Relationship Management (CRM)"));

    }

}
