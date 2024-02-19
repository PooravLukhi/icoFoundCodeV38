package com.icofound.Model;

import com.google.firebase.firestore.DocumentReference;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class Comment implements Serializable {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("comment")
    @Expose
    String comment;
    @SerializedName("deletedcomment")
    @Expose
    boolean deletedcomment;
    @SerializedName("timestamp")
    @Expose
    long timestamp;
//    @SerializedName("senderuser")
//    @Expose
//    DocumentReference senderuser;
//    @SerializedName("token")
//    @Expose
    String token;
    @SerializedName("sendername")
    @Expose
    String sendername;
    @SerializedName("senderphoto")
    @Expose
    String senderphoto;
    @SerializedName("senderUId")
    @Expose
    String senderUId;

    @SerializedName("postId")
    @Expose
    String postId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeletedcomment() {
        return deletedcomment;
    }

    public void setDeletedcomment(boolean deletedcomment) {
        this.deletedcomment = deletedcomment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

//    public DocumentReference getSenderuser() {
//        return senderuser;
//    }
//
//    public void setSenderuser(DocumentReference senderuser) {
//        this.senderuser = senderuser;
//    }
    //    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getSenderphoto() {
        return senderphoto;
    }

    public void setSenderphoto(String senderphoto) {
        this.senderphoto = senderphoto;
    }

    public String getSenderUId() {
        return senderUId;
    }

    public void setSenderUId(String senderUId) {
        this.senderUId = senderUId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
