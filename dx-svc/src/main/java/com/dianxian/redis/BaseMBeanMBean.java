package com.dianxian.redis;

/**
 * This is a mark interface, to let the instance of any subclass of BaseMBean be able 
 * to be registered into MBServer.
 *
 * The actual MBean interface doesn't need to extend this interface.
 * 
 */
public interface BaseMBeanMBean {
	public String getMsg();
}
