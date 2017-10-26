package com.dianxian.redis;

import java.util.List;

/**
 * The interface to load the data from source in case that the data is not in cache
 * or when performing auto refresh for cache
 * 
 *
 * @param <T>  type of raw data
 * @param <K>  type of key
 */
public interface CacheLoader<T, K> {
	
	/**
	 * Load all data which need to be cached, it will be called by auto refresh task
	 * 
	 * @return
	 * @throws CacheException
	 */
	public List<T> getAll() throws CacheException;
	
	/**
	 * Load the data by key, it will be called by cache store if the data is not in cache store
	 * 
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	public T load(K key) throws CacheException;
}
