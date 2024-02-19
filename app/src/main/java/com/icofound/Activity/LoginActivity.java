package com.icofound.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.Utils;
import com.icofound.Model.User;
import com.icofound.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button signin;
    String emailid, pass;
    TextInputEditText email, password;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    String loginuid;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    SignupActivity activity;
    TextView forgot,signup;
    FirebaseAuth auth;
    Utils utils;
    ImageView imgEye;
    boolean isEye = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        utils = new Utils(LoginActivity.this);
        utils.intialise();

        signin = findViewById(R.id.signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgot = findViewById(R.id.forgot);
        signup = findViewById(R.id.signup);
        imgEye = findViewById(R.id.imgEye);

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

        progressDialog = new ProgressDialog(LoginActivity.this);

        auth = FirebaseAuth.getInstance();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.forgot_verify);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                EditText email = dialog.findViewById(R.id.email);

                TextView submit = dialog.findViewById(R.id.submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str_email = email.getText().toString();
                        if (!str_email.matches(emailPattern)){
                            email.requestFocus();
                            email.setError("Please enter a valid email address.");
                        }
                        else if (!TextUtils.isEmpty(str_email)){
                            dialog.dismiss();
                            resetpassword(str_email);
                        }
                        else {
                            email.requestFocus();
                            email.setError("Please enter your email.");
                        }
                    }
                });

                dialog.show();
            }
        });

        activity = new SignupActivity();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (getIntent() != null) {
            emailid = getIntent().getStringExtra(Constant.email);
            pass = getIntent().getStringExtra("password");

            email.setText(emailid);
            password.setText(pass);
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateForm()) {
                    return;
                } else {
                    String email_id = email.getText().toString().trim();
                    String str_pass = password.getText().toString();

                    login(email_id, str_pass);
                }


            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean validateForm() {
        boolean valid = true;

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


        return valid;
    }

    private void login(String email_id, String str_pass) {

        closeKeyboard();

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Log in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email_id, str_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    loginuid = user.getUid();
                    if (user.isEmailVerified()) {
                        firestore.collection(Constant.tableUsers).document(loginuid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if (documentSnapshot.getData() != null){
//                                    final ObjectMapper mapper = new ObjectMapper();
//                                    User user1 = mapper.convertValue(documentSnapshot.getData(), User.class);
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

                                    if (mArraylist.get(Constant.showAge) != null){
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
                                        }
                                        else {
                                            user.setRatingCount((Long) mArraylist.get(Constant.ratingCount));
                                        }
                                    }

                                    user.setMyProfession((String) mArraylist.get(Constant.myProfession));
                                    user.setSkillPreference((String) mArraylist.get(Constant.skillPreference));


                                    if (mArraylist.get(Constant.tableExperience) != null) {
                                        if (mArraylist.get(Constant.tableExperience) instanceof String){

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
                                            user.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
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

                                            HashMap<String,String> hashmap = (HashMap<String, String>) mArraylist.get(Constant.tableProfessionPreference);

                                            ProfessionPreference professionPreference = new ProfessionPreference();

                                            for (Map.Entry<String, String> entry : hashmap.entrySet()) {

                                                String key = entry.getKey();
                                                String value1 = entry.getValue();

                                                if (key.equalsIgnoreCase(Constant.id)){
                                                    professionPreference.setId(value1);
                                                }else if (key.equalsIgnoreCase(Constant.professionPref)){
                                                    professionPreference.setProfessionPref(value1);
                                                }else if (key.equalsIgnoreCase(Constant.skillPref)){
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
                                            HashMap<String,String> hashmap = (HashMap<String, String>) mArraylist.get(Constant.tableCoreValues);

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

                                    verifyui(user);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        progressDialog.dismiss();
                        Dialog dialog = new Dialog(LoginActivity.this);
                        dialog.setContentView(R.layout.verify_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView ok = dialog.findViewById(R.id.cancel);
                        TextView resend = dialog.findViewById(R.id.resend);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                FirebaseAuth.getInstance().signOut();
                            }
                        });

                        resend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        dialog.show();
                    }

                } else {
                    verifyui(null);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

//                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    password.setText("");

                    Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.custom_login_error);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    TextView ok = dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

//                    password.requestFocus();
//                    password.setError("Wrong Password");
                }
                else if (e instanceof FirebaseAuthInvalidUserException) {
                    email.setText("");
                    password.setText("");
                    //                    email.requestFocus();
//                    email.setError("Incorrect email address");

                    Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.custom_login_error);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    TextView ok = dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
                else {
//                    Toast.makeText(LoginActivity.this, "We can’t find an account with this email address. Try another phone number or email, or if you don’t have an iCoFound account, you can sign up.", Toast.LENGTH_SHORT).show();
                    Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.custom_login_error);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    TextView ok = dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });
    }

    private void verifyui(User user) {
        progressDialog.dismiss();
        if (user != null) {
            Intent i = new Intent(LoginActivity.this, MainScreenActivity.class);
            i.putExtra("loginuid", loginuid);
            i.putExtra("from", "login");
            startActivity(i);
            finish();
        } else {

        }
    }

    private void closeKeyboard() {

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(findViewById(R.id.linearlayout).getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void resetpassword(String email1){

        closeKeyboard();

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        auth.sendPasswordResetEmail(email1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                if (e instanceof FirebaseAuthInvalidUserException){
                    Toast.makeText(LoginActivity.this, "We can’t find an account with this email address. Try another email, or if you don’t have an iCofound account, you can sign up.“", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}