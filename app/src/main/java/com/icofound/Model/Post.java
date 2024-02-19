package com.icofound.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Post implements Serializable {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("userName")
    @Expose
    String userName;
    @SerializedName("userId")
    @Expose
    String userId;
    @SerializedName("postImgLink")
    @Expose
    String postImgLink;
    @SerializedName("post")
    @Expose
    String post;
    @SerializedName("profilePicLink")
    @Expose
    String profilePicLink;
    @SerializedName("timestamp")
    @Expose
    long timestamp;
    @SerializedName("contentType")
    @Expose
    Integer contentType;
    @SerializedName("isApproved")
    @Expose
    Boolean isApproved;

    @SerializedName("isExpanded")
    @Expose
    Boolean isExpanded;


    @SerializedName("Like")
    @Expose
    List<Like> Like;


    @SerializedName("Comments")
    @Expose
    List<Comment> Comments;

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public List<com.icofound.Model.Like> getLike() {
        return Like;
    }

    public void setLike(List<com.icofound.Model.Like> like) {
        Like = like;
    }

    public List<Comment> getComments() {
        return Comments;
    }

    public void setComments(List<Comment> comments) {
        Comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostImgLink() {
        return postImgLink;
    }

    public void setPostImgLink(String postImgLink) {
        this.postImgLink = postImgLink;
    }

    public String getProfilePicLink() {
        return profilePicLink;
    }

    public void setProfilePicLink(String profilePicLink) {
        this.profilePicLink = profilePicLink;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Boolean getisApproved() {
        return isApproved;
    }

    public void setisApproved(Boolean approved) {
        isApproved = approved;
    }

    public Boolean getisExpanded() {
        return isExpanded;
    }

    public void setisExpanded(Boolean expanded) {
        isExpanded = expanded;
    }
}
