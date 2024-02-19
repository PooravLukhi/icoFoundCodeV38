package com.icofound.Model;

public class SkillData {

    private String title;
    private String subTitle;
    private boolean isSelected = false;


    public SkillData(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String price) {
        this.subTitle = subTitle;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
