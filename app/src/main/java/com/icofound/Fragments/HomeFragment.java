package com.icofound.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daprlabs.cardstack.SwipeDeck;
import com.daprlabs.cardstack.SwipeFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.icofound.Activity.ChatActivity;
import com.icofound.Activity.MainScreenActivity;
import com.icofound.Adapters.DeckAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Model.Connection;
import com.icofound.Model.Conversations;
import com.icofound.Model.Data;
import com.icofound.Model.MyResponse;
import com.icofound.Model.Notification1;
import com.icofound.Model.NotificationSender;
import com.icofound.Model.Swipes;
import com.icofound.Model.User;
import com.icofound.Notification.ApiClient;
import com.icofound.Notification.ApiInterface;
import com.icofound.Notification.NotificationType;
import com.icofound.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    EditText etSearch;
    RelativeLayout actionbar, etsearch_layout;
    SwipeFrameLayout profile_layout;
    LinearLayout no_profile;
    SwipeDeck cardStack;
    ImageView profile;
    ImageView messanger, filter, search, close;
    ImageView next, previous;
    TextView icUnread;
    Button dislike, like;
    FirebaseFirestore firestore;
    List<DocumentSnapshot> documentSnapshotList = new ArrayList<>();
    ArrayList<User> userList = new ArrayList<>();

    String loginuid;
    DeckAdapter adapter;
    public static  User user, receiveruser;
    HashMap<String, Boolean> swipeinfomap = new HashMap<>();
    HashMap<String, Boolean> connectioninfomap = new HashMap<>();
    ProgressDialog progressDialog;
    ArrayList<User> newuserlist = new ArrayList<>();
    private ArrayList<User> mTempData = new ArrayList<>();
    boolean issame = false;

    String skill_filter, profession_filter;
    List<DocumentSnapshot> marray = new ArrayList<>();
    ArrayList<Conversations> conversations_list = new ArrayList<>();
    ArrayList<Conversations> new_conversations_list = new ArrayList<>();
    ArrayList<User> new_userlist = new ArrayList<>();
    ArrayList<String> blockuserids = new ArrayList<>();
    SharedPreferences preferences;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
//        profile = view.findViewById(R.id.profile);
        next = view.findViewById(R.id.next);
        previous = view.findViewById(R.id.previous);
        messanger = view.findViewById(R.id.messanger);
        dislike = view.findViewById(R.id.dislike);
        like = view.findViewById(R.id.like);
        no_profile = view.findViewById(R.id.no_profile);
        profile_layout = view.findViewById(R.id.profile_layout);
        filter = view.findViewById(R.id.filter);
        icUnread = view.findViewById(R.id.icUnread);
        search = view.findViewById(R.id.search);
        actionbar = view.findViewById(R.id.actionbar);
        etsearch_layout = view.findViewById(R.id.etsearch_layout);
        etSearch = view.findViewById(R.id.etSearch);
        close = view.findViewById(R.id.close);

        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        preferences = requireActivity().getSharedPreferences("filter", Context.MODE_PRIVATE);

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            loginuid = getArguments().getString("loginuid");
        }

        getallUserlist();
        getMessages();
        getuserswipeinfo();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionbar.setVisibility(View.GONE);
                etsearch_layout.setVisibility(View.VISIBLE);
                showKeyboard(getActivity(), etSearch);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                actionbar.setVisibility(View.VISIBLE);
                etsearch_layout.setVisibility(View.GONE);
                hideKeyboard(getActivity(), view);
                etSearch.getText().clear();

            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearch.getText().toString().trim().isEmpty()) {
                    filterlist(MainScreenActivity.userList, false);
                } else {
                    filter(etSearch.getText().toString().trim());

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        messanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("user", user);
                i.putExtra("loginuid", loginuid);
                startActivity(i);
            }
        });

//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainScreenActivity.drawer.openDrawer(GravityCompat.START);
//
//            }
//        });

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {

                getdislikeuserconnectioniunfo(receiveruser);

                new CountDownTimer(1500, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    @Override
                    public void onFinish() {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }.start();
            }

            @Override
            public void cardSwipedRight(int position) {

                getlikeuserconnectioniunfo(receiveruser);

                new CountDownTimer(1500, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    @Override
                    public void onFinish() {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }.start();

            }

            @Override
            public void cardsDepleted() {
                no_profile.setVisibility(View.VISIBLE);
                profile_layout.setVisibility(View.GONE);

            }

            @Override
            public void cardActionDown() {

            }

            @Override
            public void cardActionUp() {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTempData.size() > 0) {
                    User user = mTempData.remove(0);
                    mTempData.add(user);
                    receiveruser = mTempData.get(0);
                }

                getreciverconnectioninfo();
                adapter = new DeckAdapter(getActivity(), mTempData, cardStack, loginuid);
                cardStack.setAdapter(adapter);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTempData.size() > 0) {
                    User user = mTempData.remove(mTempData.size() - 1);
                    mTempData.add(0, user);
                    receiveruser = mTempData.get(0);
                }

                getreciverconnectioninfo();
                adapter = new DeckAdapter(getActivity(), mTempData, cardStack, loginuid);
                cardStack.setAdapter(adapter);

            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardStack.swipeTopCardRight(500);
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardStack.swipeTopCardLeft(500);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FilterFragment fragment = new FilterFragment();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.linear, fragment, "Filterfragment")
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

            }
        });

        return view;
    }

    private void getallUserlist() {


        if (MainScreenActivity.userList.size() > 0) {

            for (int i = 0; i < MainScreenActivity.userList.size(); i++) {
                if (MainScreenActivity.userList.get(i).getId().equalsIgnoreCase(loginuid)) {
                    user = MainScreenActivity.userList.get(i);
                    MainScreenActivity.userList.remove(i);
                    break;
                }
            }

            if (MainScreenActivity.userList.size() > 0) {
                filterlist(MainScreenActivity.userList, false);
            } else {
                progressDialog.dismiss();
                profile_layout.setVisibility(View.GONE);
                no_profile.setVisibility(View.VISIBLE);
            }

        } else {
            progressDialog.dismiss();
            profile_layout.setVisibility(View.GONE);
            no_profile.setVisibility(View.VISIBLE);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection(Constant.tableUsers).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        documentSnapshotList = new ArrayList<>();
                        userList = new ArrayList<>();

                        assert value != null;
                        if (value.getDocuments() != null) {

                            documentSnapshotList = value.getDocuments();

                            List<DocumentSnapshot> mArraylist = value.getDocuments();

                            for (int i = 0; i < mArraylist.size(); i++) {
                                User user = new User();

                                if (mArraylist.get(i).get(Constant.id) != null) {
                                    user.setId((String) mArraylist.get(i).get(Constant.id));
                                }

                                if (mArraylist.get(i).get(Constant.name) != null) {
                                    user.setName((String) mArraylist.get(i).get(Constant.name));
                                }

                                if (mArraylist.get(i).get(Constant.bio) != null) {
                                    user.setBio((String) mArraylist.get(i).get(Constant.bio));
                                }

                                if (mArraylist.get(i).get(Constant.bizValues) != null) {
                                    user.setBizValues((String) mArraylist.get(i).get(Constant.bizValues));
                                }

                                if (mArraylist.get(i).get(Constant.blockedUserIds) != null) {
                                    user.setBlockedUserIds((ArrayList<String>) mArraylist.get(i).get(Constant.blockedUserIds));
                                }

                                if (mArraylist.get(i).get(Constant.email) != null) {
                                    user.setEmail((String) mArraylist.get(i).get(Constant.email));
                                }

                                if (mArraylist.get(i).get(Constant.profilePicLink) != null) {
                                    user.setProfilePicLink((String) mArraylist.get(i).get(Constant.profilePicLink));
                                }

                                if (mArraylist.get(i).get(Constant.gender) != null) {
                                    user.setGender((String) mArraylist.get(i).get(Constant.gender));
                                }

                                if (mArraylist.get(i).get(Constant.showAge) != null) {
                                    user.setShowAge((boolean) mArraylist.get(i).get(Constant.showAge));
                                }

                                if (mArraylist.get(i).get(Constant.zipcode) != null) {
                                    user.setZipcode((String) mArraylist.get(i).get(Constant.zipcode));
                                }

                                if (mArraylist.get(i).get(Constant.location) != null) {
                                    user.setLocation((String) mArraylist.get(i).get(Constant.location));
                                }

                                if (mArraylist.get(i).get(Constant.ratingCount) != null) {
                                    if (mArraylist.get(i).get(Constant.ratingCount) instanceof Float) {
                                        user.setRatingCount((Float) mArraylist.get(i).get(Constant.ratingCount));

                                    } else if (mArraylist.get(i).get(Constant.ratingCount) instanceof Double) {
                                        user.setRatingCount((Double) mArraylist.get(i).get(Constant.ratingCount));

                                    } else {
                                        user.setRatingCount((Long) mArraylist.get(i).get(Constant.ratingCount));
                                    }
                                }


                                user.setMyProfession((String) mArraylist.get(i).get(Constant.myProfession));
                                user.setSkillPreference((String) mArraylist.get(i).get(Constant.skillPreference));


                                if (mArraylist.get(i).get(Constant.tableExperience) != null) {
                                    if (mArraylist.get(i).get(Constant.tableExperience) instanceof String) {

                                        ArrayList<Experience> experienceArrayList = new ArrayList<>();
                                        Experience experience = new Experience();
                                        experience.setWorkDesc((String) mArraylist.get(i).get(Constant.tableExperience));
                                        experience.setTitleName("");
                                        experience.setId("");
                                        experience.setOrgName("");
                                        experience.setStartDate("");
                                        experience.setEndDate("");
                                        experience.setCurrentWorkPlace(false);

                                        experienceArrayList.add(experience);

                                        user.setExperience(experienceArrayList);


                                    } else {
                                        user.setExperience((ArrayList<Experience>) mArraylist.get(i).get(Constant.tableExperience));
                                    }

                                }


                                if (mArraylist.get(i).get(Constant.tableProfessionPreference) != null) {
                                    if (mArraylist.get(i).get(Constant.tableProfessionPreference) instanceof String) {

                                        ProfessionPreference professionPreference = new ProfessionPreference();
                                        professionPreference.setProfessionPref((String) mArraylist.get(i).get(Constant.tableProfessionPreference));
                                        professionPreference.setSkillPref("");
                                        professionPreference.setId("");

                                        user.setProfessionPreference(professionPreference);
                                    } else {

                                        HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableProfessionPreference);

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


                                if (mArraylist.get(i).get(Constant.tableCoreValues) != null) {
                                    if (mArraylist.get(i).get(Constant.tableCoreValues) instanceof String) {

                                        CoreValues coreValues = new CoreValues();
                                        coreValues.setId("");
                                        coreValues.setWorkValues("");
                                        coreValues.setWorkValues("");

                                        user.setCoreValues(coreValues);

                                    } else {

                                        HashMap<String, String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableCoreValues);

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

                                if (mArraylist.get(i).get(Constant.mySkills) != null) {
                                    user.setMySkills((ArrayList<String>) mArraylist.get(i).get(Constant.mySkills));
                                }

                                if (mArraylist.get(i).get(Constant.inspire) != null) {
                                    user.setInspire((String) mArraylist.get(i).get(Constant.inspire));
                                }

                                if (mArraylist.get(i).get(Constant.values) != null) {
                                    user.setValues((String) mArraylist.get(i).get(Constant.values));
                                }


                                if (mArraylist.get(i).get(Constant.policyTermsAckDate) != null) {
                                    if (mArraylist.get(i).get(Constant.policyTermsAckDate) instanceof Double) {
                                        user.setPolicyTermsAckDate((Double) mArraylist.get(i).get(Constant.policyTermsAckDate));
                                    } else {
                                        user.setPolicyTermsAckDate(Double.valueOf((Long) mArraylist.get(i).get(Constant.policyTermsAckDate)));
                                    }

                                }

                                if (mArraylist.get(i).get(Constant.token) != null) {
                                    user.setToken((String) mArraylist.get(i).get(Constant.token));
                                }

                                userList.add(user);
                            }

                            for (int i = 0; i < userList.size(); i++) {
                                if (userList.get(i).getId().equalsIgnoreCase(loginuid)) {
                                    user = userList.get(i);
                                    userList.remove(i);
                                    break;
                                }
                            }
//
//                            if (userList.size() > 0) {
//                                filterlist(userList, false);
//                            } else {
//                                progressDialog.dismiss();
//                            }
                        }
                    }
                });
            }
        }).start();


    }

    private void filterlist(ArrayList<User> userlist, boolean issecondtime) {

        firestore.collection(Constant.Swipes).document(loginuid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                newuserlist = new ArrayList<>();
                mTempData = new ArrayList<>();
                progressDialog.dismiss();

                if (documentSnapshot.getData() != null) {

//                    final ObjectMapper mapper = new ObjectMapper();
//                    swipes1 = mapper.convertValue(documentSnapshot.getData(), Swipes.class);

                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    Swipes swipes1 = new Swipes();

                    if (mArraylist.get(Constant.id) != null) {
                        swipes1.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get("swipeInfoData") != null) {
                        swipes1.setSwipeInfoData((HashMap<String, Boolean>) mArraylist.get("swipeInfoData"));
                    }

                    ArrayList<String> ids = new ArrayList<>();

                    for (Map.Entry<String, Boolean> entry : swipes1.getSwipeInfoData().entrySet()) {
                        if (!ids.contains(entry.getKey())) {
                            ids.add(entry.getKey());
                        }
                    }

                    for (int i = 0; i < userlist.size(); i++) {
                        String id = userlist.get(i).getId();
                        if (ids.contains(id)) {
                            continue;
                        } else {
                            newuserlist.add(userlist.get(i));
                        }
                    }
                    mTempData.addAll(newuserlist);
                } else {
                    mTempData.addAll(userlist);

                }

                if (mTempData.size() > 0) {

                    if (!issecondtime) {


                        skill_filter = preferences.getString("skill_filter", "").trim();
                        profession_filter = preferences.getString("profession_filter", "").trim();

                        boolean isfilter = false;

                        if (mTempData.size() > 0) {

                            if (!profession_filter.equalsIgnoreCase("")) {

                                List<User> professionuser = new ArrayList<>();
                                for (int i = 0; i < mTempData.size(); i++) {
                                    if (mTempData.get(i).getMyProfession() != null) {

                                        if (!mTempData.get(i).getMyProfession().equals(profession_filter)) {

                                            professionuser.add(mTempData.get(i));
                                        }
                                    } else {
                                        professionuser.add(mTempData.get(i));
                                    }
                                }

                                for (int i = 0; i < mTempData.size(); i++) {

                                    for (int i1 = 0; i1 < professionuser.size(); i1++) {

                                        if (professionuser.get(i1).getId().equals(mTempData.get(i).getId())) {
                                            mTempData.remove(i);
                                        } else if (professionuser.get(i1).getId().equals(null)) {
                                            mTempData.remove(i);
                                        }
                                    }
                                }

                                isfilter = true;
                            }

                            if (!skill_filter.equalsIgnoreCase("")) {

                                List<User> skilluser = new ArrayList<>();

                                String[] oneskill = skill_filter.split(",");

                                for (int i = 0; i < mTempData.size(); i++) {
                                    if (mTempData.get(i).getMySkills() != null) {
                                        boolean iscontain = false;
                                        for (int i1 = 0; i1 < oneskill.length; i1++) {
                                            if (mTempData.get(i).getMySkills().contains(oneskill[i1])) {
                                                iscontain = true;
                                                break;
                                            }
                                        }
                                        if (!iscontain) {
                                            if (!skilluser.contains(mTempData.get(i))) {
                                                skilluser.add(mTempData.get(i));
                                            }
                                        }

                                    } else {
                                        if (!skilluser.contains(mTempData.get(i))) {
                                            skilluser.add(mTempData.get(i));
                                        }
                                    }
                                }

                                for (int i1 = 0; i1 < skilluser.size(); i1++) {

                                    for (int i = 0; i < mTempData.size(); i++) {

                                        if (skilluser.get(i1).getId().equals(mTempData.get(i).getId())) {
                                            mTempData.remove(i);
                                        } else if (skilluser.get(i1).getId().equals(null)) {
                                            mTempData.remove(i);
                                        }
                                    }
                                }

                                isfilter = true;
                            }

                            mTempData.sort(new Comparator<User>() {
                                @Override
                                public int compare(User user, User t1) {
                                    Integer a = sortProfile(user);
                                    Integer b = sortProfile(t1);

                                    return a - b;
                                }
                            });

                            if (isfilter) {
                                if (mTempData.size() > 0) {
                                    no_profile.setVisibility(View.GONE);
                                    profile_layout.setVisibility(View.VISIBLE);
                                    adapter = new DeckAdapter(getActivity(), mTempData, cardStack, loginuid);
                                    cardStack.setAdapter(adapter);
                                } else {
                                    no_profile.setVisibility(View.VISIBLE);
                                    profile_layout.setVisibility(View.GONE);
                                }

                            } else {
                                no_profile.setVisibility(View.GONE);
                                profile_layout.setVisibility(View.VISIBLE);
                                adapter = new DeckAdapter(getActivity(), mTempData, cardStack, loginuid);
                                cardStack.setAdapter(adapter);
                            }
                        } else {
                            getallUserlist();
                            getuserswipeinfo();
                        }

//                        adapter = new DeckAdapter(getActivity(), mTempData, cardStack, loginuid);
//                        cardStack.setAdapter(adapter);
                    }

                } else {
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    no_profile.setVisibility(View.VISIBLE);
                    profile_layout.setVisibility(View.GONE);

                }

                try {
                    if (newuserlist.size() > 0) {
                        receiveruser = newuserlist.get(cardStack.getTop());
                    } else {
                        receiveruser = userlist.get(cardStack.getTop());
                    }
                    getreciverconnectioninfo();
                }catch (Exception e){

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

    }

    private Integer sortProfile(User user) {

        boolean isProfilePic = false;
        boolean isBio = false;

        if (!TextUtils.isEmpty(user.getProfilePicLink())) {
            isProfilePic = true;
        }

        if (!TextUtils.isEmpty(user.getBio())) {
            isBio = true;
        }

        if (isProfilePic && isBio) {
            return 1;
        }

        return 2;

    }

    public void getuserswipeinfo() {
        DocumentReference documentReference = firestore.collection(Constant.Swipes).document(loginuid);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                swipeinfomap = new HashMap<>();

                if (documentSnapshot.getData() != null) {
//                    final ObjectMapper mapper = new ObjectMapper();
//                    swipes1 = mapper.convertValue(documentSnapshot.getData(), Swipes.class);

                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    Swipes swipes1 = new Swipes();

                    if (mArraylist.get(Constant.id) != null) {
                        swipes1.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get("swipeInfoData") != null) {
                        swipes1.setSwipeInfoData((HashMap<String, Boolean>) mArraylist.get("swipeInfoData"));
                    }

                    swipeinfomap = swipes1.getSwipeInfoData();
                }
            }
        });

    }

    public void getreciverconnectioninfo() {

        DocumentReference documentReference = firestore.collection(Constant.tableConnections).document(receiveruser.getId());

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot value) {
                connectioninfomap = new HashMap<>();

                if (value.getData() != null) {

                    Map<String, Object> mArraylist = value.getData();

                    Connection connection = new Connection();

                    if (mArraylist.get(Constant.id) != null) {
                        connection.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get(Constant.connectionInfoData) != null) {
                        connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                    }

                    connectioninfomap = connection.getConnectionInfoData();
                }
            }
        });
    }

    public void getlikeuserconnectioniunfo(User receiveruser) {

        // remove snapshots
        DocumentReference documentReference = firestore.collection(Constant.tableConnections).document(user.getId());

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot value) {
                if (value.getData() != null) {


                    Map<String, Object> mArraylist = value.getData();

                    Connection connection = new Connection();

                    if (mArraylist.get(Constant.id) != null) {
                        connection.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get(Constant.connectionInfoData) != null) {
                        connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                    }

                    for (Map.Entry<String, Boolean> entry : connection.getConnectionInfoData().entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(receiveruser.getId())) {

                            issame = true;
                            HashMap<String, Boolean> newhasmap = new HashMap<>(connection.getConnectionInfoData());
                            newhasmap.put(receiveruser.getId(), true);
                            Connection connection1 = new Connection();
                            connection1.setId(receiveruser.getId());
                            connection1.setConnectionInfoData(newhasmap);

                            documentReference.set(connection1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                            break;

                        }
                    }
                }
                likeuser(issame, receiveruser);
            }
        });
    }

    public void likeuser(boolean issame, User receiveruser) {

        DocumentReference documentReference = firestore.collection(Constant.Swipes).document(user.getId());

        Swipes swipes = new Swipes();
        swipeinfomap.put(receiveruser.getId(), true);
        swipes.setId(user.getId());
        swipes.setSwipeInfoData(swipeinfomap);

        documentReference.set(swipes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                DocumentReference documentReference1 = firestore.collection(Constant.tableConnections).document(receiveruser.getId());

                Connection connection = new Connection();
                connectioninfomap.put(user.getId(), issame);
                connection.setId(receiveruser.getId());
                connection.setConnectionInfoData(connectioninfomap);

                documentReference1.set(connection).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        filterlist(MainScreenActivity.userList, true);
                        Toast.makeText(getContext(), "Invite Sent", Toast.LENGTH_SHORT).show();
                    }
                });

                sendnotificatioin(user.getName(), "Send Request", receiveruser.getToken(), NotificationType.request);

            }
        });

    }

    public void getdislikeuserconnectioniunfo(User receiveruser) {
        DocumentReference documentReference = firestore.collection(Constant.tableConnections).document(user.getId());

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getData() != null) {

                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    Connection connection = new Connection();

                    if (mArraylist.get(Constant.id) != null) {
                        connection.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get(Constant.connectionInfoData) != null) {
                        connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                    }

                    for (Map.Entry<String, Boolean> entry : connection.getConnectionInfoData().entrySet()) {

                        if (entry.getKey().equalsIgnoreCase(receiveruser.getId())) {

                            HashMap<String, Boolean> newhasmap = new HashMap<>(connection.getConnectionInfoData());
                            newhasmap.remove(receiveruser.getId());

                            Connection connection1 = new Connection();
                            connection1.setId(receiveruser.getId());
                            connection1.setConnectionInfoData(newhasmap);

                            documentReference.set(connection1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                        }
                    }
                }
                dislikeuser(receiveruser);
            }
        });
    }

    public void dislikeuser(User receiveruser) {

        DocumentReference documentReference = firestore.collection(Constant.Swipes).document(user.getId());

        Swipes swipes = new Swipes();
        swipeinfomap.put(receiveruser.getId(), false);
        swipes.setId(user.getId());
        swipes.setSwipeInfoData(swipeinfomap);

        documentReference.set(swipes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                filterlist(MainScreenActivity.userList, true);
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
        data.setEventId("");
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

    public void filter(String text) {

        ArrayList<User> searchUserlist = new ArrayList<>();

        for (int i = 0; i < mTempData.size(); i++) {
            if (mTempData.get(i).getName().toLowerCase().contains(text)) {
                searchUserlist.add(mTempData.get(i));
            }

        }

        if (searchUserlist.size() > 0) {
            adapter = new DeckAdapter(getActivity(), searchUserlist, cardStack, loginuid);
            cardStack.setAdapter(adapter);
            adapter.filterList(searchUserlist);
        } else {
            Toast.makeText(getContext(), "No result found", Toast.LENGTH_SHORT).show();
        }

    }

    private void getMessages() {

        CollectionReference reference = firestore.collection(Constant.conversations);

        ((MainScreenActivity) requireActivity()).registration8 = reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    return;
                }

                marray = new ArrayList<>();
                conversations_list = new ArrayList<>();
                new_conversations_list = new ArrayList<>();
                new_userlist = new ArrayList<>();


                if (value.getDocuments() != null) {
                    marray = value.getDocuments();

                    for (int i = 0; i < marray.size(); i++) {
                        Conversations conversations = marray.get(i).toObject(Conversations.class);
                        conversations_list.add(conversations);
                    }

                    for (int i = 0; i < conversations_list.size(); i++) {

                        ArrayList<String> userids = new ArrayList<>();

                        userids = conversations_list.get(i).getUserIDs();

                        if (userids.contains(loginuid)) {
                            new_conversations_list.add(conversations_list.get(i));
                        }
                    }

                    if (new_conversations_list.size() > 0) {
                        for (int i1 = 0; i1 < new_conversations_list.size(); i1++) {
                            ArrayList<String> userids = new ArrayList<>();

                            userids = new_conversations_list.get(i1).getUserIDs();

                            for (String id : userids) {

                                if (id != null) {
                                    if (!id.equalsIgnoreCase(loginuid)) {

                                        if (!blockuserids.contains(id)) {
                                            for (int i = 0; i < MainScreenActivity.userList.size(); i++) {
                                                if (id.equalsIgnoreCase(MainScreenActivity.userList.get(i).getId())) {
                                                    new_userlist.add(MainScreenActivity.userList.get(i));
                                                }
                                            }
                                        } else {
                                            new_conversations_list.remove(i1);
                                        }
                                    }
                                }


                            }

                            //getCount
                            int count = 0;
                            boolean isMatch = false;
                            for (int i = 0; i < new_conversations_list.size(); i++) {
                                Map<String, Boolean> isread = new HashMap<>();
                                isread = new_conversations_list.get(i).getIsRead();

                                for (Map.Entry<String, Boolean> entry : isread.entrySet()) {

                                    if (entry.getKey().equalsIgnoreCase(loginuid)) {
                                        if (!entry.getValue()) {
                                            count = count + 1;
                                            isMatch = true;
                                        }
                                    }
                                }

                            }

                            if (isMatch) {
                                icUnread.setVisibility(View.VISIBLE);
                                icUnread.setText(count + "");

                            } else {
                                icUnread.setVisibility(View.GONE);
                            }


                        }

                    }

                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();


        if (((MainScreenActivity) requireActivity()).registration8 != null) {
            ((MainScreenActivity) requireActivity()).registration8.remove();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, EditText etSearch) {
        etSearch.requestFocus();
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}