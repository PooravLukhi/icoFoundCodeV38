package com.icofound.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icofound.Adapters.ListAdapter;
import com.icofound.Adapters.SkillAdapter;
import com.icofound.Adapters.SocialLinkShowAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Education;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.SocialMediaLinks;
import com.icofound.Class.Utils;
import com.icofound.Model.User;
import com.icofound.R;
import com.icofound.TinyDB;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProfileActivity extends AppCompatActivity {

    public static ImageView chat;
    public static TextView cancel, save;
    MaterialSpinner professional, skill, i_am_professional;
    String[] shapes = {"Investor", "Executive", "Founder", "Director", "Team Member/Individual Contributor","C - Suite","Freelancer","Student","Other"};
    String[] shapes2 = {"IOS Developer", "Java Developer", "Fullstack Developer", "Cloud computing",
            "Artificial intelligence", "Sales leadership", "Analysis", "Translation", "Moile app development",
            "People management", "Video production", "Audio production", "UX design", "SEO/SEM marketing", "Blockchain",
            "Industrial design", "Creativity", "Collaboration", "Adaptability", "Time management", "Persuasion", "Digital journalism",
            "Animation", "Marketing"};

    EditText bio, username, location, work_values, work_culture, inspiration;
    User user;
    TextView myskills, preview_username, txt_location,  txt_about, txt_looking, txt_skill, txt_culture, txt_inspire, txt_orgName, txt_desc, txt_values, close, edit, txt_inappro, txt_title;
    TextView tvCourseName, tvDegree, tvcCourseYear, tvcCourseDescription , schoolName;
    RoundedImageView imageView, profile, menu_profile;
    FirebaseFirestore firestore;
    TinyDB tinydb;
    String str_i_proffessional, str_proffession, str_skill, cameraurl, Uid, loginuid;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    FirebaseStorage storage;
    File cameraphotoFile = null;
    MaterialRatingBar ratingBar;
    boolean frommain = false;
    boolean myprofile = false;
    LinearLayout open_profile, menu_layout, preview_layout, editLayout, profile_layout, reportLayout;
    ImageView ic_chat;
    Utils utils;
    RecyclerView skillListview;
    Button report, block;
    private RecyclerView recycleSocialLinkShow;
    private ArrayList<SocialMediaLinks> socialMediaLinkss;



    ImageView imgNextEducation , imgNextExperience;
    LinearLayout layEducationArrow ,layNoEducationData, layExperienceArrow ,layNoExperience;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        utils = new Utils(ProfileActivity.this);
        utils.intialise();

        imgNextExperience = findViewById(R.id.imgNextExperience);
        imgNextEducation = findViewById(R.id.imgNextEducation);
        layExperienceArrow = findViewById(R.id.layExperienceArrow);
        layNoExperience = findViewById(R.id.layNoExperience);
        layEducationArrow = findViewById(R.id.layEducationArrow);
        layNoEducationData = findViewById(R.id.layNoEducationData);

        recycleSocialLinkShow = findViewById(R.id.recycleSocialLinkShow);
        chat = findViewById(R.id.chat);
        cancel = findViewById(R.id.cancel);
        professional = findViewById(R.id.professional);
        skill = findViewById(R.id.skill);
        i_am_professional = findViewById(R.id.i_am_professional);
        bio = findViewById(R.id.bio);
        username = findViewById(R.id.username);
        myskills = findViewById(R.id.myskills);
        imageView = findViewById(R.id.imageView);
        save = findViewById(R.id.save);
        location = findViewById(R.id.location);
        ratingBar = findViewById(R.id.ratingBar);
        report = findViewById(R.id.report);
        block = findViewById(R.id.block);
        work_values = findViewById(R.id.work_values);
        work_culture = findViewById(R.id.work_culture);
        inspiration = findViewById(R.id.inspiration);
        open_profile = findViewById(R.id.open_profile);
        menu_layout = findViewById(R.id.menu_layout);
        preview_layout = findViewById(R.id.preview_layout);
        profile = findViewById(R.id.profile);
        preview_username = findViewById(R.id.preview_username);
        txt_about = findViewById(R.id.txt_about);
        txt_looking = findViewById(R.id.txt_looking);
        txt_skill = findViewById(R.id.txt_skill);
        txt_culture = findViewById(R.id.txt_culture);
        txt_inspire = findViewById(R.id.txt_inspire);
        txt_orgName = findViewById(R.id.txt_orgName);
        txt_desc = findViewById(R.id.txt_desc);
        txt_values = findViewById(R.id.txt_values);
        txt_inspire = findViewById(R.id.txt_inspire);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvDegree = findViewById(R.id.tvDegree);
        tvcCourseYear = findViewById(R.id.tvcCourseYear);
        tvcCourseDescription = findViewById(R.id.tvcCourseDescription);
        schoolName = findViewById(R.id.schoolName);
        editLayout = findViewById(R.id.editLayout);
        profile_layout = findViewById(R.id.profile_layout);
        close = findViewById(R.id.close);
        ic_chat = findViewById(R.id.ic_chat);
        edit = findViewById(R.id.edit);
        skillListview = findViewById(R.id.skill_list);
        reportLayout = findViewById(R.id.reportLayout);
        txt_inappro = findViewById(R.id.txt_inappro);
        menu_profile = findViewById(R.id.menu_profile);
        txt_title = findViewById(R.id.txt_title);
        txt_location = findViewById(R.id.txt_location);

        menu_layout.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        preview_layout.setVisibility(View.VISIBLE);
        close.setVisibility(View.VISIBLE);
        reportLayout.setVisibility(View.VISIBLE);
        txt_inappro.setVisibility(View.VISIBLE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("iCoFound");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        chat.setVisibility(View.GONE);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        frommain = getIntent().getBooleanExtra("frommain", false);
        myprofile = getIntent().getBooleanExtra("myprofile", false);
        Uid = getIntent().getStringExtra("Uid");
        loginuid = getIntent().getStringExtra("loginuid");

        getuserinfo(Uid);

        if (frommain) {
            save.setVisibility(View.VISIBLE);
            chat.setVisibility(View.GONE);
            ic_chat.setVisibility(View.GONE);
            report.setVisibility(View.GONE);
            block.setVisibility(View.GONE);

        } else {
            if (myprofile) {
                save.setVisibility(View.VISIBLE);
                chat.setVisibility(View.GONE);
                ic_chat.setVisibility(View.GONE);
                report.setVisibility(View.GONE);
                block.setVisibility(View.GONE);

            } else {
                save.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
                ic_chat.setVisibility(View.VISIBLE);
                report.setVisibility(View.VISIBLE);
                block.setVisibility(View.VISIBLE);
                imageView.setEnabled(false);
                username.setEnabled(false);
                location.setEnabled(false);
                bio.setEnabled(false);
                i_am_professional.setEnabled(false);
                myskills.setEnabled(false);
                professional.setEnabled(false);
                skill.setEnabled(false);
            }

            findViewById(R.id.imgAddExperience).setVisibility(View.GONE);
            findViewById(R.id.imgAddEduction).setVisibility(View.GONE);
        }

        tinydb = new TinyDB(this);

        professional.setItems(shapes);
        professional.setTextColor(getResources().getColor(R.color.gray));
        professional.setSelected(true);

        i_am_professional.setItems(shapes);
        i_am_professional.setTextColor(getResources().getColor(R.color.gray));
        i_am_professional.setSelected(true);

        skill.setItems(shapes2);
        skill.setTextColor(Color.BLACK);
        skill.setSelected(true);

        ic_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, MessageActivity.class);
                i.putExtra("loginuid", loginuid);
                i.putExtra("receiverid", Uid);
                startActivity(i);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        open_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_layout.setVisibility(View.GONE);
                preview_layout.setVisibility(View.VISIBLE);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview_layout.setVisibility(View.GONE);
                profile_layout.setVisibility(View.VISIBLE);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, MessageActivity.class);
                i.putExtra("loginuid", loginuid);
                i.putExtra("receiverid", Uid);
                startActivity(i);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile(tinydb.getListString("selectedskills"), str_i_proffessional, username.getText().toString(),
                        location.getText().toString(), bio.getText().toString(), str_proffession, str_skill, cameraurl, ratingBar.getRating(),
                        work_culture.getText().toString(), work_values.getText().toString(), inspiration.getText().toString());
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBottomCameraDialog();
            }
        });

        myskills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        professional.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(bio.getWindowToken(), 0);
                return false;
            }
        });

        i_am_professional.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(bio.getWindowToken(), 0);
                return false;
            }
        });

        skill.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(bio.getWindowToken(), 0);
                return false;
            }
        });

        i_am_professional.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                str_i_proffessional = shapes[position];
            }
        });

        professional.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                str_proffession = shapes[position];
            }
        });

        skill.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                str_skill = shapes2[position];
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.custom_report);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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

                        Intent i = new Intent(ProfileActivity.this, SendReportActivity.class);
                        i.putExtra("userid", user.getId());
                        i.putExtra("text", true);
                        startActivity(i);

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.custom_block);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView block = dialog.findViewById(R.id.block);
                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Object> request = new HashMap<>();
                        request.put(Constant.blockedUserIds, FieldValue.arrayUnion(Uid));

                        Map<String, Object> request2 = new HashMap<>();
                        request2.put(Constant.blockedUserIds, FieldValue.arrayUnion(loginuid));

                        firestore.collection(Constant.tableUsers).document(loginuid).update(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                firestore.collection(Constant.tableUsers).document(Uid).update(request2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Dialog dialog1 = new Dialog(ProfileActivity.this);
                                        dialog1.setContentView(R.layout.custom_block_complete);
                                        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                        TextView ok = dialog1.findViewById(R.id.ok);
                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                                finish();
                                            }
                                        });
                                        dialog1.show();

                                    }
                                });
                            }
                        });

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
        });




    }


    private void showBottomCameraDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.camera_gallery_dialog);

        TextView camera = bottomSheetDialog.findViewById(R.id.camera);
        TextView gallery = bottomSheetDialog.findViewById(R.id.gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();

                if (checkcamerapermission()) {

                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, 1001);

                } else {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                if (checkgallerypermission()) {

                    final Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2002);

                } else {

                    String permission =android.Manifest.permission.READ_EXTERNAL_STORAGE;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        permission =  Manifest.permission.READ_MEDIA_IMAGES ;

                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{permission}, 202);
                }
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, 1001);
        } else if (requestCode == 202 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            final Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 2002);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Glide.with(ProfileActivity.this).load(photo).placeholder(R.drawable.ic_person).into(imageView);
            try {
                cameraphotoFile = createImageFile(this, user);
                FileOutputStream outputStream = new FileOutputStream(cameraphotoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                uploadphoto(cameraphotoFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 2002 && resultCode == RESULT_OK) {

            assert data != null;
            Glide.with(ProfileActivity.this).load(data.getData()).placeholder(R.drawable.ic_person).into(imageView);
            File newfile = null;
            try {
                newfile = compressImage(ProfileActivity.this, data.getData(), user);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadphoto(newfile);
        }
    }

    private void uploadphoto(File photoFile) {

        progressDialog.show();
        Uri uri = Uri.fromFile(photoFile);

        StorageReference ref = storageReference.child(Constant.tableUsers + "/" + user.getId() + "/" + user.getId() + ".Jpg");
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            cameraurl = uri.toString();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d("Profile", "onFailure: " + e.getLocalizedMessage());
            }
        });

    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.fragment_my_skills);

        RecyclerView listrecycler = bottomSheetDialog.findViewById(R.id.listrecycler);
        TextView done = bottomSheetDialog.findViewById(R.id.done);
        String[] skills = {"iOS Developer",
                "Java Developer",
                "FullStack Developer",
                "Cloud computing",
                "Artificial intelligence",
                "Sales leadership",
                "Analysis",
                "Translation",
                "Mobile app development",
                "People management",
                "Video production",
                "Audio production",
                "UX design",
                "SEO/SEM marketing",
                "Blockchain",
                "Industrial design",
                "Creativity",
                "Collaboration",
                "Adaptability",
                "Time management",
                "Persuasion",
                "Digital journalism",
                "Animation",
                "Marketing"};

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ListAdapter adapter = new ListAdapter(this, Utils.skillDataList, tinydb, false, false);
        listrecycler.setLayoutManager(manager);
        listrecycler.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                String listString = tinydb.getListString("selectedskills").stream().map(Object::toString)
                        .collect(Collectors.joining(", "));

                myskills.setText(listString);

            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
                String listString = tinydb.getListString("selectedskills").stream().map(Object::toString)
                        .collect(Collectors.joining(", "));

                myskills.setText(listString);

            }
        });

        bottomSheetDialog.show();
    }

    private void updateprofile(ArrayList<String> skilllist, String profession, String name, String location, String bio, String profession_pref, String skill_pref
            , String url, float rating, String workCulture, String workValue, String inspiration) {

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();

        ProfessionPreference professionPreference = new ProfessionPreference();
        professionPreference.setId(UUID.randomUUID().toString());
        professionPreference.setProfessionPref(profession_pref);
        professionPreference.setSkillPref("");


        CoreValues coreValues = new CoreValues();
        coreValues.setId(UUID.randomUUID().toString());
        coreValues.setWorkValues(workValue);
        coreValues.setWorkCulture(workCulture);

        upload.put(Constant.mySkills, skilllist);
        upload.put(Constant.myProfession, profession);
        upload.put(Constant.name, name);
        upload.put(Constant.location, location);
        upload.put(Constant.bio, bio);
        upload.put(Constant.tableProfessionPreference, professionPreference);
        upload.put(Constant.tableCoreValues, coreValues);
        upload.put(Constant.skillPreference, skill_pref);
        upload.put(Constant.profilePicLink, url);
        upload.put(Constant.ratingCount, rating);

        documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    user.setMySkills(skilllist);
                    user.setMyProfession(profession);
                    user.setName(name);
                    user.setLocation(location);
                    user.setBio(bio);
                    user.setProfessionPreference(professionPreference);
                    user.setCoreValues(coreValues);
                    user.setSkillPreference(skill_pref);
                    user.setProfilePicLink(url);
                    user.setRatingCount(rating);
                    showUpdateDialog();
                } else {
                    Toast.makeText(ProfileActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkcamerapermission() {

        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    private boolean checkgallerypermission() {

        String permission =android.Manifest.permission.READ_EXTERNAL_STORAGE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permission =  Manifest.permission.READ_MEDIA_IMAGES ;

        if (ContextCompat.checkSelfPermission(ProfileActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void showUpdateDialog() {
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.profile_update);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getuserinfo(Uid);
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
            if (frommain) {
                startActivity(new Intent(ProfileActivity.this, MainScreenActivity.class).putExtra("loginuid", user.getId()));
                finish();
            } else {
                super.onBackPressed();
            }
    }

    private void getuserinfo(String id) {

        firestore.collection(Constant.tableUsers).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getData() != null) {

//                   user =  documentSnapshot.toObject(User.class);
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

//                                user.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
                        }

                    }


                    if (mArraylist.get(Constant.tableEducation) != null) {
                        if (mArraylist.get(Constant.tableEducation) instanceof String) {

                            ArrayList<Education> educationArrayList = new ArrayList<>();
                            Education experience = new Education();
                            experience.setCourseName((String) mArraylist.get(Constant.tableEducation));
                            experience.setId("");
                            experience.setCourseName("");
                            experience.setCourseDescription("");
                            experience.setCourseYear("");
                            experience.setDegree("");
                            experience.setSchoolName("");

                            educationArrayList.add(experience);

                            user.setEducation(educationArrayList);


                        } else {

                            ArrayList<HashMap<String, String>> array = (ArrayList<HashMap<String, String>>) mArraylist.get(Constant.tableEducation);
                            ArrayList<Education> EducationArrayList = new ArrayList<>();

                            for (int i = 0; i < array.size(); i++) {
                                HashMap<String, String> hashmap = array.get(i);

                                Education eductaion = new Education();

                                for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                    String key = entry.getKey();
                                    String value1 = entry.getValue();

                                    if (key.equalsIgnoreCase(Constant.id)) {
                                        eductaion.setId(value1);
                                    } else if (key.equalsIgnoreCase(Constant.courseName)) {
                                        eductaion.setCourseName(value1);
                                    } else if (key.equalsIgnoreCase(Constant.courseYear)) {
                                        eductaion.setCourseYear(value1);
                                    } else if (key.equalsIgnoreCase(Constant.courseDescription)) {
                                        eductaion.setCourseDescription(value1);
                                    } else if (key.equalsIgnoreCase(Constant.degree)) {
                                        eductaion.setDegree(value1);
                                    } else if (key.equalsIgnoreCase(Constant.schoolName)) {
                                        eductaion.setSchoolName(value1);
                                    }
                                }

                                EducationArrayList.add(eductaion);
                            }


                            user.setEducation(EducationArrayList);

//                                user.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
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


                    if (user.getName() != null) {
                        username.setText(user.getName());
                        preview_username.setText(user.getName());
                    } else {
                        username.setText("");
                        preview_username.setText("");
                    }

                    if (user.getLocation() != null) {
                        location.setText(user.getLocation());
                    } else {
                        location.setText("");
                    }

                    if (user.getBio() != null) {
                        bio.setText(user.getBio());
                        txt_about.setText(user.getBio());
                    } else {
                        bio.setText("");
                        txt_about.setText("");
                    }


                    if (user.getExperience() != null) {
                        if (user.getExperience().size() > 0) {
                            txt_orgName.setText(user.getExperience().get(0).getOrgName().trim());
                            txt_title.setText(user.getExperience().get(0).getTitleName().trim());
//                            txt_desc.setText(user.getExperience().get(0).getWorkDesc());
                            Constant.readMoreTextView(txt_desc , user.getExperience().get(0).getWorkDesc().trim());

                            imgNextExperience.setVisibility(View.VISIBLE);
                            layNoExperience.setVisibility(View.GONE);
                            layExperienceArrow.setVisibility(View.VISIBLE);
                            layExperienceArrow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(ProfileActivity.this , ExperienceActivity.class).putExtra("user", user).putExtra("isViewOnly" , true));
                                }
                            });
                        }
                        else{
                            layNoExperience.setVisibility(View.VISIBLE);
                            layExperienceArrow.setVisibility(View.GONE);
                        }

                    }

                    if (user.getEducation() != null) {
                        if (user.getExperience().size() > 0) {
                            tvCourseName.setText(user.getEducation().get(0).getCourseName().trim());
                            tvDegree.setText( user.getEducation().get(0).getDegree().trim());
                            tvcCourseYear.setText( user.getEducation().get(0).getCourseYear().trim());
                            schoolName.setText( user.getEducation().get(0).getSchoolName().trim());
//                            tvcCourseDescription.setText(user.getEducation().get(0).getCourseDescription());
                            Constant.readMoreTextView(tvcCourseDescription , user.getEducation().get(0).getCourseDescription());

                            layNoEducationData.setVisibility(View.GONE);
                            layEducationArrow.setVisibility(View.VISIBLE);
                            imgNextEducation.setVisibility(View.VISIBLE);
                            layEducationArrow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    startActivity(new Intent(ProfileActivity.this , EducationActivity.class).putExtra("user", user).putExtra("isViewOnly" , true));
                                }
                            });
                        }
                        else {
                            layNoEducationData.setVisibility(View.VISIBLE);
                            layEducationArrow.setVisibility(View.GONE);
                        }
                    }

                    if (user.getCoreValues() != null) {
                            work_culture.setText(user.getCoreValues().getWorkCulture());
                            work_values.setText(user.getCoreValues().getWorkValues());
                            inspiration.setText(user.getCoreValues().getInspiration());

                            txt_culture.setText(user.getCoreValues().getWorkCulture());
                            txt_values.setText(user.getCoreValues().getWorkValues());
                            txt_inspire.setText(user.getCoreValues().getInspiration());

                    }


                    for (int i = 0; i < shapes.length; i++) {
                        if (user.getMyProfession() != null) {
                            if (shapes[i].equalsIgnoreCase(user.getMyProfession())) {
                                i_am_professional.setSelectedIndex(i);
                            }
                        }


                        if (user.getProfessionPreference() != null) {
                            if (shapes[i].equalsIgnoreCase(user.getProfessionPreference().getProfessionPref())) {
                                professional.setSelectedIndex(i);

                                txt_looking.setText(user.getProfessionPreference().getProfessionPref());
                                txt_skill.setText(user.getProfessionPreference().getSkillPref());
                            }
                        }

                    }

                    if (user.getMySkills() != null) {
                        myskills.setText(user.getMySkills().stream().map(Object::toString).collect(Collectors.joining(", ")));
                        ArrayList<String> skillList = new ArrayList<>();
                        for (int i = 0; i < user.getMySkills().size(); i++) {
                            skillList.add(user.getMySkills().get(i));
                        }

                        SkillAdapter adapter1 = new SkillAdapter(ProfileActivity.this, skillList);
                        skillListview.setAdapter(adapter1);

                    } else {
                        myskills.setText("");
                    }


                    for (int i = 0; i < shapes2.length; i++) {
                        if (user.getSkillPreference() != null) {
                            if (shapes2[i].equalsIgnoreCase(user.getSkillPreference())) {
                                skill.setSelectedIndex(i);
                                break;
                            }
                        }

                    }


                    cameraurl = user.getProfilePicLink();
                    Glide.with(getApplicationContext()).load(cameraurl).placeholder(R.drawable.ic_person).into(profile);
                    Glide.with(getApplicationContext()).load(cameraurl).placeholder(R.drawable.ic_person).into(menu_profile);

                    if (user.getProfessionPreference() != null) {
                        str_proffession = user.getProfessionPreference().getProfessionPref();
                    }

                    if (user.getSkillPreference() != null) {
                        str_skill = user.getSkillPreference();
                    }

                    if (user.getMyProfession() != null) {
                        str_i_proffessional = user.getMyProfession();
                    }


//                    if (user.getRatingCount() != null) {
//                        if (user.getRatingCount() instanceof Float) {
//                            ratingBar.setRating((Float) user.getRatingCount());
//                        } else {
//                            ratingBar.setRating((Double) user.getRatingCount());
//                        }
//
//                    }


                    Glide.with(getApplicationContext()).load(cameraurl).placeholder(R.drawable.ic_person).into(imageView);



                    if (mArraylist.get(Constant.tableSocialMediaLinks) != null) {
                        if (mArraylist.get(Constant.tableSocialMediaLinks) instanceof String) {

                            ArrayList<SocialMediaLinks> socialArrayList = new ArrayList<>();
                            SocialMediaLinks socialLinks = new SocialMediaLinks();
//                            socialLinks.setWorkDesc((String) mArraylist.get(Constant.tableSchoolName));
                            socialLinks.setId("");
                            socialLinks.setName("");
                            socialLinks.setSocialMediaLink("");

                            socialArrayList.add(socialLinks);

                            user.setSocialMediaLinks(socialArrayList);


                        } else {

                            ArrayList<HashMap<String, String>> array = (ArrayList<HashMap<String, String>>) mArraylist.get(Constant.tableSocialMediaLinks);
                            ArrayList<SocialMediaLinks> experienceArrayList = new ArrayList<>();

                            for (int i = 0; i < array.size(); i++) {
                                HashMap<String, String> hashmap = array.get(i);

                                SocialMediaLinks socialLinks = new SocialMediaLinks();

                                for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                    String key = entry.getKey();
                                    String value1 = entry.getValue();

                                    if (key.equalsIgnoreCase(Constant.id)) {
                                        socialLinks.setId(value1);
                                    } else if (key.equalsIgnoreCase(Constant.name)) {
                                        socialLinks.setName(value1);
                                    } else if (key.equalsIgnoreCase(Constant.socialMediaLink)) {
                                        socialLinks.setSocialMediaLink(value1);
                                    }
                                }

                                experienceArrayList.add(socialLinks);
                            }


                            user.setSocialMediaLinks(experienceArrayList);


                        }

                    }


                    if (user.getSocialMediaLinks() == null) {
                        socialMediaLinkss = new ArrayList<>();
                    } else {
                        socialMediaLinkss = user.getSocialMediaLinks();
                    }


                    recycleSocialLinkShow.setHasFixedSize(true);
                    recycleSocialLinkShow.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 2));
                    recycleSocialLinkShow.setAdapter(new SocialLinkShowAdapter(ProfileActivity.this, socialMediaLinkss));

                }
            }
        });

    }


    @SuppressLint("SimpleDateFormat")
    public static File createImageFile(Context context, User user) throws IOException {
        File path = new File(context.getFilesDir(), "iCoFound" + File.separator + "Images");
        if (!path.exists()) {
            path.mkdirs();
        }
        File outFile = new File(path, user.getId() + ".jpg");

        return outFile;

    }

    public File compressImage(ProfileActivity postActivity, Uri uri, User user) throws IOException {

        String filePath = getPathFromURI(postActivity, uri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;


        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = 0;
        if (actualHeight > 0) {
            imgRatio = actualWidth / actualHeight;
        }

        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        File file = createImageFile(postActivity, user);

        try {
            out = new FileOutputStream(file);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file;

    }

    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}