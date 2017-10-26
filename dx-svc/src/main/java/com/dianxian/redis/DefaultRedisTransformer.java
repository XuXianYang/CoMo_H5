/**
 * Lufax.com Inc.
 * Copyright (c) 2011-2014 All Rights Reserved.
 */
package com.dianxian.redis;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * 系统默认的转换器。
 * <p>仅适用于第一代Redis客户端{@link RedisCacheStore}
 * 
 * @author zhengjiaqing [http://jiaqing.me]
 * @version $Id: DefaultRedisCacheTransformer.java, v 0.1 Jun 5, 2014 6:21:44 PM zhengjiaqing Exp $
 */
public class DefaultRedisTransformer<T> implements CacheTransformer<T, String, String> {

    private Class<T> clazz;

    public DefaultRedisTransformer(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 说明：如果value为null则返回null。
     */
    @Override
    public T data(String value) {
        return new Gson().fromJson(value, clazz);
    }
    
    public List<T> data(String... values) {
        List<T> list = new ArrayList<T>();
        
        if (values == null || values.length == 0) {
            return list;
        }
        
        for (int i = 0; i < values.length; i++) {
            list.add(i, new Gson().fromJson(values[i], clazz));
        }
        
        return list;
    }
    
    @Override
    public int expiration(T data) {
        return -1;
    }

    @Override
    public String key(T data) {
        return null;
    }

    /**
     * 说明：如果data为null则返回空字符串“”。
     */
    @Override
    public String value(T data) {
        return new Gson().toJson(data);
    }
}
