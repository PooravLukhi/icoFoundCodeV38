package com.icofound.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.icofound.Activity.ProfileActivity;
import com.icofound.Class.Constant;
import com.icofound.Model.Connection;
import com.icofound.Model.Data;
import com.icofound.Model.MyResponse;
import com.icofound.Model.Notification1;
import com.icofound.Model.NotificationSender;
import com.icofound.Model.Post;
import com.icofound.Model.Swipes;
import com.icofound.Model.User;
import com.icofound.Notification.ApiClient;
import com.icofound.Notification.ApiInterface;
import com.icofound.Notification.NotificationType;
import com.icofound.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {


    Activity activity;
    ArrayList<User> pendinglist = new ArrayList<>();
    FirebaseFirestore firestore;
    String loginuid;
    HashMap<String, Boolean> swipeinfomap;
    HashMap<String, Boolean> connectioninfomap = new HashMap<>();
    User currentuser;


    public RequestAdapter(FragmentActivity activity, ArrayList<User> pendinglist, FirebaseFirestore firestore, String loginuid, HashMap<String, Boolean> swipeinfomap, User currentuser) {

        this.activity = activity;
        this.pendinglist = pendinglist;
        this.firestore = firestore;
        this.loginuid = loginuid;
        this.swipeinfomap = swipeinfomap;
        this.currentuser = currentuser;

    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list, parent, false);
        RequestAdapter.RequestViewHolder viewHolder = new RequestAdapter.RequestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(activity).load(pendinglist.get(position).getProfilePicLink()).placeholder(R.drawable.ic_person).into(holder.profile_pic);

        holder.name.setText(pendinglist.get(position).getName());

        holder.own_proffession_.setText(pendinglist.get(position).getMyProfession());

        holder.layRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, ProfileActivity.class);
                i.putExtra("frommain",false);
                i.putExtra("myprofile",false);
                i.putExtra("Uid",pendinglist.get(holder.getAdapterPosition()).getId());
                i.putExtra("loginuid", loginuid);
                activity.startActivity(i);
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getreciverconnectioninfo(pendinglist.get(position).getId(), firestore,pendinglist.get(position).getToken());

            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdislikeuserconnectioniunfo(pendinglist.get(position).getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return pendinglist.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_pic;
        TextView own_proffession_, name;
        ImageView accept, decline;
        LinearLayout layRequest;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_pic = itemView.findViewById(R.id.profile_pic);
            own_proffession_ = itemView.findViewById(R.id.own_proffession_);
            name = itemView.findViewById(R.id.name);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
            layRequest = itemView.findViewById(R.id.layRequest);
        }
    }

    public void getlikeuserconnectioniunfo(String receiverid, HashMap<String, Boolean> connectioninfomap, String token) {
        DocumentReference documentReference = firestore.collection(Constant.tableConnections).document(loginuid);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getData() != null){


//                    final ObjectMapper mapper = new ObjectMapper();
//                    connection = mapper.convertValue(documentSnapshot.getData(), Connection.class);

                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    Connection connection = new Connection();

                    if (mArraylist.get(Constant.id) != null) {
                        connection.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get(Constant.connectionInfoData) != null) {
                        connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                    }

                    for (Map.Entry<String, Boolean> entry : connection.getConnectionInfoData().entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(receiverid)) {

                            HashMap<String, Boolean> newhasmap = new HashMap<>(connection.getConnectionInfoData());
                            newhasmap.put(receiverid, true);
                            Connection connection1 = new Connection();
                            connection1.setId(loginuid);
                            connection1.setConnectionInfoData(newhasmap);

                            documentReference.set(connection1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                        }
                    }
                }
                likeuser(true, receiverid, connectioninfomap,token);
            }
        });

//        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (value.getData() != null) {
//                    Connection connection = new Connection();
//
//                    final ObjectMapper mapper = new ObjectMapper();
//                    connection = mapper.convertValue(value.getData(), Connection.class);
//
//                    for (Map.Entry<String, Boolean> entry : connection.getConnectioninfoData().entrySet()) {
//                        if (entry.getKey().equalsIgnoreCase(receiverid)) {
//
//                            HashMap<String, Boolean> newhasmap = new HashMap<>(connection.getConnectioninfoData());
//                            newhasmap.put(receiverid, true);
//                            Connection connection1 = new Connection();
//                            connection1.setReciverId(receiverid);
//                            connection1.setConnectioninfoData(newhasmap);
//
//                            documentReference.set(connection1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//
//                                }
//                            });
//
//                        }
//                    }
//                }
//                likeuser(true, receiverid, connectioninfomap);
//            }
//        });
    }

    public void likeuser(boolean issame, String receiverid, HashMap<String, Boolean> connectioninfomap, String token) {

        DocumentReference documentReference = firestore.collection(Constant.Swipes).document(loginuid);

        Swipes swipes = new Swipes();
        swipeinfomap.put(receiverid, true);
        swipes.setId(loginuid);
        swipes.setSwipeInfoData(swipeinfomap);

        documentReference.set(swipes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                DocumentReference documentReference1 = firestore.collection(Constant.tableConnections).document(receiverid);

                Connection connection = new Connection();
                connectioninfomap.put(loginuid, issame);
                connection.setId(receiverid);
                connection.setConnectionInfoData(RequestAdapter.this.connectioninfomap);

                documentReference1.set(connection).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

                sendnotificatioin(currentuser.getName(),"Accepted Request", token, NotificationType.accept);

            }
        });

    }

    public void getdislikeuserconnectioniunfo(String receiverid) {
        DocumentReference documentReference = firestore.collection(Constant.tableConnections).document(loginuid);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getData() != null){

//                    final ObjectMapper mapper = new ObjectMapper();
//                    connection = mapper.convertValue(documentSnapshot.getData(), Connection.class);

                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    Connection connection = new Connection();

                    if (mArraylist.get(Constant.id) != null) {
                        connection.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get(Constant.connectionInfoData) != null) {
                        connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                    }

                    for (Map.Entry<String, Boolean> entry : connection.getConnectionInfoData().entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(receiverid)) {

                            HashMap<String, Boolean> newhasmap = new HashMap<>(connection.getConnectionInfoData());
                            newhasmap.remove(receiverid);

                            Connection connection1 = new Connection();
                            connection1.setId(receiverid);
                            connection1.setConnectionInfoData(newhasmap);

                            documentReference.set(connection1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                        }
                    }
                }
                dislikeuser(receiverid);
            }
        });

//        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (value.getData() != null) {
//                    Connection connection = new Connection();
//
//                    final ObjectMapper mapper = new ObjectMapper();
//                    connection = mapper.convertValue(value.getData(), Connection.class);
//
//                    for (Map.Entry<String, Boolean> entry : connection.getConnectioninfoData().entrySet()) {
//                        if (entry.getKey().equalsIgnoreCase(receiverid)) {
//
//                            HashMap<String, Boolean> newhasmap = new HashMap<>(connection.getConnectioninfoData());
//                            newhasmap.remove(receiverid);
//
//                            Connection connection1 = new Connection();
//                            connection1.setReciverId(receiverid);
//                            connection1.setConnectioninfoData(newhasmap);
//
//                            documentReference.set(connection1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//
//                                }
//                            });
//
//                        }
//                    }
//                }
//                dislikeuser(receiverid);
//            }
//        });
    }

    public void dislikeuser(String receiverid) {

        DocumentReference documentReference = firestore.collection(Constant.Swipes).document(loginuid);

        Swipes swipes = new Swipes();
        swipeinfomap.put(receiverid, false);
        swipes.setId(loginuid);
        swipes.setSwipeInfoData(swipeinfomap);

        documentReference.set(swipes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

    }

    public void getreciverconnectioninfo(String reciverid, FirebaseFirestore firestore, String token) {

        DocumentReference documentReference = firestore.collection(Constant.tableConnections).document(reciverid);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getData() != null){

//                    final ObjectMapper mapper = new ObjectMapper();
//                    connection = mapper.convertValue(documentSnapshot.getData(), Connection.class);

                    Map<String, Object> mArraylist = documentSnapshot.getData();

                    Connection connection = new Connection();

                    if (mArraylist.get(Constant.id) != null) {
                        connection.setId((String) mArraylist.get(Constant.id));
                    }

                    if (mArraylist.get(Constant.connectionInfoData) != null) {
                        connection.setConnectionInfoData((HashMap<String, Boolean>) mArraylist.get(Constant.connectionInfoData));
                    }

                    connectioninfomap = connection.getConnectionInfoData();
                }

                getlikeuserconnectioniunfo(reciverid, connectioninfomap,token);
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


}
