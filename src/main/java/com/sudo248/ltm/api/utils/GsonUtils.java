package com.sudo248.ltm.api.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtils {
    private static Gson gson = new Gson();
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
}
