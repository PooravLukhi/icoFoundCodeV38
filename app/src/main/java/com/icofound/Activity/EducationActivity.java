package com.icofound.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.icofound.Adapters.EducationAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.Education;
import com.icofound.Model.User;
import com.icofound.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EducationActivity extends AppCompatActivity {

    RecyclerView listEducation;
    LinearLayout layAddEduction;
    User user;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    EducationAdapter adapter;
    ArrayList<Education> educations = new ArrayList<>();
    TextView txtCancel;
    boolean isViewOnly = false;
    boolean isNeedToAdd = false;
    String[] shapes2 = {"Select Year", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002", "2001", "2000", "1999", "1998", "1997", "1996", "1995", "1994", "1993", "1992", "1991", "1990"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        listEducation = findViewById(R.id.listEducation);
        layAddEduction = findViewById(R.id.layAddEduction);
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

        try{
            isNeedToAdd = getIntent().getBooleanExtra("isNeedToAdd" , false);

        }catch (Exception e){

        }

        progressDialog = new ProgressDialog(EducationActivity.this);
        progressDialog.setTitle("iCoFound");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        txtCancel.setOnClickListener(view -> {
            onBackPressed();
        });

        layAddEduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EducationActivity.this, R.style.BottomSheetDialogStyle);
                bottomSheetDialog.setContentView(R.layout.buttom_sheet_education);

                EditText etDegree = bottomSheetDialog.findViewById(R.id.etDegree);
                EditText etCourseName = bottomSheetDialog.findViewById(R.id.etCourseName);
                EditText etSchoolName = bottomSheetDialog.findViewById(R.id.etSchoolName);
                EditText etcourseDescription = bottomSheetDialog.findViewById(R.id.courseDescription);
                TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
                TextView save = bottomSheetDialog.findViewById(R.id.save);
                Spinner spCourseYear = bottomSheetDialog.findViewById(R.id.spCourseYear);


                ArrayAdapter aa = new ArrayAdapter(EducationActivity.this, R.layout.spiiner_education, shapes2);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCourseYear.setAdapter(aa);


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String degree = etDegree.getText().toString();
                        String strCourseName = etCourseName.getText().toString();
                        String strSchoolName = etSchoolName.getText().toString();
                        String strCourseDescription = etcourseDescription.getText().toString();
                        String strCourseYear = spCourseYear.getSelectedItem().toString();


                        if (TextUtils.isEmpty(degree)) {
                            etDegree.requestFocus();
                            etDegree.setError("Please add Degree");
                            return;
                        } else if (TextUtils.isEmpty(strCourseName)) {
                            etCourseName.requestFocus();
                            etCourseName.setError("Please add Course Name");
                            return;
                        } else if (TextUtils.isEmpty(strSchoolName)) {
                            etSchoolName.requestFocus();
                            etSchoolName.setError("Please add School Name");
                            return;
                        } else if (TextUtils.isEmpty(strCourseDescription)) {
                            etcourseDescription.requestFocus();
                            etcourseDescription.setError("Please add Course Description");
                            return;
                        } else if (strCourseYear.equalsIgnoreCase("Select Year")) {
                            Toast.makeText(EducationActivity.this, "Please Select Year", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            uploadInfo(degree, strCourseName, strSchoolName, strCourseDescription, strCourseYear);
                            bottomSheetDialog.dismiss();
                        }


                    }
                });

                /*btnSelectedYear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCalenderDialog(btnSelectedYear);
                    }
                });*/

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
            if (user.getEducation() == null) {
                educations = new ArrayList<>();
            } else {
                educations = user.getEducation();
            }

            adapter = new EducationAdapter(EducationActivity.this, educations , isViewOnly);
            listEducation.setAdapter(adapter);
        }


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (educations.isEmpty() || isNeedToAdd)
                layAddEduction.performClick();
            }
        });


    }
    public void updateEducation(Education education, int pos) {

        if (education != null) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EducationActivity.this, R.style.BottomSheetDialogStyle);
            bottomSheetDialog.setContentView(R.layout.buttom_sheet_education);

            EditText etDegree = bottomSheetDialog.findViewById(R.id.etDegree);
            EditText etCourseName = bottomSheetDialog.findViewById(R.id.etCourseName);
            EditText etSchoolName = bottomSheetDialog.findViewById(R.id.etSchoolName);
            EditText etcourseDescription = bottomSheetDialog.findViewById(R.id.courseDescription);
            TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
            TextView save = bottomSheetDialog.findViewById(R.id.save);
//            Button btnSelectedYear = bottomSheetDialog.findViewById(R.id.btnSelectedYear);
            Spinner spCourseYear = bottomSheetDialog.findViewById(R.id.spCourseYear);

            etDegree.setText(education.getDegree());
            etCourseName.setText(education.getCourseName());
            etSchoolName.setText(education.getSchoolName());
            etcourseDescription.setText(education.getCourseDescription());
//            Constant.readMoreTextView(tvCourseDescription , education.getCourseDescription());


            ArrayAdapter aa = new ArrayAdapter(EducationActivity.this, R.layout.spiiner_education, shapes2);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourseYear.setAdapter(aa);

            String strTmp = education.getCourseYear();

            for (int i = 0; i < shapes2.length; i++) {
                if (shapes2[i].equalsIgnoreCase(strTmp)) {
                    spCourseYear.setSelection(i);
                    break;
                }
            }


//            btnSelectedYear.setText(education.getCourseYear());


            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();

                    String strDegree = etDegree.getText().toString();
                    String strCourseName = etCourseName.getText().toString();
                    String strSchoolName = etSchoolName.getText().toString();
                    String strCourseDescription = etcourseDescription.getText().toString();
                    String strCourseYear = spCourseYear.getSelectedItem().toString();

                    DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
                    Map<String, Object> upload = new HashMap<>();

                    education.setDegree(strDegree);
                    education.setCourseName(strCourseName);
                    education.setSchoolName(strSchoolName);
                    education.setCourseDescription(strCourseDescription);
                    education.setCourseYear(strCourseYear);

//                    educations = new ArrayList<>();
                    educations.remove(pos);
                    educations.add(education);
                    upload.put(Constant.tableEducation, educations);

                    ArrayList<Education> finalEducationArrayList = educations;
                    documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                user.setEducation(finalEducationArrayList);
                                showUpdateDialog("Udpate");
                            } else {
                                Toast.makeText(EducationActivity.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EducationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    public void deleteEducation(ArrayList<Education> educations) {

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();

        upload.put(Constant.tableEducation, educations);

        ArrayList<Education> finalEducationArrayList = educations;
        documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    user.setEducation(finalEducationArrayList);
                    Toast.makeText(EducationActivity.this, "Education deleted", Toast.LENGTH_SHORT).show();
                    showUpdateDialog("Delete");
                } else {
                    Toast.makeText(EducationActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EducationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void uploadInfo(String strDegree, String strCourseName, String strSchoolName, String strCourseDescription, String strCourseYear) {

        DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user.getId());
        Map<String, Object> upload = new HashMap<>();

        if (educations == null) {
            educations = new ArrayList<>();
        }

        Education education = new Education();
        education.setId(UUID.randomUUID().toString());
        education.setDegree(strDegree);
        education.setCourseName(strCourseName);
        education.setSchoolName(strSchoolName);
        education.setCourseDescription(strCourseDescription);
        education.setCourseYear(strCourseYear);

        educations.add(education);

        upload.put(Constant.tableEducation, educations);

        ArrayList<Education> finalEducationArrayList = educations;
        documentReference.update(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    user.setEducation(finalEducationArrayList);
                    showUpdateDialog("Upload");
                } else {
                    Toast.makeText(EducationActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EducationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showUpdateDialog(String msg) {
        sorttt();
        adapter.notifyDataSetChanged();
        Dialog dialog = new Dialog(EducationActivity.this);
        dialog.setContentView(R.layout.profile_update);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        TextView ok = dialog.findViewById(R.id.ok);
        TextView btnGotoProfile = dialog.findViewById(R.id.btnGotoProfile);


        tvTitle.setText("Education/Certification");
        tvMsg.setText("Education " + msg + " Successfully.");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        btnGotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
              onBackPressed();
            }
        });

        dialog.show();
    }



    void sorttt(){
        Collections.sort(educations, new Comparator<Education>(){
            public int compare(Education obj1, Education obj2) {
                return obj2.getCourseYear().compareToIgnoreCase(obj1.getCourseYear()); // To compare string values
            }
        });
    }

}