package com.dianxian.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class Component {

	/**
	 * Component name, it should be unique among registered component, this name
	 * format is recommended to be com.lufax.<component category>.<other
	 * key-attr> eg.
	 * 
	 * for all database component, the name is com.lufax.database
	 * 
	 * for given database and schema, the name is com.lufax.database.ies.trdopr
	 * 
	 */
	private String name;

	/**
	 * Component description
	 */
	private String description;

	/**
	 * Other statistics for this component, key-value pair
	 */
	private Map<String, String> stats;

	/**
	 * The list of contained components
	 */
	private List<Component> contained;

	/**
	 * 
	 * @param name
	 * @param description
	 */
	protected Component(String name, String description) {
		this(name, description, true);
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param autoRegister
	 *            true, auto register itself to component registry
	 */
	protected Component(String name, String description, boolean autoRegister) {
		this.name = name;
		this.description = description;
		this.stats = new HashMap<String, String>();
		this.contained = new LinkedList<Component>();

		if (autoRegister) {
			ComponentRegistry.register(this);
		}
	}

	/**
	 * The sub-class should implement this method to return the proper status.
	 * 
	 * @return
	 */
	public abstract ComponentStatus getStatus();

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, String> getStats() {
		return new HashMap<String, String>(stats);
	}

	public String getStatsItem(String key) {
		return getStats().get(key);
	}

	public synchronized void addStatsItem(String key, String value) {
		stats.put(key, value);
	}

	public synchronized void addStats(Map<String, String> stats) {
		if (stats != null) {
			this.stats.putAll(stats);
		}
	}

	public synchronized void clearStats() {
		stats.clear();
	}

	public List<Component> getContained() {
		return new ArrayList<Component>(contained);
	}

	public synchronized void addContainedComponent(Component comp) {
		if (comp == null) {
			return;
		}

		String subname = comp.getName();
		for (Component c : contained) {
			if (c == comp) {
				// Already added
				return;
			}

			if (subname.equals(c.getName())) {
				// name should be unique, only one component with the same name
				// can be registered
				getLogger()
						.warn("Component[name="
								+ this.name
								+ "] has already contained sub component with name: "
								+ subname);
				return;
			}
		}
		// add it to contained
		this.contained.add(comp);
	}

	public synchronized Component removeContainedComponent(String subname) {
		if (subname == null) {
			return null;
		}

		Component removed = null;
		for (int i = 0, size = contained.size(); i < size; i++) {
			if (subname.equals(contained.get(i).getName())) {
				removed = contained.remove(i);
				break;
			}
		}
		return removed;
	}

	public String toJson() {
		return new GsonBuilder().create().toJson(toJsonObject());
	}

	public JsonObject toJsonObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", name);
		obj.addProperty("description", description);
		obj.addProperty("status", getStatus() == null ? null : getStatus()
				.name());

		JsonArray jstats = new JsonArray();
		obj.add("stats", jstats);
		// order by name
		TreeMap<String, String> orderedStats = new TreeMap<String, String>(
				getStats());
		for (Entry<String, String> e : orderedStats.entrySet()) {
			JsonObject jo = new JsonObject();
			jo.addProperty(e.getKey(), e.getValue());
			jstats.add(jo);
		}

		JsonArray jcontained = new JsonArray();
		obj.add("contained", jcontained);
		// order by name
		TreeMap<String, JsonObject> orderedContained = new TreeMap<String, JsonObject>();
		for (Component c : getContained()) {
			orderedContained.put(c.getName(), c.toJsonObject());
		}
		for (JsonObject o : orderedContained.values()) {
			jcontained.add(o);
		}
		return obj;
	}

	/**
	 * Friendly method for register itself
	 */
	public void register() {
		if (ComponentRegistry.get(this.name) != null) {
			ComponentRegistry.register(this);
		}
	}

	/**
	 * Friendly method for register itself and its contained components
	 */
	public void registerAll() {
		register();
		for (Component c : contained) {
			c.register();
		}
	}

	/**
	 * Friendly method for unregister itself
	 */
	public void unregister() {
		if (ComponentRegistry.get(this.name) != null) {
			ComponentRegistry.unregister(this.name);
		}
	}

	/**
	 * Friendly method for unregister itself and its contained components
	 */
	public void unregisterAll() {
		unregister();
		for (Component c : contained) {
			c.unregister();
		}
	}

	protected Logger getLogger() {
		return LoggerFactory.getLogger(Component.class);
	}
}
