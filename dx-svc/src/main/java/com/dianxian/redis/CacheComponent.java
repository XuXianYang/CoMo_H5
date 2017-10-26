package com.dianxian.redis;

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Meter;

class CacheComponent extends Component {
	/**
	 * The component instance for all lufax data sources
	 */
	private static final Component allCaches = new Component(
			ComponentConstants.COMPONENT_CACHE,
			"Component for all managed caches in this application") {

		@Override
		public ComponentStatus getStatus() {
			ComponentStatus status = null;
			boolean hasError = false;
			boolean hasWarn = false;
			for (Component c : getContained()) {
				switch (c.getStatus()) {
				case Error:
					hasError = true;
					break;
				case Warn:
					hasWarn = true;
					break;
				default:
					break;
				}
			}
			if (hasError) {
				status = ComponentStatus.Error;
			} else if (hasWarn) {
				status = ComponentStatus.Warn;
			} else {
				status = ComponentStatus.Good;
			}

			return status;
		}

		@Override
		public Map<String, String> getStats() {
			Map<String, String> stats = super.getStats();
			stats.put("count", String.valueOf(getContained().size()));

			return stats;
		}
	};

	/**
	 * Friendly method for building component name
	 * 
	 * @param url
	 * @param schema
	 * @return
	 */
	static String buildName(String cacheName) {
		return ComponentConstants.COMPONENT_CACHE + "." + cacheName;
	}

	/**
	 * Friendly method for building component description
	 * 
	 * @param url
	 * @param schema
	 * @return
	 */
	static String buildDescription(String cacheName) {
		return "Cache Component for cacheName: " + cacheName;
	}

	// //////////////////////////////////////////////

	public static final String WARN_MIN_M1_EXCEPTION = "warn-min-m1-exception";
	public static final String WARN_MIN_M5_EXCEPTION = "warn-min-m5-exception";
	public static final String ERROR_MIN_M1_EXCEPTION = "error-min-m1-exception";
	public static final String ERROR_MIN_M5_EXCEPTION = "error-min-m5-exception";

	private final int warnMinM1;
	private final int warnMinM5;
	private final int errorMinM1;
	private final int errorMinM5;

	private final Meter exceptionMeter;

	protected CacheComponent(String cacheName, CacheStatistics cacheStats) {
		super(buildName(cacheName), buildDescription(cacheName));
		exceptionMeter = cacheStats.getExceptionMeter();

		warnMinM1 = ComponentRegistry.getIntProperty(
				ComponentConstants.COMPONENT_CACHE, WARN_MIN_M1_EXCEPTION, 5);
		warnMinM5 = ComponentRegistry.getIntProperty(
				ComponentConstants.COMPONENT_CACHE, WARN_MIN_M5_EXCEPTION, 10);
		errorMinM1 = ComponentRegistry.getIntProperty(
				ComponentConstants.COMPONENT_CACHE, ERROR_MIN_M1_EXCEPTION, 10);
		errorMinM5 = ComponentRegistry.getIntProperty(
				ComponentConstants.COMPONENT_CACHE, ERROR_MIN_M5_EXCEPTION, 20);

		// Registered as contained component of all caches component
		allCaches.addContainedComponent(this);
	}

	@Override
	public ComponentStatus getStatus() {
		double ce1 = exceptionMeter.getOneMinuteRate() * 60;
		double ce5 = exceptionMeter.getFiveMinuteRate() * 60 * 5;

		if (ce1 > errorMinM1 || ce5 > errorMinM5) {
			return ComponentStatus.Error;
		} else if (ce1 > warnMinM1 || ce5 > warnMinM5) {
			return ComponentStatus.Warn;
		} else {
			return ComponentStatus.Good;
		}
	}

	@Override
	public Map<String, String> getStats() {
		Map<String, String> stats = super.getStats();
		stats.putAll(buildStats());
		return stats;
	}

	public Map<String, String> buildStats() {
		Map<String, String> stats = new HashMap<String, String>();

		stats.put("exception-count",
				String.valueOf(exceptionMeter.getCount()));
		stats.put("exception-m1rate",
				String.valueOf(exceptionMeter.getOneMinuteRate()));
		stats.put("exception-m5rate",
				String.valueOf(exceptionMeter.getFiveMinuteRate()));
		stats.put("exception-m15rate",
				String.valueOf(exceptionMeter.getFifteenMinuteRate()));

		return stats;
	}
}
