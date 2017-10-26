package com.dianxian.redis;


/**
 * Basic cache configuration
 * 
 */
public class CacheConfig extends BaseMBean implements CacheConfigMBean {
	/**
	 * Cache name, it should be unique
	 */
	private final String cacheName;

	/**
	 * Short cache name, Redis cache use it as prefix of the key, in order to
	 * support name space for the cache, so it also should be unique
	 */
	private final String shortName;

	/**
	 * Default expiration time after write,
	 * RedisCacheStoreII and GuavaCacheStore support it
	 * 
	 * LocalMapCacheStore doesn't support it.
	 */
	private long expirationTimeInMs;

	/**
	 * Max cache size
	 * 
	 * RedisCacheStoreII doesn't support it
	 */
	private int cacheMaxSize;

	/**
	 * Auto refresh or not, RedisCacheStoreII doesn't support it
	 * 
	 */
	private boolean autoRefresh;

	/**
	 * Optional
	 * 
	 * Refresh time specified in format HH:mm:ss
	 */
	private String refreshTime;

	/**
	 * Refresh interval in seconds
	 */
	private long refreshIntervalInSec;

	/**
	 * Enable Metrics manage-able thru JmxReporter
	 */
	private boolean enableStatsJmxReporter;

	/**
	 * Enable Console Reporter for metrics
	 */
	private boolean enableStatsConsoleReporter;

	/**
	 * Enable Slf4j Reporter
	 */
	private boolean enableStatsSlf4jReporter;
	
	/**
	 * Console period in seconds
	 */
	private int statsConsoleReportPeriodInSec;
	
	/**
	 * Slf4j period in seconds
	 */
	private int statsSlf4jReportPeriodInSec;

	/**
	 * 
	 * @param name
	 * @param shortName
	 * @param expirationTimeInMs
	 */
	public CacheConfig(String name, String shortName, long expirationTimeInMs) {
		this(name, shortName, expirationTimeInMs, -1, false, "", -1);
	}

	/**
	 * 
	 * @param name
	 * @param shortName
	 * @param expirationTimeInMs
	 * @param cacheMaxSize
	 * @param autoRefresh
	 * @param refreshTime
	 * @param refreshIntervalInSec
	 */
	public CacheConfig(String name, String shortName, long expirationTimeInMs,
			int cacheMaxSize, boolean autoRefresh, String refreshTime,
			long refreshIntervalInSec) {
		this(name, shortName, expirationTimeInMs, cacheMaxSize, autoRefresh,
				refreshTime, refreshIntervalInSec, false, false, true, -1, 1800);
	}

	/**
	 * 
	 * @param name
	 * @param shortName
	 * @param expirationTimeInMs
	 * @param cacheMaxSize
	 * @param autoRefresh
	 * @param refreshTime
	 * @param refreshIntervalInSec
	 * @param jmxReporter
	 * @param consoleReporter
	 * @param slf4jReporer
	 */
	public CacheConfig(String name, String shortName, long expirationTimeInMs,
			int cacheMaxSize, boolean autoRefresh, String refreshTime,
			long refreshIntervalInSec, boolean jmxReporter,
			boolean consoleReporter, boolean slf4jReporer, int consolePeriodInSec, int slf4jPeriodInSec) {
		super("com.dx.cache", name);

		this.cacheName = name;
		this.shortName = shortName;
		this.expirationTimeInMs = expirationTimeInMs;
		this.cacheMaxSize = cacheMaxSize;
		this.autoRefresh = autoRefresh;
		this.refreshTime = refreshTime;
		this.refreshIntervalInSec = refreshIntervalInSec;
		this.enableStatsJmxReporter = jmxReporter;
		this.enableStatsConsoleReporter = consoleReporter;
		this.enableStatsSlf4jReporter = slf4jReporer;
		this.statsConsoleReportPeriodInSec = consolePeriodInSec;
		this.statsSlf4jReportPeriodInSec = slf4jPeriodInSec;
	}

	public long getExpirationTimeInMs() {
		return expirationTimeInMs;
	}

	public void setExpirationTimeInMs(long expirationTimeInMs) {
		this.expirationTimeInMs = expirationTimeInMs;
	}

	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	public String getRefreshTime() {
		return refreshTime;
	}

	public long getRefreshIntervalInSec() {
		return refreshIntervalInSec;
	}

	public void setRefreshIntervalInSec(long refreshIntervalInSec) {
		this.refreshIntervalInSec = refreshIntervalInSec;
	}

	public int getCacheMaxSize() {
		return cacheMaxSize;
	}

	public String getCacheName() {
		return this.cacheName;
	}

	public String getShortName() {
		return shortName;
	}

	public boolean isEnableStatsJmxReporter() {
		return enableStatsJmxReporter;
	}

	public boolean isEnableStatsConsoleReporter() {
		return enableStatsConsoleReporter;
	}

	public boolean isEnableStatsSlf4jReporter() {
		return enableStatsSlf4jReporter;
	}

	public int getStatsConsoleReportPeriodInSec() {
		return statsConsoleReportPeriodInSec;
	}

	public int getStatsSlf4jReportPeriodInSec() {
		return statsSlf4jReportPeriodInSec;
	}
	
	
}
