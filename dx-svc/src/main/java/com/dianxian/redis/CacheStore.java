package com.dianxian.redis;


public interface CacheStore<T, K> {

	/**
	 * Return the cached data, if not cached, try to load it from data loader,
	 * the logic likes
	 * 
	 * @param key
	 * @return null if the data associated with the given key is not cached,
	 * @throws CacheException
	 *             if cache server is not available, or
	 */
	public T get(K key) throws CacheException;
	
	/**
	 * Cache the data
	 * 
	 * @param data
	 * @throws CacheException
	 */
	public void put(K key, T data) throws CacheException;
	
	/**
	 * Cache the data with expiration time
	 * 
	 * @param data
	 * @throws CacheException
	 */
	public void put(K key, T data, int expiration) throws CacheException;
	
	
	/**
	 * Remove the data
	 * 
	 * @param key
	 * @throws CacheException
	 */
	public void remove(K key) throws CacheException;

	/**
	 * Return the cached data, 
	 * 
	 * @param proto
	 * @return
	 * @throws CacheException
	 */
	public T getData(T proto) throws CacheException;
	
	/**
	 * Cache the data
	 * 
	 * @param data
	 * @throws CacheException
	 */
	public void putData(T data) throws CacheException;	
	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws CacheException
	 */
	public void removeData(T data) throws CacheException;
	
	
	/**
	 * clear the data in cache
	 * 
	 * @return
	 * @throws CacheException
	 */
	public void clear() throws CacheException;
	
	/**
	 * Refresh 
	 * 
	 * @return
	 * @throws CacheException
	 */
	public void refresh() throws CacheException;
	
	/**
	 * shutdown
	 */
	public void shutdown() throws CacheException;
	
	/**
	 * Cache activity statistics
	 * 
	 * @return
	 */
	public CacheStatistics getCacheStatistics();
}
