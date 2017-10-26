package com.dianxian.redis;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

;

/**
 * Cache activity statistics, Cache store implementation is suppsoed to provide
 * the running cache statistics.
 * 
 * It is powered by codahale metrics lib and is able to be monitored thru JMX
 * and
 * 
 */
public class CacheStatistics {

	public static interface CacheSizeProvider {
		int size();
	}

	private final String cacheName;

	private final MetricRegistry cacheMetrics;

	/**
	 * For remote cache, suggest to use getTimer, putTimer, removeTimer,
	 * clearTimer, For local cache, just use hitMeter, missMeter, putMeter,
	 * removeMeter, clearMeter.
	 */

	private final Timer getTimer;
	private final Meter getFailureMeter;
	private final Meter hitMeter;
	private final Meter missMeter;

	private final Timer putTimer;
	private final Meter putFailureMeter;
	private final Meter putMeter;

	private final Timer removeTimer;
	private final Meter removeFailureMeter;
	private final Meter removeMeter;

	private final Timer clearTimer;
	private final Meter clearFailureMeter;
	private final Meter clearMeter;

	private final Timer loadTimer;
	private final Meter loadFailureMeter;

	private final Timer refreshTimer;
	private final Meter refreshFailureMeter;
	
	private final Meter exceptionMeter;

	private final Gauge<Date> lastRefreshTimeGauge;
	private final Gauge<Date> lastClearTimeGauge;
	private final Gauge<Date> lastAccessTimeGauge;
	private final Gauge<Integer> cacheSizeGauge;

	private final CacheSizeProvider sizeProvider;

	private long lastAccessTime;
	private long lastRefreshTime;
	private long lastClearTime;

	public CacheStatistics(CacheConfig config) {
		this(config, null);
	}

	public CacheStatistics(CacheConfig config, CacheSizeProvider sizer) {

		this.sizeProvider = sizer;
		this.cacheName = config.getCacheName();

		// create metrics builder
		MetricsManager.Builder metricBuilder = MetricsManager.builder();
		if (config.isEnableStatsJmxReporter()) {
			metricBuilder.withJmxReporter(cacheName);
		}
		if (config.isEnableStatsConsoleReporter()) {
			metricBuilder
					.withConsoleReporter(
							config.getStatsConsoleReportPeriodInSec(),
							TimeUnit.SECONDS);
		}
		if (config.isEnableStatsSlf4jReporter()) {
			metricBuilder.withSLF4JReporter(
					config.getStatsSlf4jReportPeriodInSec(), TimeUnit.SECONDS);
			metricBuilder.withOutputTo(LoggerFactory
					.getLogger(CacheStatistics.class));
		}
		this.cacheMetrics = metricBuilder.build();

		this.getTimer = cacheMetrics.timer(name(cacheName, "get-timer"));
		this.getFailureMeter = cacheMetrics.meter(name(cacheName,
				"get-failure-meter"));
		this.hitMeter = cacheMetrics.meter(name(cacheName, "hit-meter"));
		this.missMeter = cacheMetrics.meter(name(cacheName, "miss-meter"));

		this.putTimer = cacheMetrics.timer(name(cacheName, "put-timer"));
		this.putFailureMeter = cacheMetrics.meter(name(cacheName,
				"put-failure-meter"));
		this.putMeter = cacheMetrics.meter(name(cacheName, "put-meter"));

		this.removeTimer = cacheMetrics.timer(name(cacheName, "remove-timer"));
		this.removeFailureMeter = cacheMetrics.meter(name(cacheName,
				"remove-failure-meter"));
		this.removeMeter = cacheMetrics.meter(name(cacheName, "remove-meter"));

		this.clearTimer = cacheMetrics.timer(name(cacheName, "clear-timer"));
		this.clearFailureMeter = cacheMetrics.meter(name(cacheName,
				"clear-failure-meter"));
		this.clearMeter = cacheMetrics.meter(name(cacheName, "clear-meter"));

		this.loadTimer = cacheMetrics.timer(name(cacheName, "load-timer"));
		this.loadFailureMeter = cacheMetrics.meter(name(cacheName,
				"load-failure-meter"));

		this.refreshTimer = cacheMetrics
				.timer(name(cacheName, "refresh-timer"));
		this.refreshFailureMeter = cacheMetrics.meter(name(cacheName,
				"refresh-failure-meter"));
		
		this.exceptionMeter = cacheMetrics.meter(name(cacheName,
				"all-exception-meter"));		

		this.lastRefreshTimeGauge = cacheMetrics.register(
				name(cacheName, "last-refresh-time"), new Gauge<Date>() {
					@Override
					public Date getValue() {
						return new Date(lastRefreshTime);
					}
				});
		this.lastClearTimeGauge = cacheMetrics.register(
				name(cacheName, "last-clear-time"), new Gauge<Date>() {
					@Override
					public Date getValue() {
						return new Date(lastClearTime);
					}
				});
		this.lastAccessTimeGauge = cacheMetrics.register(
				name(cacheName, "last-access-time"), new Gauge<Date>() {
					@Override
					public Date getValue() {
						return new Date(lastAccessTime);
					}
				});
		this.cacheSizeGauge = cacheMetrics.register(
				name(cacheName, "cache-size"), new Gauge<Integer>() {
					@Override
					public Integer getValue() {
						if (sizeProvider == null) {
							return -1; // don't know
						} else {
							return sizeProvider.size();
						}
					}
				});
		
		//
		buildCacheComponent();
	}

	private void buildCacheComponent() {
		String compName = CacheComponent.buildName(this.cacheName);
		if(ComponentRegistry.get(compName) == null){
			new CacheComponent(this.cacheName, this);
		}
	}

	public String getCacheName() {
		return this.cacheName;
	}

	public MetricRegistry getCacheMetrics() {
		return this.cacheMetrics;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// // Record all activities, actually it can be extracted as a separated
	// class for recording ....
	// ///////////////////////////////////////////////////////////////////////////////////////////////

	public Context getStart() {
		lastAccessTime = System.currentTimeMillis();
		return getTimer.time();
	}

	public void getDone(Context context, boolean isSuccess) {
		context.stop();
		if (!isSuccess) {
			getFailureMeter.mark();
			exceptionMeter.mark();
		}
	}

	public Context putStart() {
		lastAccessTime = System.currentTimeMillis();
		return putTimer.time();
	}

	public void putDone(Context context, boolean isSuccess) {
		context.stop();
		if (!isSuccess) {
			putFailureMeter.mark();
			exceptionMeter.mark();
		}
	}

	public Context removeStart() {
		lastAccessTime = System.currentTimeMillis();
		return removeTimer.time();
	}

	public void removeDone(Context context, boolean isSuccess) {
		context.stop();
		if (!isSuccess) {
			removeFailureMeter.mark();
			exceptionMeter.mark();
		}
	}

	public Context clearStart() {
		lastAccessTime = System.currentTimeMillis();
		return clearTimer.time();
	}

	public void clearDone(Context context, boolean isSuccess) {
		context.stop();
		if (!isSuccess) {
			clearFailureMeter.mark();
			exceptionMeter.mark();
		}
	}

	public void hit() {
		lastAccessTime = System.currentTimeMillis();
		hitMeter.mark();
	}

	public void miss() {
		lastAccessTime = System.currentTimeMillis();
		missMeter.mark();
	}

	public void put() {
		lastAccessTime = System.currentTimeMillis();
		putMeter.mark();
	}

	public void remove() {
		lastAccessTime = System.currentTimeMillis();
		removeMeter.mark();
	}

	public void clear() {
		lastAccessTime = System.currentTimeMillis();
		lastClearTime = lastAccessTime;
		clearMeter.mark();
	}

	public Context loadStart() {
		lastAccessTime = System.currentTimeMillis();
		return loadTimer.time();
	}

	public void loadDone(Context context, boolean isSuccess) {
		context.stop();
		if (!isSuccess) {
			loadFailureMeter.mark();
			exceptionMeter.mark();
		}
	}

	public Context refreshStart() {
		lastAccessTime = System.currentTimeMillis();
		lastRefreshTime = lastAccessTime;
		return refreshTimer.time();
	}

	public void refreshDone(Context context, boolean isSuccess) {
		context.stop();
		if (!isSuccess) {
			refreshFailureMeter.mark();
			exceptionMeter.mark();
		}
	}
	
	public void exception(){
		exceptionMeter.mark();
	}

	public Timer getGetTimer() {
		return getTimer;
	}

	public Meter getHitMeter() {
		return hitMeter;
	}

	public Meter getMissMeter() {
		return missMeter;
	}

	public Meter getGetFailureMeter() {
	    return this.getFailureMeter;
	}

	public Timer getPutTimer() {
		return putTimer;
	}

	public Meter getPutMeter() {
		return putMeter;
	}
	
    public Meter getPutFailureMeter() {
        return this.putFailureMeter;
    }

	public Timer getRemoveTimer() {
		return removeTimer;
	}

	public Meter getRemoveMeter() {
		return removeMeter;
	}

	public Meter getRemoveFailureMeter() {
        return this.removeFailureMeter;
    }

	public Timer getClearTimer() {
		return clearTimer;
	}

	public Meter getClearMeter() {
		return clearMeter;
	}

	public Timer getLoadTimer() {
		return loadTimer;
	}

	public Meter getLoadFailureMeter() {
		return loadFailureMeter;
	}

	public Timer getRefreshTimer() {
		return refreshTimer;
	}

	public Meter getRefreshFailureMeter() {
		return refreshFailureMeter;
	}
	
	public Meter getExceptionMeter(){
		return this.exceptionMeter;
	}

	public Gauge<Date> getLastRefreshTimeGauge() {
		return lastRefreshTimeGauge;
	}

	public Gauge<Date> getLastClearTimeGauge() {
		return lastClearTimeGauge;
	}

	public Gauge<Date> getLastAccessTimeGauge() {
		return lastAccessTimeGauge;
	}

	public Gauge<Integer> getCacheSizeGauge() {
		return cacheSizeGauge;
	}

	public CacheSizeProvider getSizeProvider() {
		return sizeProvider;
	}

	public Date getLastAccessTime() {
		return new Date(lastAccessTime);
	}

	public Date getLastRefreshTime() {
		return new Date(lastRefreshTime);
	}

	public Date getLastClearTime() {
		return new Date(lastClearTime);
	}
	
	public void stop() {
		MetricsManager.shutdown(this.cacheMetrics);
	}
}
