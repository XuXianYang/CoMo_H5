/**
 * www.lufax.com Inc.
 * Copyright (c) 2011-2014 All Rights Reserved.
 */
package com.dianxian.redis;


/**
 * 
 * 
 * @author zhengjiaqing
 * @version $Id: RedisCacheConfigMBean.java, v 0.1 Jun 11, 2014 5:13:38 PM zhengjiaqing Exp $
 */
public interface RedisCacheConfigMBean extends CacheConfigMBean {

    //~~~ Codis属性
    public String getZookeepers();

    public void setZookeepers(String zookeepers);

    public String getProduct();

    public void setProduct(String product);

    public int getZkSessionTimeoutMs();

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs);

    public int getZkConnectionTimeoutMs();

    public void setZkConnectionTimeoutMs(int zkConnectionTimeoutMs);

    public String getEvictionPolicyClassName();

    public void setEvictionPolicyClassName(String evictionPolicyClassName);

    public long getSoftMinEvictableIdleTimeMillis();

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis);

    public int getMaxRetryCount();

    public void setMaxRetryCount(int maxRetryCount);

    //~~~ Sentinel属性

    public String getSentinels();

    public void setSentinels(String sentinels);

    public String getMasterName();

    public void setMasterName(String masterName);

    //~~~ 通用属性

    public int getMaxTotal();

    public void setMaxTotal(int maxTotal);

    public int getMaxIdel();

    public void setMaxIdel(int maxIdel);

    public int getMinIdel();

    public void setMinIdel(int minIdel);

    public boolean isBlockWhenExhausted();

    public void setBlockWhenExhausted(boolean blockWhenExhausted);

    public long getMaxWaitMillis();

    public void setMaxWaitMillis(long maxWaitMillis);

    public boolean isLifo();

    public void setLifo(boolean lifo);

    public boolean isTestOnBorrow();

    public void setTestOnBorrow(boolean testOnBorrow);

    public boolean isTestOnReturn();

    public void setTestOnReturn(boolean testOnReturn);

    public boolean isTestWhileIdle();

    public void setTestWhileIdle(boolean testWhileIdle);

    public long getTimeBetweenEvictionRunsMillis();

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis);

    public int getNumTestsPerEvictionRun();

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun);

    public long getMinEvictableIdleTimeMillis();

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis);
}
