package com.dianxian.redis;

/**
 * This interface is designed for transformation between raw data and cached
 * key/value
 * 
 * @param <T>
 *            Class type of the data
 * @param <K>
 *            Class type of the key of the cached key/value pair
 * @param <V>
 *            Class type of the value of the cached key/value pair
 */
public interface CacheTransformer<T, K, V> {

	/**
	 * cache key cache from raw data
	 * 
	 * @param data
	 * @return
	 */
	public K key(T data);

	/**
	 * cache value from raw data
	 * 
	 * @param data
	 * @return
	 */
	public V value(T data);

	/**
	 * Raw data from cache value
	 * 
	 * @param value
	 * @return
	 */
	public T data(V value);

	/**
	 * expiration time from raw data,
	 * 
	 * -1 means NO EXPIRATION TIME
	 * 
	 * @param data
	 * @return
	 */
	public int expiration(T data);
}
