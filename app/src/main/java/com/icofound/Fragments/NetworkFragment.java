package com.icofound.Fragments;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.icofound.Activity.MainScreenActivity;
import com.icofound.Adapters.ConnectionAdapter;
import com.icofound.Adapters.RequestAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Model.Connection;
import com.icofound.Model.Swipes;
import com.icofound.Model.User;
import com.icofound.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkFragment extends Fragment {

    TextView title, num_pending, num_connection;
    RecyclerView request_list, connection_list;
    FirebaseFirestore firestore;
    String loginuid;
    Map<String, Boolean> connectioninfo = new HashMap<>();
    ArrayList<User> pendinglist = new ArrayList<>();
    ArrayList<User> connectionlist = new ArrayList<>();
    HashMap<String, Boolean> swipeinfomap = new HashMap<>();
    HashMap<String, Boolean> connectioninfomap = new HashMap<>();
    ArrayList<User> userlist = new ArrayList<>();
    ArrayList<User> newUserList = new ArrayList<>();
    User currentuser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        title = view.findViewById(R.id.title);
        request_list = view.findViewById(R.id.request_list);
        connection_list = view.findViewById(R.id.connection_list);
        num_pending = view.findViewById(R.id.num_pending);
        num_connection = view.findViewById(R.id.num_connection);

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            loginuid = getArguments().getString("loginuid");
        }

        num_pending.setText("(0)");
        num_connection.setText("(0)");

        getalluserlist();

        getuserswipeinfo();

        TextPaint paint = title.getPaint();
        float width = paint.measureText("Settings");

        Shader textShader = new LinearGradient(0, 0, width, title.getTextSize(),
                new int[]{
                        Color.parseColor("#9E73FE"),
                        Color.parseColor("#838DFE"),
                        Color.parseColor("#8149FF"),
                }, null, Shader.TileMode.CLAMP);
        title.getPaint().setShader(textShader);

        return view;
    }


    public void getconnection(String loginuid) {
        firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection(Constant.tableConnections).document(loginuid);

        if (getActivity() != null){
            ((MainScreenActivity) requireActivity()).registration5 = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    pendinglist = new ArrayList<>();
                    connectionlist = new ArrayList<>();

                    if (value.getData() != null){

//                    final ObjectMapper mapper = new ObjectMapper();
//                    connection = mapper.convertValue(value.getData(), Connection.class);

                        Map<String, Object> mArraylist = value.getData();

                        Connection connection = new Connection();

                        if (mArraylist.get(Constant.id) != null) {
                            connection.setId((String) mArraylist.get(Constant.id));
                        }

                        if (mArraylist.get(Constant.connectionInfoData) != null) {
                            connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                        }

                        connectioninfo = connection.getConnectionInfoData();

                        for(Map.Entry<String, Boolean> entry : connectioninfo.entrySet()) {

                            if (!entry.getValue()){

                                for (int i = 0; i < newUserList.size(); i++) {
                                    if (newUserList.get(i).getId().equalsIgnoreCase(entry.getKey())){
                                        pendinglist.add(newUserList.get(i));
                                    }
                                }

                            } else if (entry.getValue()) {

                                for (int i = 0; i < newUserList.size(); i++) {
                                    if (newUserList.get(i).getId().equalsIgnoreCase(entry.getKey())) {
                                        connectionlist.add(newUserList.get(i));
                                    }
                                }
                            }
                        }

                        RequestAdapter adapter = new RequestAdapter(getActivity(), pendinglist, firestore, NetworkFragment.this.loginuid, swipeinfomap,currentuser);
                        request_list.setAdapter(adapter);

                        num_pending.setText("(" + pendinglist.size() + "" + ")");

                        ConnectionAdapter connectionAdapter = new ConnectionAdapter(getActivity(), connectionlist, loginuid);
                        connection_list.setAdapter(connectionAdapter);

                        num_connection.setText("(" + connectionlist.size() + "" + ")");


                    }
                }
            });
        }


    }


    private void getalluserlist() {

      firestore.collection(Constant.tableUsers).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                  @Override
                  public void onSuccess(QuerySnapshot value) {
                      userlist = new ArrayList<>();
                      newUserList = new ArrayList<>();
                      if (value.getDocuments() != null) {

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

                              if (mArraylist.get(i).get(Constant.showAge) != null){
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
                                  }
                                  else {
                                      user.setRatingCount((Long) mArraylist.get(i).get(Constant.ratingCount));
                                  }
                              }

                              user.setMyProfession((String) mArraylist.get(i).get(Constant.myProfession));
                              user.setSkillPreference((String) mArraylist.get(i).get(Constant.skillPreference));


                              if (mArraylist.get(i).get(Constant.tableExperience) != null) {
                                  if (mArraylist.get(i).get(Constant.tableExperience) instanceof String){

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

                                      HashMap<String,String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableProfessionPreference);

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


                              if (mArraylist.get(i).get(Constant.tableCoreValues) != null) {
                                  if (mArraylist.get(i).get(Constant.tableCoreValues) instanceof String) {

                                      CoreValues coreValues = new CoreValues();
                                      coreValues.setId("");
                                      coreValues.setWorkValues("");
                                      coreValues.setWorkValues("");

                                      user.setCoreValues(coreValues);

                                  } else {

                                      HashMap<String,String> hashmap = (HashMap<String, String>) mArraylist.get(i).get(Constant.tableCoreValues);

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

                              userlist.add(user);
                          }

                          ArrayList<String> blockids = new ArrayList<>();

                          for (User user : userlist){
                              if (user.getId().equalsIgnoreCase(loginuid)){
                                  currentuser = user;
                                  if(user.getBlockedUserIds() != null && user.getBlockedUserIds().size() > 0) {
                                      blockids.addAll(user.getBlockedUserIds());
                                  }
                                  break;

                              }
                          }

                          if (blockids != null && blockids.size() > 0) {
                              for (User user : userlist){

                                  if (!blockids.contains(user.getId())){
                                      newUserList.add(user);
                                  }

                              }
                          } else {
                              newUserList.addAll(userlist);
                          }

                          getconnection(loginuid);
                      }
                  }
              });

    }

    public void getuserswipeinfo() {
        DocumentReference documentReference = firestore.collection(Constant.Swipes).document(loginuid);

        ((MainScreenActivity) requireActivity()).registration6 = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                swipeinfomap = new HashMap<>();

                if (value.getData() != null) {
//                    final ObjectMapper mapper = new ObjectMapper();
//                    swipes1 = mapper.convertValue(value.getData(), Swipes.class);

                    Map<String, Object> mArraylist = value.getData();

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


    @Override
    public void onDetach() {
        super.onDetach();

        if ( ((MainScreenActivity) requireActivity()).registration5  != null){
            ((MainScreenActivity) requireActivity()).registration5 .remove();
        }

        if ( ((MainScreenActivity) requireActivity()).registration6 != null){
            ((MainScreenActivity) requireActivity()).registration6.remove();
        }
    }
}