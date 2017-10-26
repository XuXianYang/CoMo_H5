package com.dianxian.user.filter;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JerseyRequestParam {
	public JerseyRequestParam() {
	}

	private static final String DEFAULT_METHOD = "GET";
	private Map<String, String[]> parameters = new HashMap<String, String[]>();
	private Map<String, String> headers = new HashMap<String, String>();
	private List<Cookie> cookies = new ArrayList<Cookie>();
	private String methodName = DEFAULT_METHOD;
	
	
	public List<Cookie> getCookies() {
		return cookies;
	}
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}
	public Map<String, String[]> getParameters() {
		return parameters;
	}
	public void setMultiParameter(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}
    public void setParameters(Map<String, String> parameters) {
		Map<String, String[]> map = new HashMap<String, String[]>();
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            map.put(entry.getKey(), new String[]{entry.getValue()});
        }
        this.parameters = map;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
