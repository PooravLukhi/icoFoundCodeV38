package com.icofound.Model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Conversations implements Serializable {

    String id;
    ArrayList<String> userIDs;
    Long timestamp;
    String lastMessage;
    Map<String, Boolean> isRead;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(ArrayList<String> userIDs) {
        this.userIDs = userIDs;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Map<String, Boolean> getIsRead() {
        return isRead;
    }

    public void setIsRead(Map<String, Boolean> isRead) {
        this.isRead = isRead;
    }
}
