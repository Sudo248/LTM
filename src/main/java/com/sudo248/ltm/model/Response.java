package com.sudo248.ltm.model;

import com.sudo248.ltm.utils.GsonUtils;

import java.io.Serializable;

public class Response implements Serializable {
    private String path;
    private String method;

    public Response() {
    }

    public Response(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "{\n"+
                "path: " + path + "\n" +
                "method: " + method + "\n" +
                "}";
    }

    public String toJson() {
        return GsonUtils.toJson(this);
    }
}
