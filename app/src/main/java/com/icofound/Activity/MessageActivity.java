package com.icofound.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icofound.Adapters.ConversionAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.Utils;
import com.icofound.Model.Conversations;
import com.icofound.Model.Data;
import com.icofound.Model.Messages;
import com.icofound.Model.MyResponse;
import com.icofound.Model.Notification1;
import com.icofound.Model.NotificationSender;
import com.icofound.Model.User;
import com.icofound.Notification.ApiClient;
import com.icofound.Notification.ApiInterface;
import com.icofound.Notification.NotificationType;
import com.icofound.Notification.Services.MyFireBaseMessagingService;
import com.icofound.R;
import com.nambimobile.widgets.efab.FabOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

  String chat_message;
  EditText typebox;
  FabOption camera, photo, location;
  TextView username;
  ImageView back, btnSend;
  RecyclerView chat_recyclerView;
  String Uid, loginuid;
  FirebaseFirestore firestore;
  User user, currentuser;
  ArrayList<Conversations> conversations_list = new ArrayList<>();
  List<DocumentSnapshot> marray = new ArrayList<>();
  boolean isourconversetion = false;
  String getuniqueId;
  ArrayList<Messages> messageslist = new ArrayList<>();
  File cameraphotoFile = null;
  StorageReference storageReference;
  FirebaseStorage storage;
  String cameraurl, receiver_profile;
  private boolean mLocationPermissionGranted;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private FusedLocationProviderClient mFusedLocationProviderClient;
  double latitude, longtitude;
  Map<String, Boolean> isread = new HashMap<>();
  boolean isfirsttime = false;
  private Location mLastKnownLocation;
  String token;
  Utils utils;
  ListenerRegistration registration, registration1, registration2, registration3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);

    utils = new Utils(MessageActivity.this);
    utils.intialise();

    location = findViewById(R.id.location);
    photo = findViewById(R.id.photo);
    camera = findViewById(R.id.camera);
    username = findViewById(R.id.username);
    back = findViewById(R.id.back);
    chat_recyclerView = findViewById(R.id.chat_recyclerView);
    btnSend = findViewById(R.id.btnSend);
    typebox = findViewById(R.id.typebox);
    firestore = FirebaseFirestore.getInstance();
    storage = FirebaseStorage.getInstance();
    storageReference = storage.getReference();

    isfirsttime = true;

    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    Uid = getIntent().getStringExtra("receiverid");
    loginuid = getIntent().getStringExtra("loginuid");

    //        latitude = getIntent().getDoubleExtra("latitude", 0);
    //        longtitude = getIntent().getDoubleExtra("longtitude", 0);

    getLocationPermission();
    getuserinfo(Uid, loginuid);

    back.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onBackPressed();
          }
        });

    btnSend.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!typebox.getText().toString().trim().isEmpty()) {
              setconversation(Uid, loginuid);
            }
          }
        });

    photo.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (checkgallerypermission()) {

              final Intent intent = new Intent(Intent.ACTION_PICK);
              intent.setType("image/*");
              intent.setAction(Intent.ACTION_GET_CONTENT);
              startActivityForResult(intent, 2002);

            } else {

              String permission =android.Manifest.permission.READ_EXTERNAL_STORAGE;

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                permission =  Manifest.permission.READ_MEDIA_IMAGES ;


              ActivityCompat.requestPermissions(
                  MessageActivity.this,
                  new String[] {permission},
                  202);
            }
          }
        });

    camera.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (checkcamerapermission()) {

              Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(i, 1001);

            } else {
              ActivityCompat.requestPermissions(
                  MessageActivity.this, new String[] {Manifest.permission.CAMERA}, 101);
            }
          }
        });

    location.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            //                Intent i = new Intent(MessageActivity.this, AutoPlaceActivity.class);
            //                startActivityForResult(i, 1);

            // For map current location, near by places, place auto complete
            //                Intent i = new Intent(MessageActivity.this, MapActivity.class);
            //                startActivityForResult(i, 1);

            // for send current location
            if (mLocationPermissionGranted) {
              getDeviceLocation();
            } else {
              getLocationPermission();
            }
          }
        });

    TextPaint paint = username.getPaint();
    float width = paint.measureText("Settings");

    Shader textShader =
        new LinearGradient(
            0,
            0,
            width,
            username.getTextSize(),
            new int[] {
              Color.parseColor("#9E73FE"), Color.parseColor("#8149FF"),
            },
            null,
            Shader.TileMode.CLAMP);
    username.getPaint().setShader(textShader);
  }

  private void getDeviceLocation() {
    try {

      if (mLocationPermissionGranted) {
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(
            this,
            new OnCompleteListener<Location>() {
              @Override
              public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                  // Set the map's camera position to the current location of the device.
                  mLastKnownLocation = task.getResult();
                  if (mLastKnownLocation != null) {
                    latitude = mLastKnownLocation.getLatitude();
                    longtitude = mLastKnownLocation.getLongitude();

                    Map<String, Boolean> isRead = new HashMap<>();
                    isRead.put(loginuid, true);
                    isRead.put(Uid, false);

                    String location =
                        "https://maps.google.com/maps/api/staticmap?markers=color:red|"
                            + latitude
                            + ","
                            + longtitude
                            + "&;zoom=13&size=242x242&sensor=true&key=AIzaSyCcl1J8G4rJzlduDDaWc-2wFCwZvcgBF4I";

                    if (isourconversetion) {
                      Map<String, Object> upload = new HashMap<>();
                      upload.put("lastMessage", Constant.location);
                      upload.put("timestamp", System.currentTimeMillis() / 1000);
                      upload.put("isRead", isRead);

                      DocumentReference reference =
                          firestore.collection(Constant.conversations).document(getuniqueId);
                      reference
                          .update(upload)
                          .addOnSuccessListener(
                              new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                  Messages messages = new Messages();
                                  messages.setId(UUID.randomUUID().toString());
                                  messages.setOwnerID(loginuid);
                                  messages.setContent(latitude + ":" + longtitude);
                                  messages.setTimestamp(System.currentTimeMillis() / 1000);
                                  messages.setContentType(2);
                                  messages.setProfilePicLink(location);

                                  reference
                                      .collection("Messages")
                                      .document(messages.getId())
                                      .set(messages)
                                      .addOnSuccessListener(
                                          new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {}
                                          });
                                }
                              });

                    } else {

                      ArrayList<String> ids = new ArrayList<>();
                      ids.add(loginuid);
                      ids.add(Uid);

                      Conversations conversations = new Conversations();
                      conversations.setId(UUID.randomUUID().toString());
                      conversations.setUserIDs(ids);
                      conversations.setLastMessage(Constant.location);
                      conversations.setTimestamp(System.currentTimeMillis() / 1000);
                      conversations.setIsRead(isRead);

                      Messages messages = new Messages();
                      messages.setId(UUID.randomUUID().toString());
                      messages.setOwnerID(loginuid);
                      messages.setContent(latitude + ":" + longtitude);
                      messages.setTimestamp(System.currentTimeMillis() / 1000);
                      messages.setContentType(2);
                      messages.setProfilePicLink(location);

                      DocumentReference reference =
                          firestore.collection(Constant.conversations).document(conversations.getId());
                      reference
                          .set(conversations)
                          .addOnSuccessListener(
                              new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                  reference
                                      .collection("Messages")
                                      .document(messages.getId())
                                      .set(messages)
                                      .addOnSuccessListener(
                                          new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {}
                                          });
                                }
                              });
                    }
                  }
                }
              }
            });
      }
    } catch (SecurityException e) {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  private void getLocationPermission() {
    mLocationPermissionGranted = false;
    if (ContextCompat.checkSelfPermission(
            this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      mLocationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(
          this,
          new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  private void setconversation(String receiverid, String loginuid) {

    chat_message = typebox.getText().toString().trim();
    typebox.getText().clear();

    Map<String, Boolean> isRead = new HashMap<>();
    if (loginuid != null)
    isRead.put(loginuid, true);

    if (receiverid != null)
    isRead.put(receiverid, false);

    if (isourconversetion) {

      Map<String, Object> upload = new HashMap<>();
      upload.put("lastMessage", chat_message);
      upload.put("timestamp", System.currentTimeMillis() / 1000);
      upload.put("isRead", isRead);

      DocumentReference reference = firestore.collection(Constant.conversations).document(getuniqueId);
      reference
          .update(upload)
          .addOnSuccessListener(
              new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                  Messages messages = new Messages();
                  messages.setId(UUID.randomUUID().toString());
                  messages.setOwnerID(loginuid);
                  messages.setMessage(chat_message);
                  messages.setTimestamp(System.currentTimeMillis() / 1000);
                  messages.setContentType(0);

                  reference
                      .collection("Messages")
                      .document(messages.getId())
                      .set(messages)
                      .addOnSuccessListener(
                          new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {}
                          });
                }
              });

    } else {

      ArrayList<String> ids = new ArrayList<>();
      ids.add(loginuid);
      ids.add(receiverid);

      Conversations conversations = new Conversations();
      conversations.setId(UUID.randomUUID().toString());
      conversations.setUserIDs(ids);
      conversations.setLastMessage(chat_message);
      conversations.setTimestamp(System.currentTimeMillis() / 1000);
      conversations.setIsRead(isRead);

      Messages messages = new Messages();
      messages.setId(UUID.randomUUID().toString());
      messages.setOwnerID(loginuid);
      messages.setMessage(chat_message);
      messages.setTimestamp(System.currentTimeMillis() / 1000);
      messages.setContentType(0);

      DocumentReference reference =
          firestore.collection(Constant.conversations).document(conversations.getId());
      reference
          .set(conversations)
          .addOnSuccessListener(
              new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                  reference
                      .collection("Messages")
                      .document(messages.getId())
                      .set(messages)
                      .addOnSuccessListener(
                          new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {}
                          });
                }
              });
    }

    sendnotificatioin(
        currentuser.getName(),
        chat_message,
        token,
        NotificationType.message,
        loginuid,
        getuniqueId);
  }

  private void getMessages(String receiverid) {

    CollectionReference reference = firestore.collection(Constant.conversations);

    registration =
        reference.addSnapshotListener(
            new EventListener<QuerySnapshot>() {
              @Override
              public void onEvent(
                  @Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null){
                  return;
                }


                marray = new ArrayList<>();
                conversations_list = new ArrayList<>();

                if (value.getDocuments() != null) {
                  marray = value.getDocuments();

                  for (int i = 0; i < marray.size(); i++) {
                    Conversations conversations = marray.get(i).toObject(Conversations.class);
                    conversations_list.add(conversations);
                  }

                  for (int i = 0; i < conversations_list.size(); i++) {

                    ArrayList<String> userids = new ArrayList<>();

                    userids = conversations_list.get(i).getUserIDs();

                    if (userids.contains(loginuid) && userids.contains(receiverid)) {
                      isourconversetion = true;
                      getuniqueId = conversations_list.get(i).getId();
                      isread = conversations_list.get(i).getIsRead();
                      break;
                    }
                  }

                  if (getuniqueId != null) {
                    getmessagelist();
                  }

                  if (isfirsttime) {
                    if (isourconversetion) {
                      for (Map.Entry<String, Boolean> entry : isread.entrySet()) {

                        if (entry.getKey().equalsIgnoreCase(loginuid)) {
                          if (!entry.getValue()) {
                            Map<String, Boolean> isRead = new HashMap<>();
                            isRead.put(loginuid, true);
                            isRead.put(Uid, true);

                            Map<String, Object> upload = new HashMap<>();
                            upload.put("isRead", isRead);

                            DocumentReference reference1 =
                                firestore.collection(Constant.conversations).document(getuniqueId);
                            reference1.update(upload);

                            break;
                          }
                        }
                      }
                    }
                  }
                }
              }
            });
  }

  private void getmessagelist() {
    registration1 =
        firestore
            .collection(Constant.conversations)
            .document(getuniqueId)
            .collection("Messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                  @Override
                  public void onEvent(
                      @Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null){
                      return;
                    }


                    messageslist = new ArrayList<>();

                    assert value != null;
                    if (value.getDocuments() != null) {

                      for (int i = 0; i < value.getDocuments().size(); i++) {
                        Messages messages = value.getDocuments().get(i).toObject(Messages.class);
                        messageslist.add(messages);
                      }

                      RecyclerView.LayoutManager manager =
                          new LinearLayoutManager(
                              MessageActivity.this, LinearLayoutManager.VERTICAL, false);
                      ConversionAdapter adapter =
                          new ConversionAdapter(
                              MessageActivity.this, loginuid, Uid, messageslist, receiver_profile);
                      chat_recyclerView.setLayoutManager(manager);
                      chat_recyclerView.setAdapter(adapter);
                      adapter.notifyItemInserted(messageslist.size() - 1);
                      chat_recyclerView.scrollToPosition(messageslist.size() - 1);
                    }
                  }
                });
  }

  private void getuserinfo(String uid, String loginuid) {


    if (uid == null)
      return;

    registration2 =
        firestore
            .collection(Constant.tableUsers)
            .document(uid)
            .addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                  @Override
                  public void onEvent(
                      @Nullable DocumentSnapshot value,
                      @Nullable FirebaseFirestoreException error) {

                    if (value.getData() != null) {
                      //                    final ObjectMapper objectMapper = new ObjectMapper();
                      //                    user = objectMapper.convertValue(value.getData(),
                      // User.class);

                      //                    user =  value.toObject(User.class);

                      Map<String, Object> mArraylist = value.getData();

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
                        user.setBlockedUserIds(
                            (ArrayList<String>) mArraylist.get(Constant.blockedUserIds));
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
                          user.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
                        }
                      }

                      if (mArraylist.get(Constant.tableProfessionPreference) != null) {
                        if (mArraylist.get(Constant.tableProfessionPreference) instanceof String) {

                          ProfessionPreference professionPreference = new ProfessionPreference();
                          professionPreference.setProfessionPref(
                              (String) mArraylist.get(Constant.tableProfessionPreference));
                          professionPreference.setSkillPref("");
                          professionPreference.setId("");

                          user.setProfessionPreference(professionPreference);
                        } else {

                          HashMap<String, String> hashmap =
                              (HashMap<String, String>) mArraylist.get(Constant.tableProfessionPreference);

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
                          HashMap<String, String> hashmap =
                              (HashMap<String, String>) mArraylist.get(Constant.tableCoreValues);

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
                          user.setPolicyTermsAckDate(
                              Double.valueOf((Long) mArraylist.get(Constant.policyTermsAckDate)));
                        }
                      }

                      if (mArraylist.get(Constant.token) != null) {
                        user.setToken((String) mArraylist.get(Constant.token));
                      }

                      username.setText(user.getName());
                      token = user.getToken();
                      receiver_profile = user.getProfilePicLink();
                    }
                  }
                });

    registration3 =
        firestore
            .collection(Constant.tableUsers)
            .document(loginuid)
            .addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                  @Override
                  public void onEvent(
                      @Nullable DocumentSnapshot value,
                      @Nullable FirebaseFirestoreException error) {
                    if (value.getData() != null) {
                      //                    final ObjectMapper objectMapper = new ObjectMapper();
                      //                    currentuser = objectMapper.convertValue(value.getData(),
                      // User.class);

                      Map<String, Object> mArraylist = value.getData();

                      currentuser = new User();
                      if (mArraylist.get(Constant.id) != null) {
                        currentuser.setId((String) mArraylist.get(Constant.id));
                      }

                      if (mArraylist.get(Constant.name) != null) {
                        currentuser.setName((String) mArraylist.get(Constant.name));
                      }

                      if (mArraylist.get(Constant.bio) != null) {
                        currentuser.setBio((String) mArraylist.get(Constant.bio));
                      }

                      if (mArraylist.get(Constant.bizValues) != null) {
                        currentuser.setBizValues((String) mArraylist.get(Constant.bizValues));
                      }

                      if (mArraylist.get(Constant.blockedUserIds) != null) {
                        currentuser.setBlockedUserIds(
                            (ArrayList<String>) mArraylist.get(Constant.blockedUserIds));
                      }

                      if (mArraylist.get(Constant.email) != null) {
                        currentuser.setEmail((String) mArraylist.get(Constant.email));
                      }

                      if (mArraylist.get(Constant.profilePicLink) != null) {
                        currentuser.setProfilePicLink((String) mArraylist.get(Constant.profilePicLink));
                      }

                      if (mArraylist.get(Constant.gender) != null) {
                        currentuser.setGender((String) mArraylist.get(Constant.gender));
                      }

                      if (mArraylist.get(Constant.showAge) != null) {
                        currentuser.setShowAge((boolean) mArraylist.get(Constant.showAge));
                      }

                      if (mArraylist.get(Constant.zipcode) != null) {
                        currentuser.setZipcode((String) mArraylist.get(Constant.zipcode));
                      }

                      if (mArraylist.get(Constant.location) != null) {
                        currentuser.setLocation((String) mArraylist.get(Constant.location));
                      }

                      if (mArraylist.get(Constant.ratingCount) != null) {
                        if (mArraylist.get(Constant.ratingCount) instanceof Float) {
                          currentuser.setRatingCount((Float) mArraylist.get(Constant.ratingCount));

                        } else if (mArraylist.get(Constant.ratingCount) instanceof Double) {
                          currentuser.setRatingCount((Double) mArraylist.get(Constant.ratingCount));
                        } else {
                          currentuser.setRatingCount((Long) mArraylist.get(Constant.ratingCount));
                        }
                      }

                      currentuser.setMyProfession((String) mArraylist.get(Constant.myProfession));
                      currentuser.setSkillPreference((String) mArraylist.get(Constant.skillPreference));

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

                          currentuser.setExperience(experienceArrayList);

                        } else {
                          currentuser.setExperience(
                              (ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
                        }
                      }

                      if (mArraylist.get(Constant.tableProfessionPreference) != null) {
                        if (mArraylist.get(Constant.tableProfessionPreference) instanceof String) {

                          ProfessionPreference professionPreference = new ProfessionPreference();
                          professionPreference.setProfessionPref(
                              (String) mArraylist.get(Constant.tableProfessionPreference));
                          professionPreference.setSkillPref("");
                          professionPreference.setId("");

                          currentuser.setProfessionPreference(professionPreference);
                        } else {

                          HashMap<String, String> hashmap =
                              (HashMap<String, String>) mArraylist.get(Constant.tableProfessionPreference);

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
                          currentuser.setProfessionPreference(professionPreference);
                        }
                      }

                      if (mArraylist.get(Constant.tableCoreValues) != null) {
                        if (mArraylist.get(Constant.tableCoreValues) instanceof String) {

                          CoreValues coreValues = new CoreValues();
                          coreValues.setId("");
                          coreValues.setWorkValues("");
                          coreValues.setWorkValues("");

                          currentuser.setCoreValues(coreValues);

                        } else {
                          HashMap<String, String> hashmap =
                              (HashMap<String, String>) mArraylist.get(Constant.tableCoreValues);

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

                          currentuser.setCoreValues(coreValues);
                        }
                      }

                      if (mArraylist.get(Constant.mySkills) != null) {
                        currentuser.setMySkills((ArrayList<String>) mArraylist.get(Constant.mySkills));
                      }

                      if (mArraylist.get(Constant.inspire) != null) {
                        currentuser.setInspire((String) mArraylist.get(Constant.inspire));
                      }

                      if (mArraylist.get(Constant.values) != null) {
                        currentuser.setValues((String) mArraylist.get(Constant.values));
                      }

                      if (mArraylist.get(Constant.policyTermsAckDate) != null) {
                        if (mArraylist.get(Constant.policyTermsAckDate) instanceof Double) {
                          currentuser.setPolicyTermsAckDate(
                              (Double) mArraylist.get(Constant.policyTermsAckDate));
                        } else {
                          currentuser.setPolicyTermsAckDate(
                              Double.valueOf((Long) mArraylist.get(Constant.policyTermsAckDate)));
                        }
                      }

                      if (mArraylist.get(Constant.token) != null) {
                        currentuser.setToken((String) mArraylist.get(Constant.token));
                      }
                    }
                  }
                });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1001 && resultCode == RESULT_OK) {

      Bitmap photo = (Bitmap) data.getExtras().get("data");
      try {
        cameraphotoFile = createImageFile(this, user);
        FileOutputStream outputStream = new FileOutputStream(cameraphotoFile);
        photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.close();
        Uri uri = Uri.fromFile(cameraphotoFile);
        uploadphoto(uri);
      } catch (IOException e) {
        e.printStackTrace();
      }

    } else if (requestCode == 2002 && resultCode == RESULT_OK) {

      Uri uri = data.getData();
      //            File newfile = null;
      //            try {
      //                newfile = compressImage(MessageActivity.this, data.getData(), user);
      //            } catch (IOException e) {
      //                e.printStackTrace();
      //            }
      uploadphoto(uri);
    } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

      latitude = data.getDoubleExtra("latitude", 0);
      longtitude = data.getDoubleExtra("longtitude", 0);

      Map<String, Boolean> isRead = new HashMap<>();
      isRead.put(loginuid, true);
      isRead.put(Uid, false);

      String location =
          "https://maps.google.com/maps/api/staticmap?markers=color:red|"
              + latitude
              + ","
              + longtitude
              + "&;zoom=13&size=242x242&sensor=true&key=AIzaSyCcl1J8G4rJzlduDDaWc-2wFCwZvcgBF4I";

      if (isourconversetion) {
        Map<String, Object> upload = new HashMap<>();
        upload.put("lastMessage", Constant.location);
        upload.put("timestamp", System.currentTimeMillis() / 1000);
        upload.put("isRead", isRead);

        DocumentReference reference = firestore.collection(Constant.conversations).document(getuniqueId);
        reference
            .update(upload)
            .addOnSuccessListener(
                new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                    Messages messages = new Messages();
                    messages.setId(UUID.randomUUID().toString());
                    messages.setOwnerID(loginuid);
                    messages.setContent(latitude + ":" + longtitude);
                    messages.setTimestamp(System.currentTimeMillis() / 1000);
                    messages.setContentType(2);
                    messages.setProfilePicLink(location);

                    reference
                        .collection("Messages")
                        .document(messages.getId())
                        .set(messages)
                        .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {}
                            });
                  }
                });

      } else {

        ArrayList<String> ids = new ArrayList<>();
        ids.add(loginuid);
        ids.add(Uid);

        Conversations conversations = new Conversations();
        conversations.setId(UUID.randomUUID().toString());
        conversations.setUserIDs(ids);
        conversations.setLastMessage(Constant.location);
        conversations.setTimestamp(System.currentTimeMillis() / 1000);
        conversations.setIsRead(isRead);

        Messages messages = new Messages();
        messages.setId(UUID.randomUUID().toString());
        messages.setOwnerID(loginuid);
        messages.setContent(latitude + ":" + longtitude);
        messages.setTimestamp(System.currentTimeMillis() / 1000);
        messages.setContentType(2);
        messages.setProfilePicLink(location);

        DocumentReference reference =
            firestore.collection(Constant.conversations).document(conversations.getId());
        reference
            .set(conversations)
            .addOnSuccessListener(
                new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                    reference
                        .collection("Messages")
                        .document(messages.getId())
                        .set(messages)
                        .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {}
                            });
                  }
                });
      }

      sendnotificatioin(
          currentuser.getName(),
          "Sent you a location",
          token,
          NotificationType.message,
          loginuid,
          getuniqueId);
    }
  }

  private void uploadphoto(Uri uri) {

    //        Uri uri = Uri.fromFile(photoFile);

    String id = UUID.randomUUID().toString();

    StorageReference ref = storageReference.child("Messages" + "/" + id + "/" + id + ".Jpg");
    ref.putFile(uri)
        .addOnCompleteListener(
            new OnCompleteListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                  ref.getDownloadUrl()
                      .addOnSuccessListener(
                          new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                              cameraurl = uri.toString();

                              Map<String, Boolean> isRead = new HashMap<>();
                              isRead.put(loginuid, true);
                              isRead.put(Uid, false);

                              if (isourconversetion) {

                                Map<String, Object> upload = new HashMap<>();
                                upload.put("lastMessage", "Attachment");
                                upload.put("timestamp", System.currentTimeMillis() / 1000);
                                upload.put("isRead", isRead);

                                DocumentReference reference =
                                    firestore.collection(Constant.conversations).document(getuniqueId);
                                reference
                                    .update(upload)
                                    .addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {
                                            Messages messages = new Messages();
                                            messages.setId(UUID.randomUUID().toString());
                                            messages.setOwnerID(loginuid);
                                            messages.setProfilePicLink(cameraurl);
                                            messages.setTimestamp(
                                                System.currentTimeMillis() / 1000);
                                            messages.setContentType(1);

                                            reference
                                                .collection("Messages")
                                                .document(messages.getId())
                                                .set(messages)
                                                .addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                                      @Override
                                                      public void onSuccess(Void unused) {
                                                        getmessagelist();
                                                      }
                                                    });
                                          }
                                        });

                              } else {

                                ArrayList<String> ids = new ArrayList<>();
                                ids.add(loginuid);
                                ids.add(Uid);

                                Conversations conversations = new Conversations();
                                conversations.setId(UUID.randomUUID().toString());
                                conversations.setUserIDs(ids);
                                conversations.setLastMessage("Attachment");
                                conversations.setTimestamp(System.currentTimeMillis() / 1000);
                                conversations.setIsRead(isRead);

                                Messages messages = new Messages();
                                messages.setId(UUID.randomUUID().toString());
                                messages.setOwnerID(loginuid);
                                messages.setProfilePicLink(cameraurl);
                                messages.setTimestamp(System.currentTimeMillis() / 1000);
                                messages.setContentType(1);

                                DocumentReference reference =
                                    firestore
                                        .collection(Constant.conversations)
                                        .document(conversations.getId());
                                reference
                                    .set(conversations)
                                    .addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {
                                            reference
                                                .collection("Messages")
                                                .document(messages.getId())
                                                .set(messages)
                                                .addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                                      @Override
                                                      public void onSuccess(Void unused) {
                                                        getmessagelist();
                                                      }
                                                    });
                                          }
                                        });
                              }

                              sendnotificatioin(
                                  currentuser.getName(),
                                  "Sent you an attachment",
                                  token,
                                  NotificationType.message,
                                  loginuid,
                                  getuniqueId);
                            }
                          })
                      .addOnFailureListener(
                          new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {}
                          });
                } else {
                  Toast.makeText(MessageActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
                Log.d("Profile", "onFailure: " + e.getLocalizedMessage());
              }
            });
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == 101
        && grantResults.length > 0
        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      startActivityForResult(i, 1001);
    } else if (requestCode == 202
        && grantResults.length > 0
        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

      final Intent intent = new Intent(Intent.ACTION_PICK);
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(intent, 2002);
    } else if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mLocationPermissionGranted = true;
      }
    }
  }

  private boolean checkcamerapermission() {

    if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      return true;
    } else {
      return false;
    }
  }

  private boolean checkgallerypermission() {

    String permission =android.Manifest.permission.READ_EXTERNAL_STORAGE;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
      permission =  Manifest.permission.READ_MEDIA_IMAGES ;

    if (ContextCompat.checkSelfPermission(
            MessageActivity.this, permission)
        == PackageManager.PERMISSION_GRANTED) {
      return true;
    } else {
      return false;
    }
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

  public File compressImage(MessageActivity postActivity, Uri uri, User user) throws IOException {

    String filePath = getPathFromURI(postActivity, uri);
    Bitmap scaledBitmap = null;

    BitmapFactory.Options options = new BitmapFactory.Options();

    //      by setting this field as true, the actual bitmap pixels are not loaded in the memory.
    // Just the bounds are loaded. If
    //      you try the use the bitmap here, you will get null.
    options.inJustDecodeBounds = true;
    Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

    int actualHeight = options.outHeight;
    int actualWidth = options.outWidth;

    //      max Height and width values of the compressed image is taken as 816x612

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
    canvas.drawBitmap(
        bmp,
        middleX - bmp.getWidth() / 2,
        middleY - bmp.getHeight() / 2,
        new Paint(Paint.FILTER_BITMAP_FLAG));

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
      scaledBitmap =
          Bitmap.createBitmap(
              scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
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
        final Uri contentUri =
            ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

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
        final String[] selectionArgs = new String[] {split[1]};

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

  public static String getDataColumn(
      Context context, Uri uri, String selection, String[] selectionArgs) {

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

  @Override
  protected void onResume() {
    super.onResume();
    getMessages(Uid);
    MyFireBaseMessagingService.shownotification = true;
  }

  @Override
  protected void onStop() {
    super.onStop();
    isfirsttime = false;
    MyFireBaseMessagingService.shownotification = false;
  }

  private void sendnotificatioin(
      String title,
      String message,
      String token,
      NotificationType type,
      String receiverId,
      String conversationId) {

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    NotificationSender sender = new NotificationSender();

    Notification1 notification = new Notification1();
    notification.setTitle(title);
    notification.setBody(message);

    Data data = new Data();

    data.setType(type.toString());
    data.setEventId("");
    data.setConversationId(conversationId);
    data.setUserId(receiverId);

    sender.setNotification1(notification);
    sender.setTo(token);
    sender.setData(data);
    apiInterface
        .sendNotifcation(sender)
        .enqueue(
            new Callback<MyResponse>() {
              @Override
              public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {}
              }

              @Override
              public void onFailure(Call<MyResponse> call, Throwable t) {}
            });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (registration != null) {
      registration.remove();
    }

    if (registration1 != null) {
      registration1.remove();
    }
    if (registration2 != null) {
        registration2.remove();
    }
    if (registration3 != null) {
        registration3.remove();
    }
  }
}
