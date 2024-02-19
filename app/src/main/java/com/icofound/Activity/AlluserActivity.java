package com.icofound.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.icofound.Adapters.AlluserAdapter;
import com.icofound.Class.Constant;
import com.icofound.Class.Utils;
import com.icofound.Model.Connection;
import com.icofound.Model.User;
import com.icofound.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class AlluserActivity extends AppCompatActivity {

    RecyclerView rvAlluser;
    ArrayList<User> new_userlist = new ArrayList<>();
    ArrayList<User> userlist = new ArrayList<>();
    String loginuid;
    AppCompatButton close;
    FirebaseFirestore firestore;
    HashMap<String, Boolean> connectioninfomap = new HashMap<>();
    User user;
    ArrayList<String> blockuserids = new ArrayList<>();
    ArrayList<User> new_filter_userList = new ArrayList<>();
    Utils utils;

    List<DocumentSnapshot> mArraylist = new ArrayList<>();
    ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluser);
        utils = new Utils(AlluserActivity.this);
        utils.intialise();

        rvAlluser = findViewById(R.id.rvAlluser);
        close = findViewById(R.id.close);
        firestore = FirebaseFirestore.getInstance();

        user = (User) getIntent().getSerializableExtra("user");

        if (user.getBlockedUserIds() != null && user.getBlockedUserIds().size() > 0) {
            blockuserids = user.getBlockedUserIds();
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loginuid = getIntent().getStringExtra("loginuid");
        userlist = (ArrayList<User>) getIntent().getSerializableExtra("userList");


       registration =  firestore.collection(Constant.tableConnections).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                new_userlist = new ArrayList<>();
                new_filter_userList = new ArrayList<>();
                if (value.getDocuments() != null) {

                    value.getDocuments().forEach(new Consumer<DocumentSnapshot>() {
                        @Override
                        public void accept(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.getData() != null) {
                                connectioninfomap = new HashMap<>();
                                new_userlist = new ArrayList<>();

//                                final ObjectMapper mapper = new ObjectMapper();
//                                Connection connection = mapper.convertValue(documentSnapshot.getData(), Connection.class);

                                Map<String, Object> mArraylist = documentSnapshot.getData();

                                Connection connection = new Connection();

                                if (mArraylist.get(Constant.id) != null) {
                                    connection.setId((String) mArraylist.get(Constant.id));
                                }

                                if (mArraylist.get(Constant.connectionInfoData) != null) {
                                    connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                                }

                                connectioninfomap = connection.getConnectionInfoData();

                                for (Map.Entry<String, Boolean> entry : connectioninfomap.entrySet()) {
                                    if (entry.getKey().equalsIgnoreCase(loginuid)) {
                                        if (entry.getValue()) {

                                            for (int i = 0; i < userlist.size(); i++) {
                                                if (userlist.get(i).getId().equalsIgnoreCase(documentSnapshot.getId())){
                                                    new_userlist.add(userlist.get(i));
                                                    break;
                                                }
                                            }

                                            for (int i = 0; i < new_userlist.size(); i++) {
                                                String userids;
                                                userids = new_userlist.get(i).getId();
                                                if (!blockuserids.contains(userids)) {
                                                    new_filter_userList.add(new_userlist.get(i));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });

                    AlluserAdapter adapter = new AlluserAdapter(AlluserActivity.this, new_filter_userList, loginuid);
                    rvAlluser.setAdapter(adapter);


                    if (new_filter_userList.isEmpty()){
                        rvAlluser.setVisibility(View.GONE);
                    }
                    else{
                        rvAlluser.setVisibility(View.VISIBLE);

                    }
                }
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }
}