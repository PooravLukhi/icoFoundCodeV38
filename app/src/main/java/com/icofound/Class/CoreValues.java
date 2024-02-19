package com.icofound.Class;

import java.io.Serializable;

public class CoreValues implements Serializable {

    String id;
    String inspiration;
    String workCulture;
    String workValues;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInspiration() {
        return inspiration;
    }

    public void setInspiration(String inspiration) {
        this.inspiration = inspiration;
    }

    public String getWorkCulture() {
        return workCulture;
    }

    public void setWorkCulture(String workCulture) {
        this.workCulture = workCulture;
    }

    public String getWorkValues() {
        return workValues;
    }

    public void setWorkValues(String workValues) {
        this.workValues = workValues;
    }
}
