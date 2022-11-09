package com.sudo248.ltm.api.model.entities;

import com.sudo248.ltm.api.model.profile.Profile;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "profile")
public class ProfileEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bio;
    private String name;
    @Lob
    private String image;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "user_id")
    private Integer userId;

    public ProfileEntity() {
    }

    public ProfileEntity(Integer id, String bio, String name, String image, Boolean isActive, Integer userId) {
        this.id = id;
        this.bio = bio;
        this.name = name;
        this.image = image;
        this.isActive = isActive;
        this.userId = userId;
    }

    public ProfileEntity(String bio, String name, String image, Boolean isActive, Integer userId) {
        this.bio = bio;
        this.name = name;
        this.image = image;
        this.isActive = isActive;
        this.userId = userId;
    }

    public Profile toProfile(Boolean isFriended) {
        return new Profile(
                id,
                bio,
                name,
                image,
                isActive,
                userId,
                isFriended
        );
    }

    public static ProfileEntity fromProfile(Profile profile) {
        return new ProfileEntity(
                profile.getProfileId(),
                profile.getBio(),
                profile.getName(),
                profile.getImage(),
                profile.getActive(),
                profile.getUserId()
        );
    }
}
