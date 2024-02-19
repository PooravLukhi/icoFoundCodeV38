package com.icofound.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.icofound.Adapters.CommentAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.Utils;
import com.icofound.Model.Comment;
import com.icofound.Model.Data;
import com.icofound.Model.MyResponse;
import com.icofound.Model.Notification1;
import com.icofound.Model.NotificationSender;
import com.icofound.Model.Post;
import com.icofound.Model.User;
import com.icofound.Notification.ApiClient;
import com.icofound.Notification.ApiInterface;
import com.icofound.Notification.NotificationType;
import com.icofound.Notification.Services.MyFireBaseMessagingService;
import com.icofound.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    User currentuser, postuser;
    Post post;
    String postid, currentuserid;
    ImageView back, tv_post_image;
    CircleImageView userimg, currentuserimg;
    TextView username, post_text, post_comt;
    RecyclerView comment_list;
    FirebaseFirestore firestore;
    EditText comment_txt;
    ListenerRegistration registration;
    List<DocumentSnapshot> documentSnapshotList = new ArrayList<>();
    ArrayList<Comment> commentlist = new ArrayList<>();
    ArrayList<Comment> new_commentlist = new ArrayList<>();
    ArrayList<Comment> new_filter_commentList = new ArrayList<>();
    Utils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        utils = new Utils(CommentActivity.this);
        utils.intialise();

        back = findViewById(R.id.back);
        tv_post_image = findViewById(R.id.tv_post_image);
        userimg = findViewById(R.id.userimg);
        username = findViewById(R.id.username);
        post_text = findViewById(R.id.post_text);
        comment_list = findViewById(R.id.comment_list);
        currentuserimg = findViewById(R.id.currentuserimg);
        post_comt = findViewById(R.id.post_comt);
        comment_txt = findViewById(R.id.comment_txt);
        firestore = FirebaseFirestore.getInstance();

        currentuserid = getIntent().getStringExtra("user");
        postid = getIntent().getStringExtra("post");

        getPost(postid, currentuserid);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        comment_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {

                    post_comt.setEnabled(true);
                    post_comt.setTextColor(getResources().getColor(R.color.selectionblue));
                } else {
                    post_comt.setEnabled(false);
                    post_comt.setTextColor(getResources().getColor(R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        post_comt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comment_txt.getText().toString().equalsIgnoreCase("")) {

                    postcomment();
                }
            }
        });
    }

    private void getPost(String postid, String currentuserid) {

        firestore.collection(Constant.tableUsers).document(currentuserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getData() != null) {
//                    final ObjectMapper mapper = new ObjectMapper();
//                    currentuser = mapper.convertValue(documentSnapshot.getData(), User.class);

                    Map<String, Object> mArraylist = documentSnapshot.getData();

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
                        currentuser.setBlockedUserIds((ArrayList<String>) mArraylist.get(Constant.blockedUserIds));
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
                            currentuser.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
                        }

                    }


                    if (mArraylist.get(Constant.tableProfessionPreference) != null) {
                        if (mArraylist.get(Constant.tableProfessionPreference) instanceof String) {

                            ProfessionPreference professionPreference = new ProfessionPreference();
                            professionPreference.setProfessionPref((String) mArraylist.get(Constant.tableProfessionPreference));
                            professionPreference.setSkillPref("");
                            professionPreference.setId("");

                            currentuser.setProfessionPreference(professionPreference);
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
                            currentuser.setPolicyTermsAckDate((Double) mArraylist.get(Constant.policyTermsAckDate));
                        } else {
                            currentuser.setPolicyTermsAckDate(Double.valueOf((Long) mArraylist.get(Constant.policyTermsAckDate)));
                        }

                    }

                    if (mArraylist.get(Constant.token) != null) {
                        currentuser.setToken((String) mArraylist.get(Constant.token));
                    }


                    Glide.with(CommentActivity.this).load(currentuser.getProfilePicLink()).placeholder(R.drawable.ic_person).into(currentuserimg);

                    firestore.collection("Posts").document(postid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot != null) {

//                                final ObjectMapper mapper = new ObjectMapper();
//                                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//                                post = mapper.convertValue(documentSnapshot.getData(), Post.class);

                                Map<String, Object> mArraylist = documentSnapshot.getData();

                                post = new Post();

                                if (mArraylist.get(Constant.id) != null) {
                                    post.setId((String) mArraylist.get(Constant.id));
                                }

                                if (mArraylist.get("userName") != null) {
                                    post.setUserName((String) mArraylist.get("userName"));
                                }

                                if (mArraylist.get("userId") != null) {
                                    post.setUserId((String) mArraylist.get("userId"));
                                }

                                if (mArraylist.get("postImgLink") != null) {
                                    post.setPostImgLink((String) mArraylist.get("postImgLink"));
                                }

                                if (mArraylist.get("post") != null) {
                                    post.setPost((String) mArraylist.get("post"));
                                }

                                if (mArraylist.get(Constant.profilePicLink) != null) {
                                    post.setProfilePicLink((String) mArraylist.get(Constant.profilePicLink));
                                }

                                if (mArraylist.get("timestamp") != null) {
                                    post.setTimestamp((long) mArraylist.get("timestamp"));
                                }

                                if (mArraylist.get("contentType") != null) {
                                    if (mArraylist.get("contentType") instanceof Integer) {
                                        post.setContentType((int) mArraylist.get("contentType"));
                                    }  else  {
                                        post.setContentType(Math.toIntExact((Long) mArraylist.get("contentType")));

                                    }

                                }

                                if (mArraylist.get("isApproved") != null) {
                                    post.setisApproved((boolean) mArraylist.get("isApproved"));
                                }

                                if (mArraylist.get("isExpanded") != null) {
                                    post.setisExpanded((boolean) mArraylist.get("isExpanded"));
                                }


                                Glide.with(CommentActivity.this).load(post.getProfilePicLink()).placeholder(R.drawable.ic_person).into(userimg);
                                username.setText(post.getUserName());
                                post_text.setText(post.getPost());
                                if (post.getPostImgLink() != null || !TextUtils.isEmpty(post.getPostImgLink())) {
                                    tv_post_image.setVisibility(View.VISIBLE);
                                    Glide.with(CommentActivity.this).load(post.getPostImgLink()).into(tv_post_image);
                                } else {
                                    tv_post_image.setVisibility(View.GONE);
                                }

                                getpostUser();
                            }
                        }
                    });
                }
            }
        });

    }

    private void getpostUser() {

        firestore.collection(Constant.tableUsers).document(post.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot != null) {
                    if (documentSnapshot.getData() != null) {
//                    final ObjectMapper mapper = new ObjectMapper();
//                    postuser = mapper.convertValue(documentSnapshot.getData(), User.class);
                        Map<String, Object> mArraylist = documentSnapshot.getData();
                        postuser = new User();

                        if (mArraylist.get(Constant.id) != null) {
                            postuser.setId((String) mArraylist.get(Constant.id));
                        }

                        if (mArraylist.get(Constant.name) != null) {
                            postuser.setName((String) mArraylist.get(Constant.name));
                        }

                        if (mArraylist.get(Constant.bio) != null) {
                            postuser.setBio((String) mArraylist.get(Constant.bio));
                        }

                        if (mArraylist.get(Constant.bizValues) != null) {
                            postuser.setBizValues((String) mArraylist.get(Constant.bizValues));
                        }

                        if (mArraylist.get(Constant.blockedUserIds) != null) {
                            postuser.setBlockedUserIds((ArrayList<String>) mArraylist.get(Constant.blockedUserIds));
                        }

                        if (mArraylist.get(Constant.email) != null) {
                            postuser.setEmail((String) mArraylist.get(Constant.email));
                        }

                        if (mArraylist.get(Constant.profilePicLink) != null) {
                            postuser.setProfilePicLink((String) mArraylist.get(Constant.profilePicLink));
                        }

                        if (mArraylist.get(Constant.gender) != null) {
                            postuser.setGender((String) mArraylist.get(Constant.gender));
                        }

                        if (mArraylist.get(Constant.showAge) != null) {
                            postuser.setShowAge((boolean) mArraylist.get(Constant.showAge));
                        }

                        if (mArraylist.get(Constant.zipcode) != null) {
                            postuser.setZipcode((String) mArraylist.get(Constant.zipcode));
                        }

                        if (mArraylist.get(Constant.location) != null) {
                            postuser.setLocation((String) mArraylist.get(Constant.location));
                        }

//                        if (mArraylist.get(Constant.ratingCount) != null){
//                            if (mArraylist.get(Constant.ratingCount) instanceof Float) {
//                                postuser.setRatingCount((Float) mArraylist.get(Constant.ratingCount));
//                            } else {
//                                postuser.setRatingCount((Long) mArraylist.get(Constant.ratingCount));
//                            }
//                        }

                        postuser.setMyProfession((String) mArraylist.get(Constant.myProfession));
                        postuser.setSkillPreference((String) mArraylist.get(Constant.skillPreference));


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

                                postuser.setExperience(experienceArrayList);


                            } else {
                                postuser.setExperience((ArrayList<Experience>) mArraylist.get(Constant.tableExperience));
                            }

                        }


                        if (mArraylist.get(Constant.tableProfessionPreference) != null) {
                            if (mArraylist.get(Constant.tableProfessionPreference) instanceof String) {

                                ProfessionPreference professionPreference = new ProfessionPreference();
                                professionPreference.setProfessionPref((String) mArraylist.get(Constant.tableProfessionPreference));
                                professionPreference.setSkillPref("");
                                professionPreference.setId("");

                                postuser.setProfessionPreference(professionPreference);
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
                                postuser.setProfessionPreference(professionPreference);
                            }
                        }


                        if (mArraylist.get(Constant.tableCoreValues) != null) {
                            if (mArraylist.get(Constant.tableCoreValues) instanceof String) {

                                CoreValues coreValues = new CoreValues();
                                coreValues.setId("");
                                coreValues.setWorkValues("");
                                coreValues.setWorkValues("");

                                postuser.setCoreValues(coreValues);

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

                                postuser.setCoreValues(coreValues);
                            }
                        }

                        if (mArraylist.get(Constant.mySkills) != null) {
                            postuser.setMySkills((ArrayList<String>) mArraylist.get(Constant.mySkills));
                        }

                        if (mArraylist.get(Constant.inspire) != null) {
                            postuser.setInspire((String) mArraylist.get(Constant.inspire));
                        }

                        if (mArraylist.get(Constant.values) != null) {
                            postuser.setValues((String) mArraylist.get(Constant.values));
                        }


                        if (mArraylist.get(Constant.policyTermsAckDate) != null) {
                            if (mArraylist.get(Constant.policyTermsAckDate) instanceof Double) {
                                postuser.setPolicyTermsAckDate((Double) mArraylist.get(Constant.policyTermsAckDate));
                            } else {
                                postuser.setPolicyTermsAckDate(Double.valueOf((Long) mArraylist.get(Constant.policyTermsAckDate)));
                            }

                        }

                        if (mArraylist.get(Constant.token) != null) {
                            postuser.setToken((String) mArraylist.get(Constant.token));
                        }

                        getAllComments();

                    }


                } else {
                    System.out.println("$$$$$$$$$$$$$$$ null");
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("$$$$$$$$$$$$$$$$$ " + e.getLocalizedMessage());
            }
        });
    }

    private void getAllComments() {

        DocumentReference reference = firestore.collection("Posts").document(post.getId());

        CollectionReference reference1 = reference.collection("Comments");

        registration = reference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                documentSnapshotList = new ArrayList<>();
                commentlist = new ArrayList<>();
                new_commentlist = new ArrayList<>();
                new_filter_commentList = new ArrayList<>();
                ArrayList<String> blockids = new ArrayList<>();


                if (value.getDocuments() != null) {

                    documentSnapshotList = value.getDocuments();

                    for (int i = 0; i < documentSnapshotList.size(); i++) {

                        Comment comment = documentSnapshotList.get(i).toObject(Comment.class);
                        commentlist.add(comment);

                    }

                    Collections.sort(commentlist, new Comparator<Comment>() {
                        @Override
                        public int compare(Comment o1, Comment o2) {
                            return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                        }
                    });

                    for (int i1 = 0; i1 < commentlist.size(); i1++) {
                        if (!commentlist.get(i1).isDeletedcomment()) {
                            new_commentlist.add(commentlist.get(i1));
                        }
                    }


                    if (currentuser.getBlockedUserIds() != null && currentuser.getBlockedUserIds().size() > 0) {
                        blockids.addAll(currentuser.getBlockedUserIds());
                    }

                    for (int i = 0; i < new_commentlist.size(); i++) {
                        String commentids;
                        commentids = new_commentlist.get(i).getSenderUId();

                        if (!blockids.contains(commentids)) {
                            new_filter_commentList.add(new_commentlist.get(i));
                        }
                    }

                        CommentAdapter adapter = new CommentAdapter(CommentActivity.this, new_filter_commentList, currentuser, postuser);
                        comment_list.setAdapter(adapter);


                }
            }
        });

    }

    private void postcomment() {
        String comment_str = comment_txt.getText().toString();
        comment_txt.getText().clear();
        post_comt.setEnabled(false);
        post_comt.setTextColor(getResources().getColor(R.color.gray));

        DocumentReference reference = firestore.collection("Posts").document(post.getId());

        CollectionReference reference1 = reference.collection("Comments");

        String key = reference1.document().getId();

        Comment comment = new Comment();
        comment.setId(key);
        comment.setComment(comment_str);
        comment.setDeletedcomment(false);
        Long tsLong = System.currentTimeMillis() / 1000;
        comment.setTimestamp(tsLong);
        comment.setSenderUId(currentuserid);
        comment.setSenderphoto(currentuser.getProfilePicLink());
        comment.setSendername(currentuser.getName());
        comment.setPostId(post.getId());

        reference1.document(key).set(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (postuser != null) {
                    if (!postuser.getId().equalsIgnoreCase(currentuserid)) {
                        sendnotificatioin(currentuser.getName(), "Commented on post: " + post.getPost().substring(0, Math.min(post.getPost().length(), 20)), postuser.getToken(), NotificationType.postupdate);
                    }

                    ArrayList<String> token_commentList = new ArrayList<>();
                    for (int i = 0; i < new_filter_commentList.size(); i++) {

                        if (token_commentList.size() > 0) {

                            if (!token_commentList.contains(new_filter_commentList.get(i).getSenderUId())) {
                                token_commentList.add(new_filter_commentList.get(i).getSenderUId());
                            }

                        } else {
                            token_commentList.add(new_filter_commentList.get(i).getSenderUId());
                        }
                    }

                    for (int i = 0; i < MainScreenActivity.userList.size(); i++) {

                        for (int i1 = 0; i1 < token_commentList.size(); i1++) {

                            if (MainScreenActivity.userList.get(i).getId().equalsIgnoreCase(token_commentList.get(i1))) {

                                if (token_commentList.get(i1).equalsIgnoreCase(postuser.getId()) ||
                                        token_commentList.get(i1).equalsIgnoreCase(currentuserid)) {

                                } else {
                                    sendnotificatioin(currentuser.getName(), "Commented on post: " + post.getPost().substring(0, Math.min(post.getPost().length(), 20)), MainScreenActivity.userList.get(i).getToken(), NotificationType.postupdate);
                                }
                            }

                        }
                    }

                }




            }
        });
    }

    private void sendnotificatioin(String title, String message, String token, NotificationType type) {

        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        NotificationSender sender = new NotificationSender();

        Notification1 notification = new Notification1();
        notification.setTitle(title);
        notification.setBody(message);

        Data data = new Data();

        data.setType(type.toString());
        data.setEventId(post.getId());
        data.setConversationId("");
        data.setUserId("");

        sender.setNotification1(notification);
        sender.setTo(token);
        sender.setData(data);
        apiInterface.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {

                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    public void deletecomment(String id) {
        firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Posts").document(post.getId());
        documentReference.collection("Comments").document(id).delete();
    }

    @Override
    protected void onStop() {
        super.onStop();

        MyFireBaseMessagingService.shownotification = false;

        if (registration != null) {
            registration.remove();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyFireBaseMessagingService.shownotification = true;
    }
}