package com.icofound.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.icofound.Activity.CommentActivity;
import com.icofound.Activity.PostActivity;
import com.icofound.Activity.ProfileActivity;
import com.icofound.Adapters.UserAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Model.Comment;
import com.icofound.Model.Like;
import com.icofound.Model.Post;
import com.icofound.Model.User;
import com.icofound.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoriesFragment extends Fragment {

    RecyclerView rvUser;
    LinearLayout post, myprofile;
    String loginuid;
    FirebaseFirestore firestore;
    List<DocumentSnapshot> documentSnapshotList = new ArrayList<>();
    User user;
    TextView tv_name;
    RoundedImageView imageView;
    ProgressBar progress;
    UserAdapter adapter;
    List<Post> postList = new ArrayList<>();
    ArrayList<Post> new_postList = new ArrayList<>();
    ArrayList<Post> new_filter_postList = new ArrayList<>();
    ListenerRegistration registration, registration1;

    ArrayList<Like> likeList = new ArrayList<>();
    ArrayList<Comment> commentList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stories, container, false);

        rvUser = view.findViewById(R.id.rvUser);
        post = view.findViewById(R.id.post);
        tv_name = view.findViewById(R.id.tv_name);
        imageView = view.findViewById(R.id.imageView);
        progress = view.findViewById(R.id.progress);
        myprofile = view.findViewById(R.id.myprofile);

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            loginuid = getArguments().getString("loginuid");
        }


        registration = firestore.collection(Constant.tableUsers).document(loginuid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value.getData() != null) {

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

                    tv_name.setText(user.getName());

                    Glide.with(requireActivity()).load(user.getProfilePicLink()).placeholder(R.drawable.ic_person).into(imageView);

                    getAllPostList();
                }
            }
        });

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra("frommain", false);
                i.putExtra("myprofile", true);
                i.putExtra("Uid", loginuid);
                startActivity(i);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


        return view;
    }

    public void getAllPostList() {

        registration1 = firestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                documentSnapshotList = new ArrayList<>();
                postList = new ArrayList<>();
                new_postList = new ArrayList<>();
                new_filter_postList = new ArrayList<>();
                ArrayList<String> blockIds = new ArrayList<>();


                if (value != null) {

                    documentSnapshotList = value.getDocuments();

                    for (int i = 0; i < documentSnapshotList.size(); i++) {
                        Post post = documentSnapshotList.get(i).toObject(Post.class);
                        postList.add(post);
                    }

                    Collections.sort(postList, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                        }
                    });

                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getisApproved() != null) {
                            if (postList.get(i).getisApproved()) {
                                new_postList.add(postList.get(i));
                            } else {
                                if (postList.get(i).getUserId().equalsIgnoreCase(user.getId())) {
                                    new_postList.add(postList.get(i));
                                }
                            }
                        }
                    }

                    if (user.getBlockedUserIds() != null && user.getBlockedUserIds().size() > 0) {
                        blockIds.addAll(user.getBlockedUserIds());
                    }

                    for (int i = 0; i < new_postList.size(); i++) {
                        String postids;
                        postids = new_postList.get(i).getUserId();

                        if (!blockIds.contains(postids)) {
                            new_filter_postList.add(new_postList.get(i));
                        }
                    }

                    getAllLike(new_filter_postList);

                }
            }
        });
    }

    public void deletepost(String id) {
        firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Posts").document(id);
        documentReference.delete();
    }

    public void postLike(Post post, User user) {
        firestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firestore.collection("Posts").document(post.getId());

        CollectionReference reference1 = reference.collection("Like");

        double currentTimestamp = System.currentTimeMillis() / 1000;

        Like like = new Like();
        like.setUid(user.getId());
        like.setUserName(user.getName());
        like.setProfileImage(user.getProfilePicLink());
        like.setCreatedAt(currentTimestamp);
        like.setUpdatedAt(currentTimestamp);
        like.setPostId(post.getId());
        like.setDeletedLike(false);
        like.setSenderId(post.getUserId());

        reference1.document(user.getId()).set(like).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void deleteLike(String id, User user) {
        firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Posts").document(id);
        documentReference.collection("Like").document(user.getId()).delete();
    }

    public void getAllLike(ArrayList<Post> new_filter_postList) {

        List<Task<QuerySnapshot>> new_tasks = new ArrayList<>();
        likeList = new ArrayList<>();
        commentList = new ArrayList<>();

        for (Post b : new_filter_postList) {
            Task<QuerySnapshot> q = firestore
                    .collection("Posts")
                    .document(b.getId())
                    .collection("Like")
                    .get();

            new_tasks.add(q);
        }
        for (Post b : new_filter_postList) {
            Task<QuerySnapshot> q = firestore
                    .collection("Posts")
                    .document(b.getId())
                    .collection("Comments")
                    .get();

            new_tasks.add(q);
        }


        Tasks.whenAllComplete(new_tasks).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {

                likeList.clear();
                commentList.clear();
                try {
                    for (Task<QuerySnapshot> task2 : new_tasks) {
                        QuerySnapshot snap = task2.getResult();
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            Like like = doc.toObject(Like.class);
                            if (like.getUserName() != null && like.getPostId() != null)
                            likeList.add(like);
                        }
                    }
                }catch (Exception e){

                }
            for (Task<QuerySnapshot> task2 : new_tasks) {
                    QuerySnapshot snap = task2.getResult();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Comment comment = doc.toObject(Comment.class);
                        if (comment.getSendername() != null && comment.getPostId() != null)
                            commentList.add(comment);
                    }
                }

                adapter = new UserAdapter(getActivity(), new_filter_postList, user, getParentFragment(), likeList , commentList);
                rvUser.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

//        for (int i = 0; i < new_filter_postList.size(); i++) {
//
//            DocumentReference reference = firestore.collection("Posts").document(new_filter_postList.get(i).getId());
//
//            CollectionReference reference1 = reference.collection("Like");
//
//            reference1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                @Override
//                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
////                    likeList = new ArrayList<>();
//
//                    if (queryDocumentSnapshots.getDocuments() != null) {
//                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
//                            Like like = queryDocumentSnapshots.getDocuments().get(i).toObject(Like.class);
//                            likeList.add(like);
//                        }
//                    }
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                }
//            });
//        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
        }

        if (registration1 != null) {
            registration1.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllPostList();
    }
}