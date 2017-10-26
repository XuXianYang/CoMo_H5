package com.dianxian.core.spring.mvc.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by XuWenHao on 8/1/2016.
 */
public abstract class IgnorableFilter extends GenericFilterBean {
	
	protected static final String TEMPLATE = "%s(%s)";
	protected static final String DEFAULT_CALLBANK_KEY_NAME = "jsoncallback";
	
    private String[] ignore = new String[0];

    public void setIgnore(String[] ignore) {
        this.ignore = ignore.clone();
    }

    protected boolean isIgnored(ServletRequest request) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        for (String pattern : ignore) {
            if (Pattern.compile(httpServletRequest.getContextPath() + pattern).matcher(requestURI).matches()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isRequestedByAjax(HttpServletRequest httpServletRequest) {
    	return "XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With")) || httpServletRequest.getParameter(DEFAULT_CALLBANK_KEY_NAME) != null;
    }
}
