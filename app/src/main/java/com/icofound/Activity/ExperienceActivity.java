package com.icofound.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.icofound.Adapters.ExperienceAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.Experience;
import com.icofound.Model.User;
import com.icofound.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ExperienceActivity extends AppCompatActivity {

    RecyclerView experienceList;
    LinearLayout add_experience;
    User user;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    ExperienceAdapter adapter;
    ArrayList<Experience> experiences = new ArrayList<>();

    TextView txtCancel;
    private boolean isViewOnly = false;

    boolean isNeedToAdd = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);

        experienceList = findViewById(R.id.experienceList);
        add_experience = findViewById(R.id.add_experience);
        txtCancel = findViewById(R.id.txtCancel);

        firestore = FirebaseFirestore.getInstance();
        user = (User) getIntent().getSerializableExtra("user");

        try{
            isViewOnly = getIntent().getBooleanExtra("isViewOnly" , false);

            if (isViewOnly){
                findViewById(R.id.imgAdd).setVisibility(View.GONE);
            }
        }catch (Exception e){

        }


        progressDialog = new ProgressDialog(ExperienceActivity.this);
        progressDialog.setTitle("iCoFound");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        txtCancel.setOnClickListener(view -> {
            onBackPressed();
        });

        add_experience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ExperienceActivity.this, R.style.BottomSheetDialogStyle);
                bottomSheetDialog.setContentView(R.layout.fragment_buttom_sheet);

                EditText ed_organization = bottomSheetDialog.findViewById(R.id.ed_organization);
                EditText ed_job = bottomSheetDialog.findViewById(R.id.ed_job);
                EditText ed_roles = bottomSheetDialog.findViewById(R.id.ed_roles);
                TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
                TextView save = bottomSheetDialog.findViewById(R.id.save);
                Button btnStartDate = bottomSheetDialog.findViewById(R.id.btnStartDate);
                Button btnEndDate = bottomSheetDialog.findViewById(R.id.btnEndDate);
                LinearLayout layEndDate = bottomSheetDialog.findViewById(R.id.layEndDate);
                CheckBox chCurrentlyWorking = bottomSheetDialog.findViewById(R.id.chCurrentlyWorking);
                chCurrentlyWorking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                        layEndDate.setVisibility(View.GONE);
                        else
                            layEndDate.setVisibility(View.VISIBLE);
                    }
                });


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String organiztionName = ed_organization.getText().toString();
                        String jobTitle = ed_job.getText().toString();
                        String roles = ed_roles.getText().toString();
                        String startDate = btnStartDate.getText().toString();
                        String endDate = "";
                        try {
                             endDate = btnEndDate.getText().toString();
                        }catch (Exception e){

                        }
                        if (TextUtils.isEmpty(organiztionName)) {
                            ed_organization.requestFocus();
                            ed_organization.setError("Please add OrganizationName");
                            return;
                        } else if (TextUtils.isEmpty(jobTitle)) {
                            ed_job.requestFocus();
                            ed_job.setError("Please add Job Title");
                            return;
                        } else if (btnStartDate.getText().toString().equalsIgnoreCase("Select Date")) {
                            ed_job.requestFocus();
                            btnStartDate.setError("Please Select Start Date");
                            return;
                        } else if (!chCurrentlyWorking.isChecked() && btnEndDate.getText().toString().equalsIgnoreCase("Select Date")) {
                            ed_job.requestFocus();
                            btnEndDate.setError("Please Select End Date");
                            return;
                        } else if (TextUtils.isEmpty(roles)) {
                            ed_roles.requestFocus();
                            ed_roles.setError("Please add Roles and Responsibilities");
                            return;
                        } else {

                            if (chCurrentlyWorking.isChecked())
                                endDate = "";

                            uploadInfo(organiztionName, jobTitle, roles, startDate, endDate, chCurrentlyWorking.isChecked());
                            bottomSheetDialog.dismiss();
                        }


                    }
                });

                btnStartDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCalenderDialog(btnStartDate);
                    }
                });

                btnEndDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCalenderDialog(btnEndDate);
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

        if (user != null) {
            if (user.getExperience() == null) {
                experiences = new ArrayList<>();
            } else {
                experiences = user.getExperience();
            }

            adapter = new ExperienceAdapter(ExperienceActivity.this, experiences , isViewOnly);
            experienceList.setAdapter(adapter);
        }



        try{
            isNeedToAdd = getIntent().getBooleanExtra("isNeedToAdd" , false);
        }catch (Exception e){

        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (experiences.isEmpty() || isNeedToAdd)
                add_experience.performClick();
            }
        });

    }

    private void openCalenderDialog(Button btnStartDate) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ExperienceActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                        btnStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        String tmpDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date date = null;
                        try {
                            date = inputFormat.parse(tmpDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        String outputDateStr = outputFormat.format(date);
                        btnStartDate.setText(outputDateStr);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    public void updateExperience(Experience experience, int pos) {

        if (experience != null) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ExperienceActivity.this, R.style.BottomSheetDialogStyle);
            bottomSheetDialog.setContentView(R.layout.fragment_buttom_sheet);

            EditText ed_organization = bottomSheetDialog.findViewById(R.id.ed_organization);
            EditText ed_job = bottomSheetDialog.findViewById(R.id.ed_job);
            EditText ed_roles = bottomSheetDialog.findViewById(R.id.ed_roles);
            TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
            TextView save = bottomSheetDialog.findViewById(R.id.save);
            Button btnStartDate = bottomSheetDialog.findViewById(R.id.btnStartDate);
            Button btnEndDate = bottomSheetDialog.findViewById(R.id.btnEndDate);
            CheckBox chCurrentlyWorking = bottomSheetDialog.findViewById(R.id.chCurrentlyWorking);

            ed_organization.setText(experience.getOrgName());
            ed_job.setText(experience.getTitleName());
//            ed_roles.setText(experience.getWorkDesc());
            Constant.readMoreTextView(ed_roles , experience.getWorkDesc());

            btnStartDate.setText(experience.getStartDate());
            btnEndDate.setText(experience.getEndDate());
            try {
                if (experience.isCurrentWorkPlace())
                    chCurrentlyWorking.setChecked(true);
                else
                    chCurrentlyWorking.setChecked(false);
            } catch (Exception e) {

            }
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();

                    String organiztionName = ed_organization.getText().toString();
                    String jobTitle = ed_job.getText().toString();
                    String roles = ed_roles.getText().toString();
                    String strStartDate = btnStartDate.getText().toString();
                    String strEndDate = btnEndDate.getText().toString();
                    boolean isCurrentWork = chCurrentlyWorking.isChecked();

                    DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
                    Map<String, Object> upload = new HashMap<>();

                    experience.setOrgName(organiztionName);
                    experience.setTitleName(jobTitle);
                    experience.setWorkDesc(roles);
                    experience.setStartDate(strStartDate);
                    experience.setEndDate(strEndDate);
                    experience.setCurrentWorkPlace(isCurrentWork);

//                    experiences = new ArrayList<>();

                    experiences.remove(pos);
                    experiences.add(experience);

                    upload.put(Constant.tableExperience, experiences);

                    ArrayList<Experience> finalExperienceArrayList = experiences;
                    documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                user.setExperience(finalExperienceArrayList);
                                showUpdateDialog("Success" , "Work Experience Updated");
                            } else {
                                Toast.makeText(ExperienceActivity.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ExperienceActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });
            btnStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCalenderDialog(btnStartDate);
                }
            });

            btnEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCalenderDialog(btnEndDate);
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

    public void deleteExperience(ArrayList<Experience> experiences) {

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();

        upload.put(Constant.tableExperience, experiences);

        ArrayList<Experience> finalExperienceArrayList = experiences;
        documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    user.setExperience(finalExperienceArrayList);
                    Toast.makeText(ExperienceActivity.this, "Experience deleted", Toast.LENGTH_SHORT).show();
//                    showUpdateDialog("Delete" , "Work experience deleted");
                } else {
                    Toast.makeText(ExperienceActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ExperienceActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadInfo(String organiztionName, String jobTitle, String roles, String startDate, String endDate, boolean isCurrentWork) {

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();

        if (experiences == null) {
            experiences = new ArrayList<>();
        }

        Experience experience = new Experience();
        experience.setId(UUID.randomUUID().toString());
        experience.setOrgName(organiztionName);
        experience.setTitleName(jobTitle);
        experience.setWorkDesc(roles);
        experience.setStartDate(startDate);
        experience.setEndDate(endDate);
        experience.setCurrentWorkPlace(isCurrentWork);

        experiences.add(experience);

        upload.put(Constant.tableExperience, experiences);

        ArrayList<Experience> finalExperienceArrayList = experiences;
        documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    user.setExperience(finalExperienceArrayList);
                    showUpdateDialog("Success" , "Work experience Added");
                } else {
                    Toast.makeText(ExperienceActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ExperienceActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showUpdateDialog(String title , String msg) {

        adapter.notifyDataSetChanged();

        Dialog dialog = new Dialog(ExperienceActivity.this);
        dialog.setContentView(R.layout.profile_update);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        TextView ok = dialog.findViewById(R.id.ok);
        ok.setText("Cancel");

        TextView btnGotoProfile = dialog.findViewById(R.id.btnGotoProfile);
        btnGotoProfile.setText("Ok");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                adapter.notifyDataSetChanged();

            }
        });


        btnGotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                adapter.notifyDataSetChanged();

//                onBackPressed();
            }
        });

        dialog.show();
    }

}