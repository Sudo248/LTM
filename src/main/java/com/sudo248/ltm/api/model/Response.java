package com.sudo248.ltm.api.model;

import com.sudo248.ltm.api.utils.GsonUtils;

import java.io.Serializable;

public class Response<T extends Serializable> implements Serializable {

    private int code;
    private String message;

    private T payload;

    public Response() {
    }

    public Response(int code, String message, T payload) {
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "{\n"+
                "code: " + code + "\n" +
                "message: " + message + "\n" +
                "payload: " + payload + "\n" +
                "}";
    }

    public String toJson() {
        return GsonUtils.toJson(this);
    }
}
