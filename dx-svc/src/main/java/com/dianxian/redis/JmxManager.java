package com.dianxian.redis;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmxManager {

	private final static Logger logger = LoggerFactory
			.getLogger(JmxManager.class);

	private final static HashMap<String, BaseMBean> registerBeans = new HashMap<String, BaseMBean>();

	/**
	 * Register a MBean, make sure your bean has implemented a JMX compliant
	 * MBean interface.
	 * 
	 * @param bean
	 * @throws InitializationException
	 */
	public static final void register(BaseMBean bean)
			throws InitializationException {
		ObjectName name = bean.getObjectName();
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.registerMBean(bean, name);
			// put it
			registerBeans.put(bean.getCanonicalName(), bean);
		} catch (Throwable e) {
			logger.warn("Failed to register bean: " + name, e);
		}
	}

	/**
	 * Unregister the managed bean
	 * 
	 * @param bean
	 * @throws InitializationException
	 */
	public static final void unregister(BaseMBean bean)
			throws InitializationException {
		ObjectName name = bean.getObjectName();
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.unregisterMBean(name);

			// remove it
			registerBeans.remove(bean.getCanonicalName());
		} catch (Throwable e) {
			logger.warn("Failed to unregister bean: " + name, e);
		}
	}

	public static final MBeanInfo getJmxBean(String objectName) {
		if (objectName == null) {
			return null;
		}
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			MBeanInfo mbi = mbs
					.getMBeanInfo(ObjectName.getInstance(objectName));

			return mbi;
		} catch (Throwable e) {
			logger.warn("Failed to unregister bean: " + objectName, e);
		}
		return null;
	}

	public static final Object getJmxBeanAttribute(String objectName,
			String attribute) {
		if (objectName == null || attribute == null) {
			return null;
		}
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			return mbs.getAttribute(ObjectName.getInstance(objectName),
					attribute);
		} catch (Throwable e) {
			logger.warn("Failed to unregister bean: " + objectName, e);
		}
		return null;
	}

	/**
	 * Unregister all managed beans from MBean server
	 * 
	 */
	public static void shutdown() {
		List<BaseMBean> allBeans = new ArrayList<BaseMBean>(
				registerBeans.values());
		for (BaseMBean b : allBeans) {
			unregister(b);
		}
		registerBeans.clear();
	}
}
