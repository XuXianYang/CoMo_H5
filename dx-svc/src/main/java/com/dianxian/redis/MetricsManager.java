package com.dianxian.redis;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;

import com.dianxian.redis.MetricsManager.Builder;

/**
 * 
 * The class is used to create MetricRegistry instance and manage its associated reporters. 
 * 
 * Please make sure to call MetricManager.shutdown() method to stop all registered scheduling reporters 
 * before your application exit.
 * 
 * JMX reporter: JmxReporter
 * Scheduling reporter: ConsoleReporter, Slf4jReporter
 * 
 */
public class MetricsManager {

	private final static HashMap<MetricRegistry, List<Closeable>> reporters = new HashMap<MetricRegistry, List<Closeable>>();

	public static class Builder {

		private MetricRegistry registry;

		private TimeUnit ratesTimeUnit;
		private TimeUnit durationTimeUnit;

		private boolean withJmxReporter;
		private String domain;

		private boolean withConsoleReporter;
		private long consolePeriod;
		private TimeUnit consolePeriodTimeUnit;
		private PrintStream outputTo;

		private boolean withSLF4JReporter;
		private long loggerPeriod;
		private TimeUnit loggerPeriodTimeUnit;
		private Logger logger;

		public Builder forRegistry(MetricRegistry registry) {
			this.registry = registry;
			return this;
		}

		public Builder withJmxReporter(String domain) {
			this.withJmxReporter = true;
			this.domain = domain;
			return this;
		}

		public Builder withConsoleReporter(long period, TimeUnit timeUnit) {
			this.withConsoleReporter = true;
			this.consolePeriod = period;
			this.consolePeriodTimeUnit = timeUnit;
			return this;
		}

		public Builder withSLF4JReporter(long period, TimeUnit timeUnit) {
			this.withSLF4JReporter = true;
			this.loggerPeriod = period;
			this.loggerPeriodTimeUnit = timeUnit;
			return this;
		}

		public Builder withOutputTo(Logger logger) {
			this.logger = logger;
			return this;
		}

		public Builder withOutputTo(PrintStream outputTo) {
			this.outputTo = outputTo;
			return this;
		}

		public Builder convertRatesTo(TimeUnit timeUnit) {
			this.ratesTimeUnit = timeUnit;
			return this;
		}

		public Builder convertDurationsTo(TimeUnit timeUnit) {
			this.durationTimeUnit = timeUnit;
			return this;
		}

		public MetricRegistry build() {
			if (registry == null) {
				registry = new MetricRegistry();
			}

			// associate it with JmxReporter
			if (withJmxReporter) {
				JmxReporter.Builder jb = JmxReporter
						.forRegistry(registry);
				if (domain != null) {
					jb.inDomain(domain);
				}
				if (ratesTimeUnit != null) {
					jb.convertRatesTo(ratesTimeUnit);
				}
				if (durationTimeUnit != null) {
					jb.convertDurationsTo(durationTimeUnit);
				}
				JmxReporter jmx = jb.build();
				jmx.start();

				// add it
				registerReport(registry, jmx);
			}

			// associate it with ConsoleReporter
			if (withConsoleReporter) {
				ConsoleReporter.Builder cb = ConsoleReporter
						.forRegistry(registry);
				if (outputTo != null) {
					cb.outputTo(outputTo);
				}
				if (ratesTimeUnit != null) {
					cb.convertRatesTo(ratesTimeUnit);
				}
				if (durationTimeUnit != null) {
					cb.convertDurationsTo(durationTimeUnit);
				}
				ConsoleReporter con = cb.build();
				con.start(consolePeriod, consolePeriodTimeUnit);
				
				//add it
				registerReport(registry, con);
			}

			// associate it with Slf4jReporter
			if (withSLF4JReporter) {
				Slf4jReporter.Builder lb = Slf4jReporter
						.forRegistry(registry);
				if (logger != null) {
					lb.outputTo(logger);
				}
				if (ratesTimeUnit != null) {
					lb.convertRatesTo(ratesTimeUnit);
				}
				if (durationTimeUnit != null) {
					lb.convertDurationsTo(durationTimeUnit);
				}
				Slf4jReporter slf = lb.build();
				slf.start(loggerPeriod, loggerPeriodTimeUnit);			
				//add it
				registerReport(registry, slf);
			}
			return registry;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static void registerReport(MetricRegistry registry, Closeable rpt) {
		synchronized (reporters) {
			List<Closeable> rpts = reporters.get(registry);
			if (rpts == null) {
				rpts = new LinkedList<Closeable>();
				reporters.put(registry, rpts);
			}
			rpts.add(rpt);
		}
	}
	
	public static void shutdown(MetricRegistry metrics) {
		if(metrics == null) {
			return;
		}
		
		List<Closeable> rpts = reporters.get(metrics);
		if(rpts != null) {
			for (Closeable rpt : rpts) {
				try {
					rpt.close();
				} catch (IOException e) {
					//
				}
			}
		}	
	}

	public static void shutdown() {
		List<List<Closeable>> all = new ArrayList<List<Closeable>>(reporters.values());
		for (List<Closeable> rpts : all) {
			for (Closeable rpt : rpts) {
				try {
					rpt.close();
				} catch (IOException e) {
					//
				}
			}
		}
		reporters.clear();
	}
}
