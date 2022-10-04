package com.sudo248.ltm.api.model;

import java.io.Serializable;
import java.util.Map;

public class Request<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 3489076719896769175L;
    protected String path;
    protected RequestMethod method;
    protected Map<String, String> params;
    protected Map<String, String> queries;
    protected T payload;

    public Request() {
    }

    public Request(String path, RequestMethod method, Map<String, String> params, Map<String, String> queries) {
        this.path = path;
        this.method = method;
        this.params = params;
        this.queries = queries;
    }

    public Request(String path, RequestMethod method, Map<String, String> params, Map<String, String> queries, T payload) {
        this.path = path;
        this.method = method;
        this.params = params;
        this.queries = queries;
        this.payload = payload;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod requestMethod) {
        this.method = requestMethod;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
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
                "path: " + path + "\n" +
                "method: " + method + "\n" +
                "params: " + params + "\n" +
                "queries: " + queries + "\n" +
                "payload: " + payload + "\n" +
                "}";
    }
}
