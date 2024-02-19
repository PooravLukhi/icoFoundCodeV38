package com.icofound.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Connection implements Serializable {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("connectionInfoData")
    @Expose
    HashMap<String, Boolean> connectionInfoData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Boolean> getConnectionInfoData() {
        return connectionInfoData;
    }

    public void setConnectionInfoData(HashMap<String, Boolean> connectionInfoData) {
        this.connectionInfoData = connectionInfoData;
    }
}
