package com.sudo248.ltm.api.model.profile;

import java.io.Serializable;

/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 11:20 - 06/11/2022
 */
public class Profile implements Serializable {
    private static final long serialVersionUID = 6984228473170295300L;

    private Integer profileId;
    private String bio;
    private String name;
    private String image;
    private Boolean isActive;
    private Integer userId;
    private Boolean isFriended;

    public Profile() {
    }

    public Profile(Integer profileId, String bio, String name, String image, Boolean isActive, Integer userId, Boolean isFriend) {
        this.profileId = profileId;
        this.bio = bio;
        this.name = name;
        this.image = image;
        this.isActive = isActive;
        this.userId = userId;
        this.isFriended = isFriend;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean isFriended() {
        return isFriended;
    }

    public void setFriended(Boolean friended) {
        isFriended = friended;
    }
}
