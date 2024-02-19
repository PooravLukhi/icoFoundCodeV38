package com.icofound.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icofound.Activity.ContactUsActivity;
import com.icofound.Activity.EducationActivity;
import com.icofound.Activity.ExperienceActivity;
import com.icofound.Activity.MainActivity;
import com.icofound.Activity.MainScreenActivity;
import com.icofound.Activity.MessageActivity;
import com.icofound.Activity.SendReportActivity;
import com.icofound.Activity.SettingActivity;
import com.icofound.Activity.SignupActivity;
import com.icofound.Adapters.ListAdapter;
import com.icofound.Adapters.SkillAdapter;
import com.icofound.Adapters.SocialLinkAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProfileFragment extends Fragment {

    public static ImageView chat;
    public static TextView cancel, save , skillPreferences;
    MaterialSpinner professional, i_am_professional;
    String[] shapes = {"Investor", "Executive", "Founder", "Director", "Team Member/Individual Contributor", "C - Suite", "Freelancer", "Student", "Other"};

    EditText bio, username, location, work_values, work_culture, inspiration;
    User user;
    TextView tvExperienceCount , tvEducationCount,myskills, menu_username, txt_location, txt_about, txt_looking, txt_skill, txt_culture, txt_values, txt_inspire, txt_orgName, txt_desc, preview_username, edit, txt_title;
    TextView tvCourseName, tvDegree, tvcCourseYear, tvcCourseDescription , schoolName;
    ImageView imgNextEducation , imgNextExperience;
    LinearLayout layEducationArrow ,layNoEducationData, layExperienceArrow ,layNoExperience;
    RoundedImageView imageView, menu_profile, profile;
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
    boolean isEditProfile = false;
    LinearLayout profile_layout, menu_layout, open_profile, setting_layout, contactus_layout, logout_layout, experience_layout, layEducation, preview_layout, preview_profile, editLayout;
    RecyclerView skillListview;
    Button report, block;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    MainScreenActivity activity;
    LinearLayout layAddLink;
    SocialMediaLinks socialMediaLinks;
    ArrayList<SocialMediaLinks> socialMediaLinkss = new ArrayList<>();
    RecyclerView recycleSocialLink, recycleSocialLinkShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_profile, container, false);


        chat = view.findViewById(R.id.chat);
        cancel = view.findViewById(R.id.cancel);
        professional = view.findViewById(R.id.professional);
        skillPreferences = view.findViewById(R.id.skill);
        i_am_professional = view.findViewById(R.id.i_am_professional);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        myskills = view.findViewById(R.id.myskills);
        tvExperienceCount = view.findViewById(R.id.tvExperienceCount);
        tvEducationCount = view.findViewById(R.id.tvEducationCount);
        imageView = view.findViewById(R.id.imageView);
        save = view.findViewById(R.id.save);
        location = view.findViewById(R.id.location);
        ratingBar = view.findViewById(R.id.ratingBar);
        report = view.findViewById(R.id.report);
        block = view.findViewById(R.id.block);
        profile_layout = view.findViewById(R.id.profile_layout);
        menu_layout = view.findViewById(R.id.menu_layout);
        open_profile = view.findViewById(R.id.open_profile);
        menu_username = view.findViewById(R.id.menu_username);
        menu_profile = view.findViewById(R.id.menu_profile);
        setting_layout = view.findViewById(R.id.setting_layout);
        contactus_layout = view.findViewById(R.id.contactus_layout);
        logout_layout = view.findViewById(R.id.logout_layout);
        experience_layout = view.findViewById(R.id.experience_layout);
        layEducation = view.findViewById(R.id.layEducation);
        work_values = view.findViewById(R.id.work_values);
        inspiration = view.findViewById(R.id.inspiration);
        work_culture = view.findViewById(R.id.work_culture);
        //preview fvb
        preview_layout = view.findViewById(R.id.preview_layout);
        preview_profile = view.findViewById(R.id.preview_profile);
        profile = view.findViewById(R.id.profile);
        preview_username = view.findViewById(R.id.preview_username);
        txt_about = view.findViewById(R.id.txt_about);
        txt_looking = view.findViewById(R.id.txt_looking);
        txt_skill = view.findViewById(R.id.txt_skill);
        txt_culture = view.findViewById(R.id.txt_culture);
        txt_values = view.findViewById(R.id.txt_values);
        txt_inspire = view.findViewById(R.id.txt_inspire);
        txt_orgName = view.findViewById(R.id.txt_orgName);
        tvCourseName = view.findViewById(R.id.tvCourseName);
        tvDegree = view.findViewById(R.id.tvDegree);
        tvcCourseYear = view.findViewById(R.id.tvcCourseYear);
        tvcCourseDescription = view.findViewById(R.id.tvcCourseDescription);
        schoolName = view.findViewById(R.id.schoolName);
        imgNextExperience = view.findViewById(R.id.imgNextExperience);
        imgNextEducation = view.findViewById(R.id.imgNextEducation);
        layExperienceArrow = view.findViewById(R.id.layExperienceArrow);
        layNoExperience = view.findViewById(R.id.layNoExperience);
        layEducationArrow = view.findViewById(R.id.layEducationArrow);
        layNoEducationData = view.findViewById(R.id.layNoEducationData);
        txt_desc = view.findViewById(R.id.txt_desc);
        txt_title = view.findViewById(R.id.txt_title);
        skillListview = view.findViewById(R.id.skill_list);
        editLayout = view.findViewById(R.id.editLayout);
        edit = view.findViewById(R.id.edit);
        txt_location = view.findViewById(R.id.txt_location);
        layAddLink = view.findViewById(R.id.layAddLink);
        recycleSocialLink = view.findViewById(R.id.recycleSocialLink);
        recycleSocialLinkShow = view.findViewById(R.id.recycleSocialLinkShow);


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("iCoFound");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        chat.setVisibility(View.GONE);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        preferences = getActivity().getSharedPreferences("filter", MODE_PRIVATE);
        editor = preferences.edit();

        if (getArguments() != null) {
            frommain = getArguments().getBoolean("frommain", false);
            myprofile = getArguments().getBoolean("myprofile", false);
            isEditProfile = getArguments().getBoolean("editProfile", false);
            Uid = getArguments().getString("Uid");
            loginuid = getArguments().getString("loginuid");
        }

//        getuserinfo(Uid);



        if (frommain) {
            save.setVisibility(View.VISIBLE);
            chat.setVisibility(View.GONE);
            report.setVisibility(View.GONE);
            block.setVisibility(View.GONE);
        }
        else if (isEditProfile){

            menu_layout.setVisibility(View.GONE);
            preview_layout.setVisibility(View.GONE);
            profile_layout.setVisibility(View.VISIBLE);
        }
        else {
            if (myprofile) {
                save.setVisibility(View.VISIBLE);
                chat.setVisibility(View.GONE);
                report.setVisibility(View.GONE);
                block.setVisibility(View.GONE);
            } else {
                save.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
                report.setVisibility(View.VISIBLE);
                block.setVisibility(View.VISIBLE);
                imageView.setEnabled(false);
                username.setEnabled(false);
                location.setEnabled(false);
                bio.setEnabled(false);
                i_am_professional.setEnabled(false);
                myskills.setEnabled(false);
                professional.setEnabled(false);
                skillPreferences.setEnabled(false);
            }


            view.findViewById(R.id.imgAddExperience).setVisibility(View.GONE);
            view.findViewById(R.id.imgAddEduction).setVisibility(View.GONE);
        }

        tinydb = new TinyDB(getContext());

        professional.setItems(shapes);
        professional.setTextColor(Color.BLACK);
        professional.setSelected(true);

        i_am_professional.setItems(shapes);
        i_am_professional.setTextColor(Color.BLACK);
        i_am_professional.setSelected(true);

//        skillPreferences.setItems(shapes2);
//        skillPreferences.setTextColor(Color.BLACK);
//        skillPreferences.setSelected(true);

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

        experience_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ExperienceActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        layEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EducationActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        contactus_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(i);
            }
        });

        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MessageActivity.class);
                i.putExtra("loginuid", loginuid);
                i.putExtra("receiverid", Uid);
                startActivity(i);
            }
        });


        layAddLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogStyle);
                bottomSheetDialog.setContentView(R.layout.social_link_layout);


                EditText etSocialMediaName = bottomSheetDialog.findViewById(R.id.etSocialMediaName);
                EditText etLink = bottomSheetDialog.findViewById(R.id.etLink);
                TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
                TextView save = bottomSheetDialog.findViewById(R.id.save);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String socialMediaName = etSocialMediaName.getText().toString();
                        String socialLink = etLink.getText().toString();


                        if (TextUtils.isEmpty(socialMediaName)) {
                            etSocialMediaName.requestFocus();
                            etSocialMediaName.setError("Please add Social Media Name");
                            return;
                        } else if (TextUtils.isEmpty(socialLink)) {
                            etLink.requestFocus();
                            etLink.setError("Please add Link");
                            return;
                        } else
                        {
                            if (socialLink.contains("www") || socialLink.contains("http") ) {
                                uploadInfo(socialMediaName, socialLink);
                                bottomSheetDialog.dismiss();
                            } else {
                                etLink.setError("Please Enter Valid Link");
                                return;
                            }
                        }


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();


            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile(tinydb.getListString("selectedskills"), str_i_proffessional, username.getText().toString(),
                        location.getText().toString(), bio.getText().toString(),
                        str_proffession, str_skill, cameraurl, ratingBar.getRating(), work_culture.getText().toString(), work_values.getText().toString(), inspiration.getText().toString());
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
                showBottomSheetDialog(false);
//                startActivity(new Intent(getActivity(), SkillListActivity.class));
            }
        });

        cancel.setVisibility(View.INVISIBLE);

        professional.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(bio.getWindowToken(), 0);
                return false;
            }
        });

        i_am_professional.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(bio.getWindowToken(), 0);
                return false;
            }
        });

//        skillPreferences.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(bio.getWindowToken(), 0);
//                return false;
//            }
//        });

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

//        skillPreferences.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                str_skill = shapes2[position];
//            }
//        });

        skillPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(true);
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

                        Intent i = new Intent(getActivity(), SendReportActivity.class);
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

                Dialog dialog = new Dialog(getActivity());
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

                                        Dialog dialog1 = new Dialog(getActivity());
                                        dialog1.setContentView(R.layout.custom_block_complete);
                                        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                        TextView ok = dialog1.findViewById(R.id.ok);
                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
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



        view.findViewById(R.id.imgAddExperience).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ExperienceActivity.class);
                i.putExtra("user", user);
                i.putExtra("isNeedToAdd" , true);

                startActivity(i);
            }
        });
        view.findViewById(R.id.imgAddEduction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EducationActivity.class);
                i.putExtra("user", user);
                i.putExtra("isNeedToAdd" , true);
                startActivity(i);
            }
        });

        return view;
    }

    private void showBottomCameraDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
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
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
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

                    requestPermissions(new String[]{permission}, 202);
                }
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Uid != null)
            getuserinfo(Uid);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, 1001);

        } else if (requestCode == 202 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            final Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 2002);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Glide.with(getActivity()).load(photo).placeholder(R.drawable.ic_person).into(imageView);
            try {
                cameraphotoFile = createImageFile(getActivity(), user);
                FileOutputStream outputStream = new FileOutputStream(cameraphotoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                Uri uri = Uri.fromFile(cameraphotoFile);
                uploadphoto(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 2002 && data != null) {

            assert data != null;
            Uri uri = data.getData();
            Glide.with(getActivity()).load(uri).placeholder(R.drawable.ic_person).into(imageView);
//            File newfile = null;

//            try {
//                newfile = compressImage(getActivity(), data.getData(), user);
//                newfile = new File(uri.getPath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            uploadphoto(uri);


        }
    }

    private void uploadphoto(Uri uri) {

        progressDialog.show();
//        Uri uri = Uri.fromFile(photoFile);


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
                    Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d("Profile", "onFailure: " + e.getLocalizedMessage());
            }
        });

    }

    private void showBottomSheetDialog(boolean isSkillPref) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.fragment_my_skills);

        RecyclerView listrecycler = bottomSheetDialog.findViewById(R.id.listrecycler);
        TextView done = bottomSheetDialog.findViewById(R.id.done);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ListAdapter adapter = new ListAdapter(getActivity(), Utils.skillDataList, tinydb, false , isSkillPref);
        listrecycler.setLayoutManager(manager);
        listrecycler.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();


                if (isSkillPref){
                    str_skill = tinydb.getString("skillPref");
                    skillPreferences.setText(str_skill);
                }
                else{
                    String listString = tinydb.getListString("selectedskills").stream().map(Object::toString).collect(Collectors.joining(", "));
                    myskills.setText(listString);
                }


            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog.dismiss();



                if (isSkillPref){
                    str_skill = tinydb.getString("skillPref");
                    skillPreferences.setText(str_skill);
                }
                else{

                    String listString = tinydb.getListString("selectedskills").stream().map(Object::toString).collect(Collectors.joining(", "));

                    myskills.setText(listString);
                }


            }
        });

        bottomSheetDialog.show();
    }

    private void updateprofile(ArrayList<String> skilllist, String profession, String name, String location, String bio, String profession_pref, String skill_pref, String url, float rating, String workCulture, String workValue, String inspiration) {

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();

        ProfessionPreference professionPreference = new ProfessionPreference();
        professionPreference.setId(UUID.randomUUID().toString());
        professionPreference.setProfessionPref(profession_pref);
        professionPreference.setSkillPref(skill_pref);


        CoreValues coreValues = new CoreValues();
        coreValues.setId(UUID.randomUUID().toString());
        coreValues.setWorkValues(workValue);
        coreValues.setWorkCulture(workCulture);
        coreValues.setInspiration(inspiration);

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
                    Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkcamerapermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    private boolean checkgallerypermission() {

        String permission =android.Manifest.permission.READ_EXTERNAL_STORAGE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permission =  Manifest.permission.READ_MEDIA_IMAGES ;

        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void showUpdateDialog() {
        if (getActivity() != null) {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.profile_update);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView ok = dialog.findViewById(R.id.ok);
            TextView btnGotoProfile = dialog.findViewById(R.id.btnGotoProfile);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    getuserinfo(Uid);
                }
            });
            btnGotoProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    getuserinfo(Uid);
                    preview_layout.setVisibility(View.VISIBLE);
                    profile_layout.setVisibility(View.GONE);
                }
            });

            dialog.show();
        }
    }

    private void showUpdateDialogLink() {
        if (getActivity() != null) {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.profile_update);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView tvMsg = dialog.findViewById(R.id.tvMsg);
            tvMsg.setText("Social Media Profiles");
            TextView ok = dialog.findViewById(R.id.ok);
            ok.setText("Cancel");
            TextView btnGotoProfile = dialog.findViewById(R.id.btnGotoProfile);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    getuserinfo(Uid);
                }
            });
            btnGotoProfile.setText("Ok");
            btnGotoProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    getuserinfo(Uid);
//                    preview_layout.setVisibility(View.VISIBLE);
//                    profile_layout.setVisibility(View.GONE);
                }
            });

            dialog.show();
        }
    }

    private void getuserinfo(String id) {

        firestore.collection(Constant.tableUsers).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getData() != null) {

//                    user =  documentSnapshot.toObject(User.class);
                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    user = new User();

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
//                            experience.setCurrentWorkPlace(false);

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
                            tvExperienceCount.setText(experienceArrayList.size()+"");

//                                user.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
                        }

                    }


//-------- table here

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

                            Collections.sort(EducationArrayList, new Comparator<Education>(){
                                public int compare(Education obj1, Education obj2) {
                                    return obj2.getCourseYear().compareToIgnoreCase(obj1.getCourseYear()); // To compare string values
                                }
                            });


                            user.setEducation(EducationArrayList);
                            tvEducationCount.setText(EducationArrayList.size()+"");

//                                user.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
                        }

                    }

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
                        menu_username.setText(user.getName());
                        preview_username.setText(user.getName());
                    } else {
                        username.setText("");
                        menu_username.setText("");
                        preview_username.setText("");
                    }

                    if (user.getLocation() != null) {
                        location.setText(user.getLocation());
                        txt_location.setText(user.getLocation());
                    } else {
                        location.setText("");
                        menu_username.setText("");
                    }

                    if (user.getBio() != null) {
                        bio.setText(user.getBio());
                        txt_about.setText(user.getBio());
                    } else {
                        bio.setText("");
                        txt_about.setText("");
                    }

                    if (user.getCoreValues() != null) {
                        work_culture.setText(user.getCoreValues().getWorkCulture());
                        work_values.setText(user.getCoreValues().getWorkValues());
                        inspiration.setText(user.getCoreValues().getInspiration());

                        txt_culture.setText(user.getCoreValues().getWorkCulture());
                        txt_values.setText(user.getCoreValues().getWorkValues());
                        txt_inspire.setText(user.getCoreValues().getInspiration());
                    }


                    if (user.getExperience() != null) {
                        if (user.getExperience().size() > 0) {
                            txt_orgName.setText(user.getExperience().get(0).getOrgName());
                            txt_title.setText( user.getExperience().get(0).getTitleName());
//                            txt_desc.setText(user.getExperience().get(0).getWorkDesc());

                            Constant.readMoreTextView(txt_desc , user.getExperience().get(0).getWorkDesc());
                            imgNextExperience.setVisibility(View.VISIBLE);
                            layNoExperience.setVisibility(View.GONE);
                            layExperienceArrow.setVisibility(View.VISIBLE);

                            layExperienceArrow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity() , ExperienceActivity.class).putExtra("user", user));
                                }
                            });

                        }
                        else{
                            layNoExperience.setVisibility(View.VISIBLE);
                            layExperienceArrow.setVisibility(View.GONE);
                        }
                    }


                    if (user.getEducation() != null) {
                        if (user.getEducation().size() > 0) {
                            tvCourseName.setText(user.getEducation().get(0).getCourseName().trim());
                            tvDegree.setText(user.getEducation().get(0).getDegree().trim());
                            tvcCourseYear.setText( user.getEducation().get(0).getCourseYear().trim());
                            schoolName.setText( user.getEducation().get(0).getSchoolName().trim());
                            tvcCourseDescription.setText(user.getEducation().get(0).getCourseDescription().trim());
                            Constant.readMoreTextView(tvcCourseDescription , user.getEducation().get(0).getCourseDescription());


                            layNoEducationData.setVisibility(View.GONE);
                            layEducationArrow.setVisibility(View.VISIBLE);
                            imgNextEducation.setVisibility(View.VISIBLE);
                            layEducationArrow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    startActivity(new Intent(getActivity() , EducationActivity.class).putExtra("user", user));
                                }
                            });
                        }
                        else {
                            layNoEducationData.setVisibility(View.VISIBLE);
                            layEducationArrow.setVisibility(View.GONE);
                        }
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

                        SkillAdapter adapter1 = new SkillAdapter(getActivity(), skillList);
                        skillListview.setAdapter(adapter1);

                    } else {
                        myskills.setText("");
                    }


                    for (int i = 0; i < Utils.skillDataList.size(); i++) {
                        if (user.getSkillPreference() != null) {
                            if (Utils.skillDataList.get(i).getSubTitle().equalsIgnoreCase(user.getSkillPreference())) {
//                                skillPreferences.setSelectedIndex(i);
                                skillPreferences.setText(Utils.skillDataList.get(i).getSubTitle());
                                break;
                            }
                        }

                    }


                    if (user.getProfilePicLink() != null) {
                        cameraurl = user.getProfilePicLink();
                        try {
                            Glide.with(requireActivity()).load(cameraurl).placeholder(R.drawable.ic_person).into(menu_profile);
                        } catch (Exception e) {

                        }
                        try {
                            Glide.with(getActivity()).load(cameraurl).placeholder(R.drawable.ic_person).into(profile);
                        } catch (Exception e) {

                        }
                    }

                    if (user.getProfessionPreference() != null) {
                        str_proffession = user.getProfessionPreference().getProfessionPref();
                    }


                    if (user.getSkillPreference() != null) {
                        str_skill = user.getSkillPreference();
                    }

                    if (user.getMyProfession() != null) {
                        str_i_proffessional = user.getMyProfession();
                    }


                    if (user.getSocialMediaLinks() == null) {
                        socialMediaLinkss = new ArrayList<>();
                    } else {
                        socialMediaLinkss = user.getSocialMediaLinks();
                    }

                    recycleSocialLink.setHasFixedSize(true);
                    recycleSocialLink.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    recycleSocialLink.setAdapter(new SocialLinkAdapter(getActivity(), false , socialMediaLinkss, new SocialLinkAdapter.ShowEditSocialMediaDialog() {
                        @Override
                        public void onOpenDialog(SocialMediaLinks socialMediaLinks, int pos, String edit) {

                            if (edit.equalsIgnoreCase("Edit")) {
                                updateSocialMediaLink(socialMediaLinks, pos);
                            } else {
                                deleteSocialLink(socialMediaLinks, pos);
                            }

                        }

                        @Override
                        public void onAddLink() {
                            layAddLink.performClick();
                        }
                    }));

                    recycleSocialLinkShow.setHasFixedSize(true);
                    recycleSocialLinkShow.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    recycleSocialLinkShow.setAdapter(new SocialLinkAdapter(getActivity(), true,socialMediaLinkss, new SocialLinkAdapter.ShowEditSocialMediaDialog() {
                        @Override
                        public void onOpenDialog(SocialMediaLinks socialMediaLinks, int pos, String edit) {

                            if (edit.equalsIgnoreCase("Edit")) {
                                updateSocialMediaLink(socialMediaLinks, pos);
                            } else {
                                deleteSocialLink(socialMediaLinks, pos);
                            }

                        }

                        @Override
                        public void onAddLink() {
                            layAddLink.performClick();
                        }
                    }));

//                    if (user.getRatingCount() != null) {
//                        if (user.getRatingCount() instanceof Float) {
//                            ratingBar.setRating((Float) user.getRatingCount());
//                        } else {
//                            ratingBar.setRating((Double) user.getRatingCount());
//                        }
//
//                    }

                    try {
                        Glide.with(requireActivity()).load(cameraurl).placeholder(R.drawable.ic_person).into(imageView);
                    } catch (Exception e) {

                    }


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

    public File compressImage(FragmentActivity postActivity, Uri uri, User user) throws IOException {

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
        if (actualHeight > 0 && actualWidth > 0) {
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

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
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
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
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
                final String[] selectionArgs = new String[]{split[1]};

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

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
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

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(getActivity());
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

                removeListners();

//                firestore.collection(Constant.tableUsers).document(user.getId()).update(Constant.token, "");


                progressDialog.setTitle(getString(R.string.app_name));
                progressDialog.setMessage("Log out...");
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseAuth.getInstance().signOut();
                        progressDialog.dismiss();
                        Intent intent = new Intent(getActivity(), SignupActivity.class);
                        startActivity(intent);
                        getActivity().finish();
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

    public void removeListners() {

        if (((MainScreenActivity) requireActivity()).registration1 != null) {
            ((MainScreenActivity) requireActivity()).registration1.remove();
            ((MainScreenActivity) requireActivity()).registration1 = null;
        }

        if (((MainScreenActivity) requireActivity()).registration5 != null) {
            ((MainScreenActivity) requireActivity()).registration5.remove();
        }

        if (((MainScreenActivity) requireActivity()).registration6 != null) {
            ((MainScreenActivity) requireActivity()).registration6.remove();
        }
    }

    private void uploadInfo(String socialName, String link) {

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();

        if (socialMediaLinkss == null) {
            socialMediaLinkss = new ArrayList<>();
        }

        SocialMediaLinks socialMediaLinks = new SocialMediaLinks();
        socialMediaLinks.setId(UUID.randomUUID().toString());
        socialMediaLinks.setName(socialName);
        socialMediaLinks.setSocialMediaLink(link);

        socialMediaLinkss.add(socialMediaLinks);

        upload.put(Constant.tableSocialMediaLinks, socialMediaLinkss);

        ArrayList<SocialMediaLinks> finalExperienceArrayList = socialMediaLinkss;
        documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    user.setSocialMediaLinks(finalExperienceArrayList);
                    showUpdateDialogLink();
                } else {
                    Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateSocialMediaLink(SocialMediaLinks socialMediaLinks, int pos) {

        if (socialMediaLinks != null) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogStyle);
            bottomSheetDialog.setContentView(R.layout.social_link_layout);


            EditText etSocialMediaName = bottomSheetDialog.findViewById(R.id.etSocialMediaName);
            EditText etLink = bottomSheetDialog.findViewById(R.id.etLink);
            TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
            TextView save = bottomSheetDialog.findViewById(R.id.save);


            etSocialMediaName.setText(socialMediaLinks.getName());
            etLink.setText(socialMediaLinks.getSocialMediaLink());

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String socialMediaName = etSocialMediaName.getText().toString();
                    String socialLink = etLink.getText().toString();

                    DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
                    Map<String, Object> upload = new HashMap<>();


                    socialMediaLinks.setName(socialMediaName);
                    socialMediaLinks.setSocialMediaLink(socialLink);

                    socialMediaLinkss.remove(pos);
                    socialMediaLinkss.add(pos, socialMediaLinks);

                    upload.put(Constant.tableSocialMediaLinks, socialMediaLinkss);

                    ArrayList<SocialMediaLinks> finalLinkArrayList = socialMediaLinkss;
                    documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                user.setSocialMediaLinks(finalLinkArrayList);
                                bottomSheetDialog.dismiss();
                                showUpdateDialog();
                            } else {
                                Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.show();
        }


    }

    public void deleteSocialLink(SocialMediaLinks socialMediaLinks, int pos) {

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();
        socialMediaLinkss.remove(pos);
        upload.put(Constant.tableSocialMediaLinks, socialMediaLinkss);

        ArrayList<SocialMediaLinks> finalExperienceArrayList = socialMediaLinkss;
        documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    user.setSocialMediaLinks(finalExperienceArrayList);
//                    Toast.makeText(getContext(), "Link deleted", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}