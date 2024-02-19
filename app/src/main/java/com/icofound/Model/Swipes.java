package com.icofound.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Swipes implements Serializable {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("swipeInfoData")
    @Expose
    HashMap<String, Boolean> swipeInfoData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Boolean> getSwipeInfoData() {
        return swipeInfoData;
    }

    public void setSwipeInfoData(HashMap<String, Boolean> swipeInfoData) {
        this.swipeInfoData = swipeInfoData;
    }
}
