/**
 * www.lufax.com Inc.
 * Copyright (c) 2011-2015 All Rights Reserved.
 */
package com.dianxian.redis;

import java.util.List;
import java.util.Set;


/**
 * 
 * @author zhengjiaqing
 * @version $Id: IRedisCacheStore.java, v 0.1 2015年2月3日 上午10:49:49 zhengjiaqing Exp $
 */
public interface IRedisCacheStore<T, K> extends CacheStore<T, K> {

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
    public Set<String> keys(String pattern) throws CacheException;

    /**
//     * 更新一个key的过期时间。
//     *
//     * @param key   key，非空
//     * @param expireSeconds 新的过期时间
//     * @throws CacheException   如果更新失败抛出该异常
//     */
//    public void expire(String key, int expireSeconds) throws CacheException;
//
//    /**
//     * 递增计数器。
//     * <p>
//     * <li>递增指定的key值(+1)，并返回递增后的值。
//     * <li>整个过程是原子操作。
//     *
//     * @param key           key，非空
//     * @param expireSeconds 指定过期时间，单位秒，小于等于0则认为不过期，
//     * @throws CacheException   存储失败抛出该异常
//     */
//    public long incr(String key, int expireSeconds) throws CacheException;
//
//    /**
//     * 可指定步长的递增计数器。
//     * <p>
//     * <li>递增指定的key值(+N)，并返回递增后的值。
//     * <li>整个过程是原子操作。
//     *
//     * @param key           key，非空
//     * @param by            递增步长，如果为负数就变成递减
//     * @param expireSeconds 指定过期时间，单位秒，小于等于0则认为不过期，
//     * @throws CacheException   存储失败抛出该异常
//     */
//    public long incrBy(String key, long by, int expireSeconds) throws CacheException;
//
//    /**
//     * 递减计数器。
//     * <p>
//     * <li>递减指定的key值(+1)，并返回递减后的值。
//     * <li>整个过程是原子操作。
//     *
//     * @param key           key，非空
//     * @param expireSeconds 指定过期时间，单位秒，小于等于0则认为不过期，
//     * @throws CacheException   存储失败抛出该异常
//     */
//    public long decr(String key, int expireSeconds) throws CacheException;
//
//    /**
//     * 按指定范围查询list。
//     *
//     * @param key   list的key，非空
//     * @param start 指定开始位置，闭区间，列表从0开始计数
//     * @param end   指定结束位置，闭区间
//     * @return      满足条件的元素列表
//     * @throws CacheException   查询失败抛出该异常
//     */
//    public List<T> listRange(String key, long start, long end) throws CacheException;
//
//    /**
//     * 裁剪list到指定范围。
//     *
//     * @param key   list的key，非空
//     * @param start 指定保留列表的开始位置，闭区间，列表从0开始计数
//     * @param end   指定保留列表的结束位置，闭区间
//     * @throws CacheException   裁剪失败抛出该异常
//     */
//    public void listTrim(String key, long start, long end) throws CacheException;
//
//    /**
//     * 向list左边增加一个元素。
//     * <p>
//     * <li>如果list不存在则新建对应list
//     * <li>仅在添加第一个元素时设置过期时间
//     *
//     * @param key           list的key，非空
//     * @param data          新增元素，非空
//     * @param expireSeconds list的过期时间（只在初次创建时设置），单位秒，小于等于0则不过期
//     * @return 当前list总长度
//     * @throws CacheException   添加失败时抛出该异常
//     */
//    public long listPush(String key, T data, int expireSeconds) throws CacheException;
//
//    /**
//     * 向list左边增加一个元素。
//     * <p>
//     * <li>如果list不存在则新建对应list
//     * <li>每次添加新元素都更新过期时间
//     *
//     * @param key           list的key，非空
//     * @param data          新增元素，非空
//     * @param expireSeconds list的过期时间，单位秒，大于0
//     * @return 当前list总长度
//     * @throws CacheException   添加失败时抛出该异常
//     */
//    public long listPushAndExpireEverytime(String key, T data, int expireSeconds)
//                                                                                 throws CacheException;
//
//    /**
//     * 从list左边弹出一个元素。
//     *
//     * @param key   list的key
//     * @return      list左边第一个元素，如果没有元素则返回null
//     * @throws CacheException   Redis反问失败抛出该异常
//     */
//    public T listLPop(String key) throws CacheException;
//
//    /**
//     * （阻塞式地）从list右边弹出一个元素。
//     * <p>
//     * 从list的右端弹出一个元素，如果list当前没有元素，则等待指定的timeout时间。
//     * 在timeout之后仍然没有元素，则返回null。
//     *
//     * @param key               list的key
//     * @param timeoutSeconds    阻塞等待时间，秒，等于0表示一直阻塞
//     * @return                  列表右端的第一个元素，如果超时则返回null
//     * @throws CacheException   Redis访问失败抛出该异常
//     */
//    public T listBlockingRPop(String key, int timeoutSeconds) throws CacheException;
//
//    /**
//     * 获取列表的长度。
//     *
//     * @param key   列表的key
//     * @return      列表长度。列表长度为零，或者列表不存在，都返回0
//     * @throws CacheException   访问Redis失败抛出该异常
//     */
//    public long listLen(String key) throws CacheException;
//
//    /**
//     * 测试Redis可用性。
//     *
//     * @return  如果Redis可用则返回字符串“PONG”（大写）
//     * @throws CacheException   访问Redis失败抛出该异常
//     */
//    public String ping() throws CacheException;
//
//    /**
//     * 流量控制，判断当前请求是否允许访问。
//     * <p>比如“限制用户每24小时只能请求5次校验码”
//     *
//     * @param key       流量控制标识，通常是userid+具体场景，如上述例子中的“userid+请求校验码”
//     * @param limit     次数限制，大于0，如上述例子中的“5次”
//     * @param duration  时间范围，大于0，单位毫秒，如上述例子中的“24小时”
//     * @return  true：需要限制  false：无需限制
//     * @throws CacheException   远程调用失败抛出该异常
//     */
//    public boolean isDenied(String key, int limit, long duration) throws CacheException;

}
