/**
 * Lufax.com Inc.
 * Copyright (c) 2000-2015 All Rights Reserved.
 */
package com.dianxian.redis;

import java.util.Map;
import java.util.TreeMap;

/**
 * Redis批量写入的Key-Value。
 * 
 * @author zhengjiaqing [ http://jiaqing.me ]
 * @version $Id: KeysValues.java, v 0.1 Aug 4, 2015 10:38:33 AM zhengjiaqing Exp $
 */
public class KeysValues<T> {

    private final Map<String, T> mapping = new TreeMap<String, T>();

    /**
     * 自检。
     * 
     * @return
     */
    public boolean valid() {
       if (mapping.size() == 0) {
           return false;
       }
       
       for (String key : mapping.keySet()) {
           if (mapping.get(key) == null) {
               return false;
           }
       }
       
       return true;
    }
    
    /**
     * 添加Key-Value。
     * </p>安添加顺序排序
     * 
     * @param key
     * @param value
     */
    public void add(String key, T value) {
        mapping.put(key, value);
    }

    /**
     * 获取整个Key-Value集合
     * 
     * @return
     */
    public Map<String, T> getKeysValues() {
        return this.mapping;
    }
}
