/**
 * Lufax.com Inc.
 * Copyright (c) 2000-2015 All Rights Reserved.
 */
package com.dianxian.redis;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.*;

/**
 * 适用于Sentinel部署方案的配置参数。
 * 
 */
public class RedisCacheConfig extends BaseCacheConfig {

    // sentinel集群相关属性
    /** Sentinel地址列表，格式："ip:port;ip:port..." */
    private String sentinels  = "";
    /** Sentinel.conf配置的的masterName */
    private String masterName = "";

    /**
     * 构造基于Jedis的Redis客户端参数集。
     * 
     * @param sentinels     Sentinel服务地址，如有多台以分号间隔，必填。格式: ip:port;ip:port... ，比如: "172.17.40.51:26379;172.17.40.52:26379" 
     * @param masterName    在Sentinel上注册的Redis服务名称，用于区分一组Redis实例，必填。
     * @param prefix        缓存的key前缀，指定前缀后系统默认给所有key加上该前缀，用于区分存储在Redis实例上的不同业务，必填。 注意：不同缓存的prefix不能重复，比如session缓存和list缓存要区分开
     */
    public RedisCacheConfig(String sentinels, String masterName, String prefix) {
        super(prefix);
        this.sentinels = sentinels;
        this.masterName = masterName;
    }

    /**
     * 
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(";sentinels=" + this.getSentinels());
        sb.append(";masterName=" + this.getMasterName());
        return sb.toString();
    }

    /**
     * 
     * 
     * @return
     */
    public GenericObjectPoolConfig buildGenericObjectPoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(this.getMaxTotal());
        poolConfig.setMaxIdle(this.getMaxIdel());
        poolConfig.setMinIdle(this.getMinIdel());
        poolConfig.setBlockWhenExhausted(this.isBlockWhenExhausted());
        poolConfig.setMaxWaitMillis(this.getMaxWaitMillis());
        poolConfig.setLifo(this.isLifo());
        poolConfig.setTestOnBorrow(this.isTestOnBorrow());
        poolConfig.setTestOnReturn(this.isTestOnReturn());
        poolConfig.setTestWhileIdle(this.isTestWhileIdle());
        poolConfig.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        poolConfig.setNumTestsPerEvictionRun(this.getNumTestsPerEvictionRun());
        poolConfig.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
        return poolConfig;
    }
    
    /**
     * 
     */
    public Set<String> buildSentinelSet() {
        Set<String> sentinelSet = new HashSet<String>();
        String[] addrs = this.getSentinels().split(";");
        
        for (String addr : addrs) {
            sentinelSet.add(addr);
        }
        
        return sentinelSet;
    }

    //~~~ getter and setters

    public String getSentinels() {
        return sentinels;
    }

    /**
     * 设置Sentinel服务集群地址
     * 
     * @param sentinels
     */
    public void setSentinels(String sentinels) {
        this.sentinels = sentinels;
    }

    public String getMasterName() {
        return masterName;
    }

    /**
     * 设置Sentinel.conf配置的Redis服务名称
     * 
     * @param masterName
     */
    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }
}
