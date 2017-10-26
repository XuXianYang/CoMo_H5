package com.dianxian.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisDataException;

import com.codahale.metrics.Timer.Context;


/**
 * Redis客户端。
 * <p>
 * 提供基于Redis的基本操作比如get/set，以及请求队列/流量控制/分布式锁等功能。
 * <p>
 *
 */
public class RedisCacheStore<T> implements IRedisCacheStore<T, String> {
    private final static Logger                 logger                    = LoggerFactory
                                                                              .getLogger(RedisCacheStore.class);

    /** Redis客户端的参数集 */
    private RedisCacheConfig                    redisConfig;
    /** 业务数据转换器 */
    private CacheTransformer<T, String, String> transformer;
    /** 业务数据加载器 */
    private CacheLoader<T, String>              loader;
    /** 业务数据类型 */
    private Class<T>                            clazz;
    /** Apache-Commons连接池的属性集 */
    private GenericObjectPoolConfig             poolConfig;
    /** sentinel地址集 */
    private Set<String>                         sentinelSet;

    /** 用于JedisSentinelPool初始化的线程池 */
    private ExecutorService                     executor;
    /** 基于Sentinel的Redis连接池 */
    private volatile JedisSentinelPool          pool;
    /** 监控统计 */
    private CacheStatistics                     cacheStats;

    /** Cache Name */
    private String                              cacheName;
    /** Key辅助工具 */
    private KeyHelper                           keyHelper;

    /** redis存储的lua脚本的sha值：用于先插入后计算 */
    private static volatile String              luaEnqueueFirstSHA        = null;
    /** redis存储的lua脚本的sha值：用于先计算后插入 */
    private static volatile String              luaNotEnqueueFirstSHA     = null;
    /** 释放锁的Lua代码SHA值 */
    private static volatile String              luaReleaseLockSHA         = null;
    /** 加锁的Lua代码SHA值 */
    private static volatile String              luaAcquireLockSHA         = null;
    /** 累加操作且仅在第一次过期的Lua代码SHA值 */
    private static volatile String              luaIncrAndExpireFirstTime = null;
    /** 带降级的流控 */
    private static volatile String              luaWithMarkdownSHA        = null;

    /**
     * 构造Redis客户端。
     * 
     * @param redisConfig   客户端参数集，必填
     * @param clazz         客户端数据类型，必填
     * @param transformer   客户端转换器，选填
     * @param loader        客户端加载器，选填
     */
    public RedisCacheStore(RedisCacheConfig redisConfig, Class<T> clazz,
                           CacheTransformer<T, String, String> transformer,
                           CacheLoader<T, String> loader) {
        if (null == redisConfig || null == clazz) {
            throw new InitializationException(
                "RedisClient init fail, RedisCacheConfig and Class must not be null.");
        }

        this.redisConfig = redisConfig;
        this.keyHelper = new KeyHelper(this.redisConfig);
        this.cacheName = redisConfig.getCacheName();
        this.clazz = clazz;
        this.loader = loader;
        if (null != transformer) {
            this.transformer = transformer;
        } else {
            this.transformer = new DefaultRedisTransformer<T>(clazz);
        }
        this.executor = Executors.newSingleThreadExecutor();

        setupMonitor();
        setupSentinelPool();

        logger.info("[CACHE]RedisClient init finished. config[ " + this.redisConfig.toString()
                    + " ]");
    }

    /**
     * 初始化Sentinel连接池。
     */
    private void setupSentinelPool() {
        try {
            // 1.配置Apache-Commons连接池属性
            poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(redisConfig.getMaxTotal());
            poolConfig.setMaxIdle(redisConfig.getMaxIdel());
            poolConfig.setMinIdle(redisConfig.getMinIdel());
            poolConfig.setBlockWhenExhausted(redisConfig.isBlockWhenExhausted());
            poolConfig.setMaxWaitMillis(redisConfig.getMaxWaitMillis());
            poolConfig.setLifo(redisConfig.isLifo());
            poolConfig.setTestOnBorrow(redisConfig.isTestOnBorrow());
            poolConfig.setTestOnReturn(redisConfig.isTestOnReturn());
            poolConfig.setTestWhileIdle(redisConfig.isTestWhileIdle());
            poolConfig.setTimeBetweenEvictionRunsMillis(redisConfig
                .getTimeBetweenEvictionRunsMillis());
            poolConfig.setNumTestsPerEvictionRun(redisConfig.getNumTestsPerEvictionRun());
            poolConfig.setMinEvictableIdleTimeMillis(redisConfig.getMinEvictableIdleTimeMillis());

            // 2.获取Sentinel地址
            String[] addrs = redisConfig.getSentinels().split(";");
            sentinelSet = new HashSet<String>();
            for (String addr : addrs) {
                sentinelSet.add(addr);
            }


            // 3.创建JedisSentinelPool
            Future<JedisSentinelPool> future = executor.submit(new JedisPoolCreator());
            try {
                pool = future.get(30, TimeUnit.SECONDS); // 等待30秒
            } catch (Exception e) {
                logger.warn("[CACHE]init JedisSentinelPool fail, task still running in daemon ...",
                    e);
            }
        } catch (Throwable e) {
            // logger.error("[CACHE]init fail", e);
            throw new InitializationException("RedisClient init fail", e);
        } finally {

        }

        logger.info("[CACHE]Sentinel Pool for " + this.clazz.getName() + " init finished.");
    }

    /**
     * 初始化本地监控服务。
     */
    private void setupMonitor() {
        this.cacheStats = new CacheStatistics(redisConfig);
        logger.info("[CACHE]Monitor init finished.");
    }

    /**
     * Key的模糊查询。
     * 
     * @param pattern 模糊查询条件:</br>
     *        * 匹配所有 key</br>
     *        h?llo 匹配 hello ， hallo 和 hxllo 等</br>
     *        h*llo 匹配 hllo 和 heeeeello 等</br>
     *        h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo</br>
     *        注意1：特殊符号用 \ 隔开</br>
     *        注意2：客户端默认会对应用传入的key拼上前缀，所以即使在pattern=＊的情况下，也只能搜索到该前缀下的所有key，搜索不到其它前缀的Key</br>
     * @return 满足条件的无序集合，如果没有匹配则返回空Set
     * @throws CacheException   查询失败抛出该异常
     */
    public Set<String> keys(String pattern) throws CacheException {
        if (!Validator.checkString(pattern)) {
            throw new CacheException("invalid params, key=" + pattern);
        }

        Context ctx = cacheStats.getStart();
        boolean isSuccess = false;
        Jedis conn = null;
        String realKey = this.keyHelper.realKey(pattern);

        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            Set<String> result = conn.keys(realKey);
            cacheStats.hit();


            return result;

        } catch (Exception e) {
            logger.error("[CACHE]keys fail, pattern=" + pattern + ", redis="
                         + getCurrentRedisAddr(), e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("cache keys fail", e);

        } finally {

            returnResource(conn);
            cacheStats.getDone(ctx, isSuccess);
        }
    }

    /**
     * 根据key获取value。
     * <p>
     * 
     * @param key   查询key，非空
     * @return      如果没有匹配结果返回null
     * @throws CacheException   查询失败抛出该异常
     */
    public T get(String key) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Context ctx = cacheStats.getStart();
        boolean isSuccess = false;
        boolean isReloaded = false;
        String realKey = this.keyHelper.realKey(key);

        Jedis conn = null;
        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            String value = conn.get(realKey);
            isSuccess = true;

            // case-1 有值则直接返回
            if (null != value) {
                cacheStats.hit();
                return transformer.data(value);
            }
            // case-2 没值，且指定了loader则尝试从loader重新加载
            else if (null != loader) {
                cacheStats.miss();
                isReloaded = true;
                return reload(key);
            }
            // case-3 否则返回null
            else {
                cacheStats.miss();
                return null;
            }

        } catch (Exception e) {
            logger.error("[CACHE]get fail, key=" + realKey + ", redis=" + getCurrentRedisAddr(), e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }

            // ex-case-1 如果指定了loader，並且沒在正常流程里reload過，则尝试从loader重新加载
            if (null != loader && !isReloaded) {
                try {
                    return reload(key);
                } catch (Exception e3) {
                    logger.error("[CACHE]reload fail", e3);
                    throw new CacheException("reload fail", e3);
                }
            }
            // ex-case-2 否则抛出异常
            else {
                throw new CacheException("cache get fail", e);
            }

        } finally {
            returnResource(conn);
            cacheStats.getDone(ctx, isSuccess);
        }
    }



    /**
     * 从loader重新加载数据。
     * 
     * @param key
     * @return
     * @throws CacheException
     */
    private T reload(String key) throws CacheException {
        T reloadData = loader.load(this.keyHelper.realKey(key));
        if (null != reloadData) {
            put(transformer.key(reloadData), reloadData, transformer.expiration(reloadData));
        }
        return reloadData;
    }

    /**
     * 存储一个key-value。
     * <p>
     * 注意，使用该接口，系统默认会设置key的过期时间为24小时。如果需要指定过期时间，请使用put(String key, T data, int expireSeconds)
     * 
     * @param key   key，非空
     * @param data  value，非空
     * @throws CacheException   存储失败抛出该异常
     */
    public void put(String key, T data) throws CacheException {
        put(key, data, RedisConstants.DEF_EXPIRE_SECONDS);
    }

    /**
     * 存储一个key-value。
     * 
     * @param key   key，非空
     * @param data  value，非空
     * @param expireSeconds 指定过期时间，如果输入小于等于0的值则认为不过期
     * @throws CacheException   存储失败抛出该异常
     */
    public void put(String key, T data, int expireSeconds) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Context ctx = cacheStats.putStart();
        boolean isSuccess = false;
        String realKey = this.keyHelper.realKey(key);

        Jedis conn = null;
        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            String value = (String) transformer.value(data);

            if (expireSeconds > 0) {
                conn.setex(realKey, expireSeconds, value);
            } else {
                conn.set(realKey, value);
            }

            cacheStats.put();
            isSuccess = true;

        } catch (Exception e) {
            logger.error("[CACHE]put fail, key=" + realKey + ", redis=" + getCurrentRedisAddr(), e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("cache put fail", e);

        } finally {

            returnResource(conn);
            cacheStats.putDone(ctx, isSuccess);
        }
    }



    /**
     * 删除一个key
     * 
     * @param key   key，非空
     * @throws CacheException   删除失败抛出该异常
     */
    public void remove(String key) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Context ctx = cacheStats.removeStart();
        boolean isSuccess = false;
        Jedis conn = null;
        String realKey = this.keyHelper.realKey(key);

        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            conn.del(realKey);
            cacheStats.remove();
            isSuccess = true;

        } catch (Exception e) {
            logger.error("[CACHE]remove fail, key=" + realKey + ", redis=" + getCurrentRedisAddr(),
                e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("cache put fail", e);

        } finally {
            returnResource(conn);
            cacheStats.removeDone(ctx, isSuccess);
        }
    }

    /**
     * 更新一个key的过期时间。
     * 
     * @param key   key，非空
     * @param expireSeconds 新的过期时间
     * @throws CacheException   如果更新失败抛出该异常
     */
    public void expire(String key, int expireSeconds) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Context ctx = cacheStats.putStart();
        boolean isSuccess = false;
        Jedis conn = null;
        String realKey = this.keyHelper.realKey(key);

        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            conn.expire(realKey, expireSeconds);
            cacheStats.put();
            isSuccess = true;

        } catch (Exception e) {
            logger.error("[CACHE]expire fail, key=" + realKey + ", redis=" + getCurrentRedisAddr(),
                e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("cache expire fail", e);

        } finally {

            returnResource(conn);
            cacheStats.putDone(ctx, isSuccess);
        }
    }

    /**
     * 递增计数器。
     * <p>
     * <li>递增指定的key值(+1)，并返回递增后的值。
     * <li>整个过程是原子操作。
     * 
     * @param key           key，非空
     * @param expireSeconds 指定过期时间，单位秒，小于等于0则认为不过期，
     * @return              递增后的结果
     * @throws CacheException   存储失败抛出该异常
     */
    public long incr(String key, int expireSeconds) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Context ctx = cacheStats.putStart();
        boolean isSuccess = false;
        String realKey = this.keyHelper.realKey(key);

        Jedis conn = null;
        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            Long result = conn.incr(realKey);
            if (expireSeconds > 0) {
                conn.expire(realKey, (int) expireSeconds);
            }
            cacheStats.put();
            isSuccess = true;
            return result;

        } catch (Exception e) {
            logger
                .error("[CACHE]incr fail, key=" + realKey + ", redis=" + getCurrentRedisAddr(), e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("cache incr fail", e);

        } finally {

            returnResource(conn);
            cacheStats.putDone(ctx, isSuccess);
        }
    }

    /**
     * 递增计数器，仅在第一次递增时设置过期时间。
     * 
     * @param key               key，非空
     * @param expireSeconds     指定过期时间，单位秒，小于等于0则认为不过期，仅在从0递增到1时有效
     * @return                  递增后的结果
     * @throws CacheException   访问Redis出错抛出该异常
     */
    public long incrAndExpireFirstTime(String key, int expireSeconds) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Jedis conn = null;
        String realKey = this.keyHelper.realKey(key);

        try {
            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            if (luaIncrAndExpireFirstTime == null) {
                luaIncrAndExpireFirstTime = conn
                    .scriptLoad(RedisConstants.LUA_INCR_EXPIRE_FIRST_TIME); // 不需要并发控制，如果是相同的脚本，redis会返回相同的SHA值，同时在它的内存里只维护一份
            }
            try {
                Object result = conn.evalsha(luaIncrAndExpireFirstTime, 1, realKey, expireSeconds
                                                                                    + "");
                return Long.valueOf((Long) result);
            } catch (JedisDataException jedisEx) { // 理论上不会发生，以防万一：如果redis在本地缓存里没找到lua脚本，则重新推送一份给它
                if ("NOSCRIPT No matching script. Please use EVAL.".equalsIgnoreCase(jedisEx
                    .getMessage())) {
                    logger.warn("[CACHE]incr.ExpireFirstTime but redis cannot read it, key=" + key
                                + ", redis=" + getCurrentRedisAddr());

                    luaIncrAndExpireFirstTime = conn
                        .scriptLoad(RedisConstants.LUA_INCR_EXPIRE_FIRST_TIME);
                }
                throw jedisEx; // 其它错误不特殊处理
            }

        } catch (Exception e) {
            logger.error("[CACHE]incr.ExpireFirstTime fail, key=" + realKey + ", redis="
                         + getCurrentRedisAddr(), e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("call incr.ExpireFirstTime fail", e);

        } finally {
            returnResource(conn);
        }

    }

    /**
     * 可指定步长的递增计数器。
     * <p>
     * <li>递增指定的key值(+N)，并返回递增后的值。
     * <li>整个过程是原子操作。
     * 
     * @param key           key，非空
     * @param by            递增步长，如果为负数就变成递减
     * @param expireSeconds 指定过期时间，单位秒，小于等于0则认为不过期，
     * @throws CacheException   存储失败抛出该异常
     */
    public long incrBy(String key, long by, int expireSeconds) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Context ctx = cacheStats.putStart();
        boolean isSuccess = false;
        String realKey = this.keyHelper.realKey(key);

        Jedis conn = null;
        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            Long result = conn.incrBy(realKey, by);
            if (expireSeconds > 0) {
                conn.expire(realKey, (int) expireSeconds);
            }
            cacheStats.put();
            isSuccess = true;
            return result;

        } catch (Exception e) {
            logger.error("[CACHE]incrBy fail, key=" + realKey + ", redis=" + getCurrentRedisAddr(),
                e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("cache incrBy fail", e);

        } finally {

            returnResource(conn);
            cacheStats.putDone(ctx, isSuccess);
        }
    }

    /**
     * 递减计数器。
     * <p>
     * <li>递减指定的key值(+1)，并返回递减后的值。
     * <li>整个过程是原子操作。
     * 
     * @param key           key，非空
     * @param expireSeconds 指定过期时间，单位秒，小于等于0则认为不过期，
     * @throws CacheException   存储失败抛出该异常
     */
    public long decr(String key, int expireSeconds) throws CacheException {
        if (!Validator.checkString(key)) {
            throw new CacheException("invalid params, key=" + key);
        }

        Context ctx = cacheStats.putStart();
        boolean isSuccess = false;
        String realKey = this.keyHelper.realKey(key);

        Jedis conn = null;
        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            Long result = conn.decr(realKey);
            if (expireSeconds > 0) {
                conn.expire(realKey, (int) expireSeconds);
            }
            cacheStats.put();
            isSuccess = true;
            return result;

        } catch (Exception e) {
            logger
                .error("[CACHE]decr fail, key=" + realKey + ", redis=" + getCurrentRedisAddr(), e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("cache decr fail", e);

        } finally {

            returnResource(conn);
            cacheStats.putDone(ctx, isSuccess);
        }
    }



    /**
     * 测试Redis可用性。
     * 
     * @return  如果Redis可用则返回字符串“PONG”（大写）
     * @throws CacheException   访问Redis失败抛出该异常
     */
    public String ping() throws CacheException {
        Jedis conn = null;

        try {

            if (null == pool) {
                throw new CacheException("JedisSentinelPool initializing..");
            } else {
                conn = pool.getResource();
            }

            String retVal = conn.ping();
            return retVal;
        } catch (Exception e) {
            logger.error("[CACHE]ping fail, redis=" + getCurrentRedisAddr(), e);
            if (returnBrokenResource(conn)) {
                conn = null;
            }
            throw new CacheException("ping fail", e);

        } finally {
            returnResource(conn);
        }
    }


    /**
     */
    public T getData(T proto) throws CacheException {
        String key = this.transformer.key(proto);
        return get(key);
    }

    /**
     */
    public void putData(T data) throws CacheException {
        String key = this.transformer.key(data);
        put(key, data);
    }

    /**
     */
    public void removeData(T data) throws CacheException {
        String key = this.transformer.key(data);
        remove(key);
    }

    /**
     */
    public void clear() throws CacheException {
        throw new CacheException("not support");
    }

    /**
     */
    public void refresh() throws CacheException {
        if (null == pool) {
            throw new CacheException("JedisSentinelPool initializing..");
        }

        if (null == this.loader) {
            throw new CacheException("not support");
        }

        List<T> all = this.loader.getAll();

        if (null == all) {
            return;
        }

        for (T item : all) {
            putData(item);
        }
    }

    /**
     */
    public CacheStatistics getCacheStatistics() {
        return this.cacheStats;
    }

    /**
     */

    @Override
    public void shutdown() throws CacheException {
        if (null != executor) {
            executor.shutdown();
        }
        if (null != pool) {
            pool.destroy();
            cacheStats.stop();
            logger.info("[CACHE]Client destroyed.");
        }
    }

    /**
     * 获取当前RedisMaster地址
     * 
     * @return
     */
    private String getCurrentRedisAddr() {
        StringBuffer sb = new StringBuffer();

        if (this.pool != null) {
            HostAndPort hap = pool.getCurrentHostMaster();
            if (null != hap) {
                sb.append(hap.getHost());
                sb.append(":");
                sb.append(hap.getPort());
            }
        }

        return sb.toString();
    }

    /**
    * 负责在系统启动时创建JedisSentinelPool。
     */
    class JedisPoolCreator implements Callable<JedisSentinelPool> {
        /** 
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public JedisSentinelPool call() throws Exception {
            pool = new JedisSentinelPool(redisConfig.getMasterName(), sentinelSet, poolConfig,
                redisConfig.getTimeout(), redisConfig.getPassword());
            logger.info("[CACHE]JedisSentinelPool init completed.");
            return pool;
        }
    }

    /**
     * 归还异常连接。
     * 
     * @param conn
     * @return
     */
    private boolean returnBrokenResource(Jedis conn) {
        boolean result = false;

        try {
            if (pool != null && conn != null) {
                pool.returnBrokenResource(conn);
                result = true;
            }
        } catch (Exception e) {
            logger.error("[CACHE]return broken resource fail", e);
        }

        return result;
    }

    /**
     * 归还正常连接。
     * 
     * @param conn
     * @return
     */
    private boolean returnResource(Jedis conn) {
        boolean result = false;

        try {
            if (pool != null && conn != null) {
                pool.returnResource(conn);
                result = true;
            }
        } catch (Exception e) {
            logger.error("[CACHE]return resource fail", e);
        }

        return result;
    }

    /**
     * 用于分布式锁的线程上下文。
     * 
     * @author zhengjiaqing [ http://jiaqing.me ]
     * @version $Id: LockContextOld.java, v 0.1 Jul 9, 2015 3:06:09 PM zhengjiaqing Exp $
     */
    @Deprecated
    static class LockContextOld {
        protected static final ThreadLocal<LockContextOld> LOCAL = new ThreadLocal<LockContextOld>() {
                                                                     @Override
                                                                     protected LockContextOld initialValue() {
                                                                         return new LockContextOld();
                                                                     }
                                                                 };

        /**
        * 获取当前线程上下文。
        * 
        * @return 线程上下文，非空
        */
        public static LockContextOld get() {
            return LOCAL.get();
        }

        //~~~携带的相关信息：

        private String        contextId;
        private AtomicInteger lockCounter = new AtomicInteger(0);

        public void remove() {
            LOCAL.remove();
        }

        //~~~Getter and Setters

        public String getContextId() {
            return contextId;
        }

        public void setContextId(String contextId) {
            this.contextId = contextId;
        }

        public int incrLockCounter() {
            return this.lockCounter.incrementAndGet();
        }

        public int decrLockCounter() {
            return this.lockCounter.decrementAndGet();
        }
    }
}
