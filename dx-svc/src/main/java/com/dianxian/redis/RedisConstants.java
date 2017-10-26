/**
 * Lufax.com Inc.
 * Copyright (c) 2011-2014 All Rights Reserved.
 */
package com.dianxian.redis;

import redis.clients.jedis.Protocol;

/**
 * Redis相关常量。
 * 
 * @author zhengjiaqing [http://jiaqing.me]
 * @version $Id: Constants.java, v 0.1 Jun 4, 2014 10:10:29 AM zhengjiaqing Exp $
 */
public interface RedisConstants {
    //~~~ Redis属性：

    /** 请求超时时间, 即Socket.soTimeout, 单位毫秒 */
    int     DEF_TIMEOUT                           = Protocol.DEFAULT_TIMEOUT;
    /** redis密码, 默认null即没有密码 */
    String  DEF_PASSWORD                          = null;
    /** key默认超时时间，24小时，单位秒 */
    int     DEF_EXPIRE_SECONDS                    = 60 * 60 * 24;

    //~~~ Jedis连接池属性：

    /** 最大连接数 */
    int     DEF_MAX_TOTAL                         = 10;
    /** 最小连接数 */
    int     DEF_MIN_IDEL                          = 2;
    /** 连接池满时是否等待 true：是 */
    boolean DEF_BLOCK_WHEN_EXHAUSTED              = true;
    /** 连接池满时等待时间，单位毫秒 */
    long    DEF_MAX_WAIT_MILLIS                   = 1000 * 5;
    /** 是否使用LIFO队列存储连接， true：是 */
    boolean DEF_LIFO                              = false;
    /** 是否在获取连接时测试可用性 true：是 */
    boolean DEF_TEST_ON_BORROW                    = false;
    /** 是否在归还连接时测试可用性 true：是 */
    boolean DEF_TEST_ON_RETURN                    = false;
    /** 是否在连接空闲时测试可用性 true：是 */
    boolean DEF_TEST_WHILE_IDLE                   = true;
    /** 连接空闲时测试可用性的时间间隔，单位毫秒 */
    long    DEF_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 1000 * 30;
    /** 连接空闲时测试可用性每次测试的线程数， -1为不限制 */
    int     DEF_NUM_TESTS_PER_EVICTION_RUN        = -1;
    /** 连接空闲多长时间被认为idle，单位毫秒 */
    long    DEF_MIN_EVICTABLE_IDLE_TIME_MILLIS    = 1000 * 60;

    //~~~ Jodis属性:
    /** zookeeper会话超时时间,单位毫秒 */
    int     DEF_ZK_SESSION_TIMEOUT_MS             = 1000 * 60;
    /** zookeeper连接超时时间，单位毫秒 */
    int     DEF_ZK_CONNECTION_TIMEOUT_MS          = 1000 * 15;
    /** 最大重试次数 */
    int     DEF_MAX_RETRY_COUNT                   = Integer.MAX_VALUE;
    /** JMX 前缀 */
    String  DEF_CODIS_JMX_PREFIX                  = "CODIS";

    //~~~ 流控算法

    /** 用于流控的lua脚本：用于先插入后计算 */
    String  LUA_ENQUEUE_FIRST                     = ""
                                                    + "local key = KEYS[1]                                 \n"
                                                    + "local limit = ARGV[1]                               \n"
                                                    + "local duration = ARGV[2]                            \n"
                                                    + "local current = ARGV[3]                             \n"
                                                    + ""
                                                    + "local len = redis.call('lpush', key, current)       \n" // 将请求入队
                                                    + "redis.call('pexpire', key, duration*2)              \n" // 设置过期
                                                    + "if len > limit*2 then                               \n" // 防止队列过长
                                                    + "    redis.call('ltrim', key, 0, limit+1)            \n"
                                                    + "end                                                 \n"
                                                    + "local last = redis.call('lindex', key, limit)       \n" // 判断是否超限
                                                    + "if last and current-last < tonumber(duration) then  \n"
                                                    + "    return 'true'                                   \n"
                                                    + "else                                                \n"
                                                    + "    return 'false'                                  \n"
                                                    + "end                                                 \n";

    /** 用于流控的lua脚本：用于先计算后插入 */
    String  LUA_NOT_ENQUEUE_FIRST                 = ""
                                                    + "local key = KEYS[1]                                 \n"
                                                    + "local limit = ARGV[1]                               \n"
                                                    + "local duration = ARGV[2]                            \n"
                                                    + "local current = ARGV[3]                             \n"
                                                    + ""
                                                    + "local last = redis.call('lindex', key, limit-1)     \n" // 判断是否超限
                                                    + "if last and current-last < tonumber(duration) then  \n"
                                                    + "    return 'true'                                   \n"
                                                    + "end                                                 \n"
                                                    + "local len = redis.call('lpush', key, current)       \n" // 将请求入队
                                                    + "redis.call('pexpire', key, duration*2)              \n" // 设置过期
                                                    + "if len > limit*2 then                               \n" // 防止队列过长
                                                    + "    redis.call('ltrim', key, 0, limit+1)            \n"
                                                    + "end                                                 \n"
                                                    + "return 'false'                                      \n";

    /** 用于流控的lua脚本：支持降级的流控 */
    // 切换到Codis后需要将脚本压缩，或者修改Codis载入Lua脚本的方案
    String  LUA_WITH_MARKDOWN                     = ""
                                                    + "local key = KEYS[1]              \n"
                                                    + "local limit = ARGV[1]            \n"
                                                    + "local duration = ARGV[2]         \n"
                                                    + "local current = ARGV[3]          \n"
                                                    + "local markdown_count = ARGV[4]   \n" // 违规N次后执行降级
                                                    + "local markdown_time = ARGV[5]    \n" // 降级持续时间，过期后恢复到正常规则
                                                    + "local markdown_limit = ARGV[6]   \n" // 降级的limit
                                                    + "local markdown_duration = ARGV[7]\n" // 降级的duration
                                                    + ""
                                                    + "local counter = redis.call('get', key..':md_counter')\n" // 判断是否执行降级  
                                                    + "if counter and tonumber(counter) >= tonumber(markdown_count) then \n"
                                                    + "    limit = markdown_limit                           \n"
                                                    + "    duration = markdown_duration                     \n"
                                                    + "end                                                  \n"
                                                    + ""
                                                    + "if tonumber(limit) == 0 then                         \n" // 对limit=0降级特例的优化，如果limit=0直接阻止
                                                    + "    return 'true'                                    \n"
                                                    + "end                                                  \n"
                                                    + ""
                                                    + "local len = redis.call('lpush', key, current)        \n" // 将请求入队
                                                    + "redis.call('pexpire', key, duration*2)               \n" // 设置过期
                                                    + "if len > limit*2 then                                \n" // 防止队列过长
                                                    + "    redis.call('ltrim', key, 0, limit+1)             \n"
                                                    + "end                                                  \n"
                                                    + "local last = redis.call('lindex', key, limit)        \n" // 判断是否超限
                                                    + "if last and current-last < tonumber(duration) then   \n" // 累加违规超限次数
                                                    + "    if not counter then                              \n"
                                                    + "        redis.call('incr', key..':md_counter')       \n"
                                                    + "        redis.call('pexpire', key..':md_counter', markdown_time)    \n"
                                                    + "    else                                             \n"
                                                    + "        redis.call('incr', key..':md_counter')       \n"
                                                    + "    end                                              \n"
                                                    + "    return 'true'                                    \n"
                                                    + "else                                                 \n"
                                                    + "    return 'false'                                   \n"
                                                    + "end                                                  \n"
                                                    + "";

    //~~~分布式锁算法

    /** 加锁的Lua代码 */
    String  LUA_ACQUIRE_LOCK                      = ""
                                                    + "local key = KEYS[1]                              \n"
                                                    + "local value = ARGV[1]                            \n"
                                                    + "local timeout = ARGV[2]                          \n"
                                                    + "                                                 \n"
                                                    + "if (redis.call('setnx', key, value) == 1) then   \n"
                                                    + "    redis.call('pexpire', key, timeout)          \n"
                                                    + "    return 'true'                                \n"
                                                    + "else                                             \n"
                                                    + "    return 'false'                               \n"
                                                    + "end                                              \n";
    /** 释放锁的Lua代码 */
    String  LUA_RELEASE_LOCK                      = ""
                                                    + "local key = KEYS[1]                          \n"
                                                    + "local value = ARGV[1]                        \n"
                                                    + "local orig_value = redis.call('get', key)    \n"
                                                    + "                                             \n"
                                                    + "if (orig_value == nil) then                  \n"
                                                    + "    return 'true'                            \n"
                                                    + "elseif (orig_value == value) then            \n"
                                                    + "    redis.call('del', key)                   \n"
                                                    + "    return 'true'                            \n"
                                                    + "else                                         \n"
                                                    + "    return 'false'                           \n"
                                                    + "end                                          \n";

    //~~~ 其它基础操作
    /** 递增计数，仅在第一次设置过期时间 */
    String  LUA_INCR_EXPIRE_FIRST_TIME            = ""
                                                    + "local key = KEYS[1]                                          \n"
                                                    + "local timeout = ARGV[1]                                      \n"
                                                    + "local current = redis.call('incr', key)                      \n"
                                                    + "if (tonumber(current) == 1 and tonumber(timeout) > 0) then   \n"
                                                    + "    redis.call('expire', key, timeout)                       \n"
                                                    + "end                                                          \n"
                                                    + "return current                                               \n";
    // 这个没人使用？
    /** 递增计数，仅在第一次设置过期时间 */
    String  LUA_INCR_BY_EXPIRE_FIRST_TIME         = ""
                                                    + "local key = KEYS[1]                                          \n"
                                                    + "local by = KEYS[1]                                           \n"
                                                    + "local timeout = ARGV[1]                                      \n"
                                                    + "local current = redis.call('incrby', key, by)                \n"
                                                    + "if (tonumber(current) == 1 and tonumber(timeout) > 0) then   \n"
                                                    + "    redis.call('expire', key, timeout)                       \n"
                                                    + "end                                                          \n"
                                                    + "return current                                               \n";

    /** 2的32次方 */
    long    _2_TO_THE_32                          = 4294967296L;
}
