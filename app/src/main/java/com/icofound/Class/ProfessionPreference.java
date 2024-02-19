package com.icofound.Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfessionPreference implements Serializable {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("ProfessionPref")
    @Expose
    String ProfessionPref;
    @SerializedName("skillPref")
    @Expose
    String skillPref;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfessionPref() {
        return ProfessionPref;
    }

    public void setProfessionPref(String professionPref) {
        ProfessionPref = professionPref;
    }

    public String getSkillPref() {
        return skillPref;
    }

    public void setSkillPref(String skillPref) {
        this.skillPref = skillPref;
    }
}
