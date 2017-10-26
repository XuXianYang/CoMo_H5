package com.dianxian.user.filter;


import java.util.HashMap;
import java.util.Map;

public class UserContextUtils {
	private ThreadLocal<UserContext> userRequestThread = new ThreadLocal<UserContext>();
	private ThreadLocal<Map<String, Object>> userParameter = new ThreadLocal<Map<String, Object>>();
	private static final UserContextUtils instance = new UserContextUtils();
	
	public static void setUserParameter(String key, Object value) {
		if(getInstance().userParameter.get() == null) {
			getInstance().userParameter.set(new HashMap<String, Object>());
		}
		Map<String, Object> map = getInstance().userParameter.get();
		map.put(key, value);
	}
	public static Object getUserParameter(String key) {
		if(getInstance().userParameter.get() == null) {
			getInstance().userParameter.set(new HashMap<String, Object>());
		}
		return getInstance().userParameter.get().get(key);
	}

	private static UserContextUtils getInstance() {
		return instance;
	}

	public final static UserContext getCurrentUserContext() {
		return getInstance().userRequestThread.get();
	}

	public final static void setCurrentUserContext(final UserContext userContext) {
		getInstance().userRequestThread.set(userContext);
	}

	public final static void removeCurrentUserContext() {
		getInstance().userRequestThread.remove();
		getInstance().userParameter.remove();
	}
}