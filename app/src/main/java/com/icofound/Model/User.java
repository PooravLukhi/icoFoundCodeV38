package com.icofound.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.icofound.Class.CoreValues;
import com.icofound.Class.Education;
import com.icofound.Class.Experience;
import com.icofound.Class.ProfessionPreference;
import com.icofound.Class.SocialMediaLinks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class User implements Serializable {

    String id;
    String name;
    String email;
    String profilePicLink;
    String bio;
    String gender;
    boolean showAge;
    String zipcode;
    String location;
    Object ratingCount;
    String myProfession;
    String skillPreference;
    ProfessionPreference professionPreference;
    ArrayList<Experience> experience;
    ArrayList<Education> education;
    ArrayList<SocialMediaLinks> socialMediaLinks;
    String inspire;
    String values;
    String bizValues;
    ArrayList<String> mySkills;
    ArrayList<String> blockedUserIds;
    Double policyTermsAckDate;
    String token;
    CoreValues coreValues;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicLink() {
        return profilePicLink;
    }

    public void setProfilePicLink(String profilePicLink) {
        this.profilePicLink = profilePicLink;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isShowAge() {
        return showAge;
    }

    public void setShowAge(boolean showAge) {
        this.showAge = showAge;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Object getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Object ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getMyProfession() {
        return myProfession;
    }

    public void setMyProfession(String myProfession) {
        this.myProfession = myProfession;
    }

    public String getSkillPreference() {
        return skillPreference;
    }

    public void setSkillPreference(String skillPreference) {
        this.skillPreference = skillPreference;
    }

    public String getInspire() {
        return inspire;
    }

    public void setInspire(String inspire) {
        this.inspire = inspire;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getBizValues() {
        return bizValues;
    }

    public void setBizValues(String bizValues) {
        this.bizValues = bizValues;
    }

    public ArrayList<String> getMySkills() {
        return mySkills;
    }

    public void setMySkills(ArrayList<String> mySkills) {
        this.mySkills = mySkills;
    }

    public ArrayList<String> getBlockedUserIds() {
        return blockedUserIds;
    }

    public void setBlockedUserIds(ArrayList<String> blockedUserIds) {
        this.blockedUserIds = blockedUserIds;
    }

    public Double getPolicyTermsAckDate() {
        return policyTermsAckDate;
    }

    public void setPolicyTermsAckDate(Double policyTermsAckDate) {
        this.policyTermsAckDate = policyTermsAckDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public ProfessionPreference getProfessionPreference() {
        return professionPreference;
    }

    public void setProfessionPreference(ProfessionPreference professionPreference) {
        this.professionPreference = professionPreference;
    }

    public ArrayList<Experience> getExperience() {
        return experience;
    }

    public void setExperience(ArrayList<Experience> experience) {
        this.experience = experience;
    }

    public CoreValues getCoreValues() {
        return coreValues;
    }

    public void setCoreValues(CoreValues coreValues) {
        this.coreValues = coreValues;
    }

    public ArrayList<SocialMediaLinks> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(ArrayList<SocialMediaLinks> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    public ArrayList<Education> getEducation() {
        return education;
    }

    public void setEducation(ArrayList<Education> education) {
        this.education = education;
    }
}
