package com.icofound.Class;

import java.io.Serializable;

public class SocialMediaLinks implements Serializable {

    String id;
    String name;
    String socialMediaLink;

    public SocialMediaLinks(String id, String name, String socialMediaLink) {
        this.id = id;
        this.name = name;
        this.socialMediaLink = socialMediaLink;
    }
    public SocialMediaLinks() {

    }

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

    public String getSocialMediaLink() {
        return socialMediaLink;
    }

    public void setSocialMediaLink(String socialMediaLink) {
        this.socialMediaLink = socialMediaLink;
    }
}
