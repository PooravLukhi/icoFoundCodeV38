package com.icofound.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.icofound.Class.Constant;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.Utils;
import com.icofound.Model.User;
import com.icofound.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    TextView login, error_text,policy,terms;
    MaterialSpinner iam, looking;
    String[] shapes = {"Investor", "Executive", "Founder", "Director", "Team Member/Individual Contributor","C - Suite","Freelancer","Student","Other"};
    String[] shapes2 = {"Investor", "Executive", "Founder", "Director", "Team Member/Individual Contributor","C - Suite","Freelancer","Student","Other"};
    TextInputEditText name, email, password;
    Button register;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    String loginuid;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    Utils utils;
    private boolean isEye = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        utils = new Utils(SignupActivity.this);
        utils.intialise();

        iam = findViewById(R.id.iam);
        looking = findViewById(R.id.looking);
        login = findViewById(R.id.login);
        name = findViewById(R.id.name);
        register = findViewById(R.id.register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        error_text = findViewById(R.id.error_text);
        policy = findViewById(R.id.policy);
        terms = findViewById(R.id.terms);

        progressDialog = new ProgressDialog(SignupActivity.this);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateForm()) {
                    return;
                } else {

                    String fullname = name.getText().toString();
                    String emailid = email.getText().toString().trim();
                    String pass = password.getText().toString();
                    String str_iam = null;

                    for (int i = 0; i < shapes.length; i++) {

                        if (iam.getSelectedIndex() == i) {
                            str_iam = shapes[i];
                        }
                    }


                    String str_looking = null;

                    for (int i = 0; i < shapes2.length; i++) {

                        if (looking.getSelectedIndex() == i) {
                            str_looking = shapes2[i];
                        }
                    }

                    createaccount(emailid, pass, fullname, str_iam, str_looking);


                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        iam.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                return false;
            }
        });

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://arthasvc.com/privacy-policy"));
                startActivity(i);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://arthasvc.com/terms-and-conditions"));
                startActivity(i);
            }
        });

        iam.setItems(shapes);
        iam.setTextColor(Color.BLACK);
        iam.setHintColor(Color.parseColor("#c1c1c1"));


        looking.setItems(shapes2);
        looking.setTextColor(Color.BLACK);
        looking.setHintColor(Color.parseColor("#c1c1c1"));


        ImageView imgEye = findViewById(R.id.imgEye);


        imgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEye){
                    isEye = true;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setSelection(password.length());
//                    imgEye.setColorFilter(ContextCompat.getColor(LoginActivity.this, android.R.color.darker_gray));
                    imgEye.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));

                }else{
                    isEye = false;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setSelection(password.length());
                    imgEye.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String str_name = name.getText().toString();
        if (TextUtils.isEmpty(str_name)) {
            name.requestFocus();
            name.setError("Required.");
            valid = false;
        } else {
            name.setError(null);
        }

        String str_email = email.getText().toString();
        if (TextUtils.isEmpty(str_email)) {
            email.requestFocus();
            email.setError("Required.");
            valid = false;
        } else if (!str_email.trim().matches(emailPattern)) {
            email.requestFocus();
            email.setError("Email not valid.");
            valid = false;
        } else {
            email.setError(null);
        }

        String str_password = password.getText().toString();

        if (TextUtils.isEmpty(str_password)) {
            password.requestFocus();
            password.setError("Required.");
            valid = false;
        } else {
            if (password.length() >= 6) {
                password.setError(null);
            } else {
                password.requestFocus();
                password.setError("password must be 6 character");
                valid = false;
            }
        }

        String str_iam = null;

        for (int i = 0; i < shapes.length; i++) {

            if (iam.getSelectedIndex() == i) {
                str_iam = shapes[i];
            }
        }

        if (TextUtils.isEmpty(str_iam)) {
            error_text.setVisibility(View.VISIBLE);
            error_text.setText("*Please select your position.");
            valid = false;
        } else {
            error_text.setVisibility(View.GONE);
        }


        String str_looking = null;

        for (int i = 0; i < shapes2.length; i++) {

            if (looking.getSelectedIndex() == i) {
                str_looking = shapes2[i];
            }
        }

        if (TextUtils.isEmpty(str_looking)) {
            error_text.setVisibility(View.VISIBLE);
            error_text.setText("*Please select your designation.");
            valid = false;
        } else {
            error_text.setVisibility(View.GONE);
        }


        return valid;
    }

    private void createaccount(String emailid, String pass, String fullname, String str_iam, String str_looking) {
        closeKeyboard();

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Sign Up...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(emailid, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();
                    loginuid = user.getUid();

                    DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(loginuid);
                    User user1 = new User();
                    user1.setEmail(user.getEmail());
                    user1.setName(fullname);
                    user1.setId(loginuid);

                    ProfessionPreference professionPreference = new ProfessionPreference();
                    professionPreference.setId(UUID.randomUUID().toString());
                    professionPreference.setProfessionPref(str_looking);
                    professionPreference.setSkillPref("");

                    user1.setProfessionPreference(professionPreference);
                    user1.setMyProfession(str_iam);
                    user1.setProfilePicLink("");
                    user1.setBizValues("");
                    user1.setInspire("");

                    /*ArrayList<Experience> experienceArrayList = new ArrayList<>();

                    Experience experience = new Experience();
                    experience.setId(UUID.randomUUID().toString());
                    experience.setOrgName("");
                    experience.setTitleName("");
                    experience.setWorkDesc("");

                    experienceArrayList.add(experience);

                    user1.setExperience(experienceArrayList);*/
                    user1.setBio("");
                    user1.setLocation("");
                    user1.setSkillPreference("");
                    user1.setBlockedUserIds(new ArrayList<>());
                    user1.setGender("");
                    Double tsLong = Double.valueOf(System.currentTimeMillis()/1000);
                    user1.setPolicyTermsAckDate(tsLong);
                    user1.setRatingCount(0);
                    user1.setShowAge(false);
                    user1.setValues("");
                    user1.setZipcode("");
                    user1.setMySkills(new ArrayList<>());

                    documentReference.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Dialog dialog = new Dialog(SignupActivity.this);
                                        dialog.setContentView(R.layout.send_verify_dialog);
                                        dialog.setCancelable(false);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        TextView ok = dialog.findViewById(R.id.cancel);
                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                verifyui(user1);
                                            }
                                        });

                                        dialog.show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignupActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                }
                else {
                    verifyui(null);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }

    private void verifyui(User user) {
        progressDialog.dismiss();
        if (user != null) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            i.putExtra("email", user.getEmail());
            i.putExtra("password", password.getText().toString());
            startActivity(i);
            finish();
        } else {

        }
    }

    private void closeKeyboard() {

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(findViewById(R.id.linearlayout).getWindowToken(), 0);
    }
}