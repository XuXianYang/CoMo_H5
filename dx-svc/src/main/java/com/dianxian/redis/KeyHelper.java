/**
 * CompanyName.com Inc.
 * Copyright (c) 2000-2015 All Rights Reserved.
 */
package com.dianxian.redis;


/**
 * Key辅助工具。
 * 
 * @author zhengjiaqing [ http://jiaqing.me ]
 * @version $Id: KeyHelper.java, v 0.1 Aug 4, 2015 1:07:44 PM zhengjiaqing Exp $
 */
public class KeyHelper {

    private BaseCacheConfig redisConfig;

    public KeyHelper(BaseCacheConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    /**
     * 加工为Redis真实的key
     * <p>规则： shortName + ":" + key
     * 
     * @param key
     * @return
     */
    public String realKey(String key) {
        return redisConfig.getShortName() + ":" + key;
    }

    /**
     * 加工为Redis真实的Key
     * <p>规则： shortName + ":" + key
     * 
     * @param keys
     * @return
     */
    public String[] realKeys(String... keys) {
        String[] result = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            result[i] = realKey(keys[i]);
        }
        return result;
    }

    /**
     * 将keysvalues填充真实Redis的key
     * <p>规则：将数组的奇数位按照{@link #realKey(String)}填充
     * @param keysvalues
     */
    public void fillInRealKeys(String... keysvalues) {
        for (int i = 0; i < keysvalues.length; i += 2) {
            keysvalues[i] = realKey(keysvalues[i]);
        }
    }

    /**
     * 获取keysvalues中的key列表
     * 
     * @param keysvalues
     * @return
     */
    public String[] keysInKeysValues(String... keysvalues) {
        String[] result = new String[keysvalues.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = keysvalues[i << 1];
        }
        return result;
    }

    /**
     * 获取keysvalues中的value列表
     * 
     * @param keysvalues
     * @return
     */
    public String[] valuesInKeysValues(String... keysvalues) {
        String[] result = new String[keysvalues.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = keysvalues[(i << 1) + 1];
        }
        return result;
    }
}
