package com.sudo248.ltm.model.entity.user;

import com.google.gson.GsonBuilder;
import com.sudo248.ltm.utils.GsonUtils;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 6367465053150130226L;

    private Long id;
    private String name;
    private String dob;

    public User() {
    }

    public User(Long id, String name, String dob) {
        this.id = id;
        this.name = name;
        this.dob = dob;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public static User fromJson(String json) {
        return GsonUtils.fromJson(json, User.class);
    }

    @Override
    public String toString() {
        return "{id: " + id + "\n"+
                "name: " + name + "\n" +
                "dob: " + dob +"\n"+
                "}";
    }
}
