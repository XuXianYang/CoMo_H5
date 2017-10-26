package com.dianxian.redis;

/**
 * MBean interface for CacheConfig to enable it be manage-able by MBean server.
 * 
 */
public interface CacheConfigMBean {

	public String getCacheName();

	public String getShortName();

	public int getCacheMaxSize();

	public long getExpirationTimeInMs();

	public boolean isAutoRefresh();

	public String getRefreshTime();

	public long getRefreshIntervalInSec();

	public void setExpirationTimeInMs(long expirationTimeInMs);

	public boolean isEnableStatsJmxReporter();

	public boolean isEnableStatsConsoleReporter();

	public boolean isEnableStatsSlf4jReporter();
	
	public int getStatsConsoleReportPeriodInSec();

	public int getStatsSlf4jReportPeriodInSec();
}
