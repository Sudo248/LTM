package com.sudo248.ltm.api.model;

import java.io.Serializable;

/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:23 - 23/10/2022
 */
public class Response<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -5957071120338937220L;
    private final long requestId;
    private int code;
    private String message;
    private T payload;

    public Response(long requestId) {
        this.requestId = requestId;
    }

    public Response(long requestId, int code, String message, T payload) {
        this.requestId = requestId;
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public long getRequestId() {
        return requestId;
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
}
