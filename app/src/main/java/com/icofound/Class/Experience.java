package com.icofound.Class;

import java.io.Serializable;

public class Experience implements Serializable {

    String id;
    String orgName;
    String titleName;
    String workDesc;
    String startDate;
    String endDate;
    boolean isCurrentWorkPlace;

    public boolean isCurrentWorkPlace() {
        return isCurrentWorkPlace;
    }

    public void setCurrentWorkPlace(boolean currentWorkPlace) {
        isCurrentWorkPlace = currentWorkPlace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getWorkDesc() {
        return workDesc;
    }

    public void setWorkDesc(String workDesc) {
        this.workDesc = workDesc;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
