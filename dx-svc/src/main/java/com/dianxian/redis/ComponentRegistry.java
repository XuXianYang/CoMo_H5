package com.dianxian.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentRegistry {

	private final static ConcurrentHashMap<String, Component> REGISTRY = new ConcurrentHashMap<String, Component>();

	public final static Component register(Component comp) {
		if (comp == null) {
			return null;
		}
		Component existing = REGISTRY.put(comp.getName(), comp);
		if (existing != null && existing != comp) {
			LoggerFactory.getLogger(ComponentRegistry.class).warn(
					"Replaced the existing registered component with name: "
							+ comp.getName());
		}
		return existing;
	}

	public final static Component unregister(String name) {
		if (name == null) {
			return null;
		}
		Component comp = REGISTRY.remove(name);
		if (comp == null) {
			LoggerFactory.getLogger(ComponentRegistry.class).warn(
					"No registered component with name: " + name);
		}
		return comp;
	}

	public final static Component get(String name) {
		if (name == null) {
			return null;
		}
		return REGISTRY.get(name);
	}

	public final static List<Component> getAll() {
		return new ArrayList<Component>(REGISTRY.values());
	}

	private static final Logger getLogger() {
		return LoggerFactory.getLogger(ComponentRegistry.class);
	}

	// ///////////////////////////////////////////////
	// / Component Properties
	// ///////////////////////////////////////////////

	private final static String PROPERTY_FILE = "/admin-internals.properties";
	private static volatile Properties properties = null;

	private static final Properties loadProperties() {
	    if (properties == null) {
	        synchronized (ComponentRegistry.class) {
	            if (properties == null) {
	                properties = doLoadProperties();
	            }
	        }
	    }
	    
	    return properties;
	}

	private static final Properties doLoadProperties() {
	    getLogger().info("begin to load " + PROPERTY_FILE);
	    Properties prop = new Properties();
	    InputStream in = null;
	    try {
	        
	           in = String.class.getResourceAsStream(PROPERTY_FILE);
	           if (in != null) {
	               getLogger().info(
	                   String.format("String load %s from %s.", PROPERTY_FILE, String.class.getResource(PROPERTY_FILE)));
	           } else {
	               in = ComponentRegistry.class.getResourceAsStream(PROPERTY_FILE);
	               if (in != null) {
	                   getLogger().info(
	                       String.format("Component load %s from %s.", PROPERTY_FILE, ComponentRegistry.class.getResource(PROPERTY_FILE)));
	               }
	           }
	           if (in != null) {
	               prop.load(in);
	           }
	            
	    } catch (Throwable e) {
	        getLogger().warn("Failed to load admin-internals.properties.", e);
	    } finally {
	        if (in != null) {
	            try {
	                in.close();
	            } catch (IOException e) {
	            }
	        }
	    }
	    return prop;
	}
	
	public static final String getProperty(String compName, String key) {
		return loadProperties().getProperty(compName + "." + key);
	}

	public static final int getIntProperty(String compName, String key,
			int defaultValue) {
		try {
			return Integer.parseInt(loadProperties()
					.getProperty(compName + "." + key));
		} catch (Throwable t) {
			return defaultValue;
		}
	}

	public static final HashMap<String, String> getAllProperties() {
		HashMap<String, String> props = new HashMap<String, String>();
		for (Object key : loadProperties().keySet()) {
			props.put(key.toString(), loadProperties().get(key).toString());
		}
		return props;
	}

}
