package com.dianxian.core.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Created by XuWenHao on 4/20/2016.
 */
public class JsonUtils {
    protected static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static Gson g;
    private static Gson glog;

    static {
        g = getGB().create();
        glog = getGB().disableHtmlEscaping().create();
    }

    public static String toJson(Object o) {
        return g.toJson(o);
    }

    public static String toUnescapedJson(Object o) {
        return glog.toJson(o);
    }

    /**
     * 默认通过Gson库转换
     * @param json
     * @param classOfT
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return g.fromJson(json, classOfT);
    }

    /**
     * 通过Gson库转换
     * @param json
     * @param typeToken
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return (T) g.fromJson(json, typeToken.getType());
    }

    /**
     * 通过Gson库转换
     * @param json
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Type type) {
        return (T) g.fromJson(json, type);
    }

    private static GsonBuilder getGB() {
        GsonBuilder gb = new GsonBuilder();
        return gb;
    }
}
