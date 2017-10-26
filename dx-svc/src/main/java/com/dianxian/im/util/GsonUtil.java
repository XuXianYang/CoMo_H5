package com.dianxian.im.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtil {

    private static Gson gson = new Gson();

    public static String toJson(Object obj, Type type) {
        return gson.toJson(obj, type);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static Object fromJson(String str, Type type) {
        return gson.fromJson(str, type);
    }

    public static <T> T fromJson(String str, Class<T> type) {
        return gson.fromJson(str, type);
    }
}  
