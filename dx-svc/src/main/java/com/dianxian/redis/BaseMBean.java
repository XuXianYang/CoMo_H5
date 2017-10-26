package com.dianxian.redis;

import java.util.Date;
import java.util.Hashtable;

import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base MBean class, which carries the domain and name information.
 * 
 * In order to enable your MBean class be manage-able, the class should implement a MBean-compliant interface,
 * eg.
 * 
 * <code>
 * public interface MyTypeMBean {
 * }
 * 
 * public class MyType implements MyTypeMBean {
 * 	....
 * }
 * </code>
 *
 */
public class BaseMBean implements BaseMBeanMBean {

	public final static String KEY_TYPE = "type";
	public final static String KEY_NAME = "name";

	private String domain;
	private String type;
	private String name;
	private Date timeStamp;

	/**
	 * Not register to MBean server
	 * 
	 * @param domain normally it should be in format x.y.z, mustn't contain the special chars, like :,*#?
	 * @param name   normally it should be in format x.y.z, mustn't contain the special chars, like :,*#?
	 */
	protected BaseMBean(String domain, String name) {
		this(domain, name, false);
	}

	/**
	 * This MBean is automatically registered into MBean server
	 *  
	 * @param domain
	 * @param name
	 * @param register
	 */
	protected BaseMBean(String domain, String name, boolean register) {
		this.domain = domain;
		this.type = getClass().getName();
		this.name = name;
		this.timeStamp = new Date();

		if (register) {
			JmxManager.register(this);
		}
	}

	/**
	 * 
	 * Default ObjectName: <domain>:type=<type>,name=<name>
	 * 
	 * @return
	 */
	protected ObjectName getObjectName() {
		try {
			return ObjectName.getInstance(domain, getKeyProperties());
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(getClass());
			logger.warn("Failed to get ObjectName!", e);
			return null;
		}
	}

	protected Hashtable<String, String> getKeyProperties() {
		Hashtable<String, String> table = new Hashtable<String, String>();

		//table.put(KEY_TYPE, type);
		if (name != null) {
			table.put(KEY_NAME, name);
		}
		return table;
	}

	public String getDomain() {
		return domain;
	}

	public String getName() {
		return name;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public String getCanonicalName() {
		return getObjectName().getCanonicalName();
	}

	/**
	 * Dummy method to return warn message, if user doesn't define its MBean interface
	 */
	public String getMsg() {
		return "Please define your MBean interface compliant with JMX convention, like "
				+ type
				+ "MBean or "
				+ type
				+ "MXBean or implement DynamicMBean interface, otherwise you will see this message in JMX management console.";
	}
}
