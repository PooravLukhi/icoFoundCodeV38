package com.icofound.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icofound.Class.Constant;
import com.icofound.Class.Utils;
import com.icofound.Model.Conversations;
import com.icofound.Model.Post;
import com.icofound.Model.User;
import com.icofound.R;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

  TextView setting_header;
  ImageView back;
  LinearLayout delete, terms, policy;
  FirebaseFirestore firestore;
  User user1;
  Utils utils;

  List<Post> postList = new ArrayList<>();
  List<DocumentSnapshot> documentSnapshotList = new ArrayList<>();

  ArrayList<Conversations> conversations_list = new ArrayList<>();
  List<DocumentSnapshot> marray = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);

    utils = new Utils(SettingActivity.this);
    utils.intialise();

    setting_header = findViewById(R.id.setting_header);
    back = findViewById(R.id.back);
    delete = findViewById(R.id.delete);
    terms = findViewById(R.id.terms);
    policy = findViewById(R.id.policy);

    firestore = FirebaseFirestore.getInstance();

    user1 = (User) getIntent().getSerializableExtra("user");

    firestore
        .collection("Posts")
        .whereEqualTo("userId", user1.getId())
        .get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot value) {
                marray = new ArrayList<>();
                conversations_list = new ArrayList<>();

                if (value != null) {
                  marray = value.getDocuments();

                  for (int i = 0; i < documentSnapshotList.size(); i++) {
                    Post post = documentSnapshotList.get(i).toObject(Post.class);
                    postList.add(post);
                  }
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(
                        SettingActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                    .show();
              }
            });

    back.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //                startActivity(new Intent(SettingActivity.this,
            // MainScreenActivity.class).putExtra("loginuid", user1.getId()));
            finish();
          }
        });

    terms.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent i =
                new Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://arthasvc.com/terms-and-conditions"));
            startActivity(i);
          }
        });

    policy.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent i =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://arthasvc.com/privacy-policy"));
            startActivity(i);
          }
        });

    TextPaint paint = setting_header.getPaint();
    float width = paint.measureText("Settings, China");

    Shader textShader =
        new LinearGradient(
            0,
            0,
            width,
            setting_header.getTextSize(),
            new int[] {
              Color.parseColor("#F97C3C"),
              Color.parseColor("#FDB54E"),
              Color.parseColor("#64B678"),
              Color.parseColor("#478AEA"),
              Color.parseColor("#8446CC"),
            },
            null,
            Shader.TileMode.CLAMP);
    setting_header.getPaint().setShader(textShader);

    delete.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Dialog dialog = new Dialog(SettingActivity.this);
            dialog.setContentView(R.layout.custom_delete);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    dialog.dismiss();
                  }
                });

            TextView delete = dialog.findViewById(R.id.delete);
            delete.setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    dialog.dismiss();
                    deleteaccount();
                  }
                });

            dialog.show();
          }
        });
  }

  private void deleteaccount() {

    Dialog dialog = new Dialog(SettingActivity.this);
    dialog.setContentView(R.layout.delete_verify);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    EditText password = dialog.findViewById(R.id.password);

    TextView submit = dialog.findViewById(R.id.submit);
    submit.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
            String str_password = password.getText().toString();

            if (!TextUtils.isEmpty(str_password)) {
              reauthenticate(user1.getEmail(), str_password);
            } else {
              password.requestFocus();
              password.setError("Please enter your password.");
            }
          }
        });

    dialog.show();
  }

  @Override
  public void onBackPressed() {
    startActivity(
        new Intent(SettingActivity.this, MainScreenActivity.class)
            .putExtra("loginuid", user1.getId()));
    finish();
  }

  private void reauthenticate(String email, String password) {
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

    user.reauthenticate(credential)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                  user.delete()
                      .addOnCompleteListener(
                          new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                              deletedata();
                              FirebaseAuth.getInstance().signOut();
                              Toast.makeText(
                                      SettingActivity.this, "Account deleted", Toast.LENGTH_SHORT)
                                  .show();
                              startActivity(new Intent(SettingActivity.this, SignupActivity.class));
                              finish();
                            }
                          })
                      .addOnFailureListener(
                          new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                              Toast.makeText(
                                      SettingActivity.this,
                                      e.getLocalizedMessage(),
                                      Toast.LENGTH_SHORT)
                                  .show();
                            }
                          });

                } else {
                  Toast.makeText(SettingActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                  Toast.makeText(SettingActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                      .show();
                }
              }
            });
  }

  private void deletedata() {

    for (int i = 0; i < postList.size(); i++) {
      if (user1.getId().equalsIgnoreCase(postList.get(i).getUserId())) {
        DocumentReference documentReference =
            firestore.collection("Posts").document(postList.get(i).getId());
        documentReference.delete();
      }
    }

    DocumentReference documentReference = firestore.collection(Constant.tableUsers).document(user1.getId());
    documentReference.delete();
  }
}
