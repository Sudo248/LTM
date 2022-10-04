package com.sudo248.ltm.api.model.entity.user;

import com.sudo248.ltm.api.utils.GsonUtils;

public class UserResponse {
    private Integer code;
    private String error;
    private User user;

    public UserResponse() {
    }

    public UserResponse(Integer code, String error, User user) {
        this.code = code;
        this.error = error;
        this.user = user;
    }

    public UserResponse(Integer code, String error) {
        this.code = code;
        this.error = error;
    }

    public UserResponse(Integer code, User user) {
        this.code = code;
        this.user = user;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toJson() {
        return GsonUtils.toJson(this);
    }
}
