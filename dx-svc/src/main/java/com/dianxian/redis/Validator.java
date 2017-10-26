/**
 * www.lufax.com Inc.
 * Copyright (c) 2011-2014 All Rights Reserved.
 */
package com.dianxian.redis;

import java.util.Map;
import java.util.Set;


/**
 * 参数验证工具类。
 * 
 * @author zhengjiaqing
 * @version $Id: Validator.java, v 0.1 Jun 10, 2014 3:26:44 PM zhengjiaqing Exp $
 */
public class Validator {

    public static boolean checkKeysValues(KeysValues keysValues) {
        if (keysValues == null || !keysValues.valid()) {
            return false;
        }
        return true;
    }

    public static boolean checkKeysValues(String... keysvalues) {
        if (!checkString(keysvalues)) {
            return false;
        }
        
        if (keysvalues.length % 2 != 0) {
            return false;
        }
        
        return true;
    }
    
    
    public static boolean checkString(String... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }
        for (String key : keys) {
            if (!checkString(key)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkString(String key) {
        if (null == key || key.trim().equals("")) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    public static boolean checkSet(Set items) {
        if (null == items || items.size() == 0) {
            return false;
        }
        for (Object item : items) {
            if (null == item) {
                return false;
            }
            if ((item instanceof String) && ((String) item).trim().equals("")) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkMap(Map<String, String> map) {
        if (null == map || map.size() == 0) {
            return false;
        }
        for (String key : map.keySet()) {
            if (map.get(key) == null) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean checkMapInDouble(Map<String, Double> map) {
        if (null == map || map.size() == 0) {
            return false;
        }
        for (String key : map.keySet()) {
            if (map.get(key) == null) {
                return false;
            }
        }
        return true;
    }
    
    @SuppressWarnings("rawtypes")
    public static boolean checkClass(Class clazz) {
        if (null == clazz) {
            return false;
        }
        return true;
    }
}
