/**
 * www.lufax.com Inc.
 * Copyright (c) 2011-2014 All Rights Reserved.
 */
package com.dianxian.redis;



/**
 * 基础配置参数。
 * 
 */
public abstract class BaseCacheConfig extends CacheConfig implements RedisCacheConfigMBean {

    // 连接相关属性
    protected int     timeout                       = RedisConstants.DEF_TIMEOUT;
    protected String  password                      = RedisConstants.DEF_PASSWORD;

    // 连接池相关属性：
    protected int     maxTotal                      = RedisConstants.DEF_MAX_TOTAL;
    protected int     maxIdel                       = RedisConstants.DEF_MAX_TOTAL;
    protected int     minIdel                       = RedisConstants.DEF_MIN_IDEL;
    protected boolean blockWhenExhausted            = RedisConstants.DEF_BLOCK_WHEN_EXHAUSTED;
    protected long    maxWaitMillis                 = RedisConstants.DEF_MAX_WAIT_MILLIS;
    protected boolean lifo                          = RedisConstants.DEF_LIFO;
    protected boolean testOnBorrow                  = RedisConstants.DEF_TEST_ON_BORROW;
    protected boolean testOnReturn                  = RedisConstants.DEF_TEST_ON_RETURN;
    protected boolean testWhileIdle                 = RedisConstants.DEF_TEST_WHILE_IDLE;
    protected long    timeBetweenEvictionRunsMillis = RedisConstants.DEF_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    protected int     numTestsPerEvictionRun        = RedisConstants.DEF_NUM_TESTS_PER_EVICTION_RUN;
    protected long    minEvictableIdleTimeMillis    = RedisConstants.DEF_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * 
     * @param prefix 
     */
    public BaseCacheConfig(String prefix) {
        super(prefix, prefix, -1);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(";timeout=" + this.timeout);
        sb.append(";password=" + MaskUtils.maskAll(this.password));
        sb.append(";maxTotal=" + this.maxTotal);
        sb.append(";maxIdle=" + this.maxIdel);
        sb.append(";minIdle=" + this.minIdel);
        sb.append(";blockWhenExhausted=" + this.blockWhenExhausted);
        sb.append(";maxWaitMillis=" + this.maxWaitMillis);
        sb.append(";lifo=" + this.lifo);
        sb.append(";testOnBorrow=" + this.testOnBorrow);
        sb.append(";testOnReturn=" + this.testOnReturn);
        sb.append(";testWhileIdle=" + this.testWhileIdle);
        sb.append(";timeBetweenEvictionRunsMillis=" + this.timeBetweenEvictionRunsMillis);
        sb.append(";numTestsPerEvictionRun=" + this.numTestsPerEvictionRun);
        sb.append(";minEvictableIdleTimeMillis=" + this.minEvictableIdleTimeMillis);
        return sb.toString();
    }

    //~~~ getter and setters

    public int getMaxTotal() {
        return maxTotal;
    }

    /**
     * 设置连接池最大连接数，默认10
     * 
     * @param maxTotal
     */
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdel() {
        return maxIdel;
    }

    /**
     * 设置连接池最大空闲数，默认8
     * 
     * @return
     */
    public void setMaxIdel(int maxIdel) {
        this.maxIdel = maxIdel;
    }

    public int getMinIdel() {
        return minIdel;
    }

    /**
     * 设置连接池最小连接数，默认2
     * 
     * @param minIdel
     */
    public void setMinIdel(int minIdel) {
        this.minIdel = minIdel;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    /**
     * 连接池满时是否等待， true：等待maxWaitMillis指定时间， false：直接拒绝， 默认 true
     * 
     * @param blockWhenExhausted
     */
    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    /**
     * 连接池满时的等待时间，单位毫秒，默认5秒
     * 
     * @param maxWaitMillis
     */
    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public boolean isLifo() {
        return lifo;
    }

    /**
     * 是否使用LILO队列管理链接，默认false
     * 
     * @param lifo
     */
    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    /**
     * 是否在获取连接时测试连接可用性，默认false
     * 
     * @param testOnBorrow
     */
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    /**
     * 是否在归还连接时测试可用性，默认false
     * 
     * @param testOnReturn
     */
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    /**
     * 是否在连接空闲时测试可用性，默认true
     * 
     * @param testWhileIdle
     */
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    /**
     * 连接空闲时检测可用性的频率，单位毫秒，默认30秒
     * 
     * @param timeBetweenEvictionRunsMillis
     */
    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    /**
     * 每次对多少个连接检查可用性，-1为不限制，默认不限制
     * 
     * @param numTestsPerEvictionRun
     */
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    /**
     * 连接空闲多长时间认为是idle，单位毫秒，默认60秒
     * 
     * @param minEvictableIdleTimeMillis
     */
    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    /**
     * Getter method for property <tt>timeout</tt>.
     * 
     * @return property value of timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Setter method for property <tt>timeout</tt>.
     * 
     * @param timeout value to be assigned to property timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Getter method for property <tt>password</tt>.
     * 
     * @return property value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter method for property <tt>password</tt>.
     * 
     * @param password value to be assigned to property password
     */
    public void setPassword(String password) {
        if (null != password && !password.trim().equals("")) {
            this.password = password;
        }
    }

    //~~~ 由子类实现

    public String getSentinels() {
        throw new RuntimeException("not support");
    }

    public void setSentinels(String sentinels) {
        throw new RuntimeException("not support");
    }

    public String getMasterName() {
        throw new RuntimeException("not support");
    }

    public void setMasterName(String masterName) {
        throw new RuntimeException("not support");
    }

    public String getZookeepers() {
        throw new RuntimeException("not support");
    }

    public void setZookeepers(String zookeepers) {
        throw new RuntimeException("not support");
    }

    public String getProduct() {
        throw new RuntimeException("not support");
    }

    public void setProduct(String product) {
        throw new RuntimeException("not support");
    }

    public int getZkSessionTimeoutMs() {
        throw new RuntimeException("not support");
    }

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
        throw new RuntimeException("not support");
    }

    public String getEvictionPolicyClassName() {
        throw new RuntimeException("not support");
    }

    public void setEvictionPolicyClassName(String evictionPolicyClassName) {
        throw new RuntimeException("not support");
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        throw new RuntimeException("not support");
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        throw new RuntimeException("not support");
    }

    public int getZkConnectionTimeoutMs() {
        throw new RuntimeException("not support");
    }

    public void setZkConnectionTimeoutMs(int zkConnectionTimeoutMs) {
        throw new RuntimeException("not support");
    }

    public int getMaxRetryCount() {
        throw new RuntimeException("not support");
    }

    public void setMaxRetryCount(int maxRetryCount) {
        throw new RuntimeException("not support");
    }
}
